package org.dhis2.form.ui

import android.media.MediaMetadataRetriever
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.dhis2.commons.dialogs.media.DialogMediaEntity
import org.dhis2.commons.dialogs.media.DialogMediaType
import org.dhis2.commons.viewmodel.DispatcherProvider
import org.dhis2.form.R
import org.dhis2.form.data.DataIntegrityCheckResult
import org.dhis2.form.data.FormRepository
import org.dhis2.form.data.GeometryController
import org.dhis2.form.data.GeometryParserImpl
import org.dhis2.form.data.RulesUtilsProviderConfigurationError
import org.dhis2.form.data.media.MediaDetails
import org.dhis2.form.model.ActionType
import org.dhis2.form.model.FieldUiModel
import org.dhis2.form.model.InfoUiModel
import org.dhis2.form.model.RowAction
import org.dhis2.form.model.StoreResult
import org.dhis2.form.model.UiRenderType
import org.dhis2.form.model.ValueStoreResult
import org.dhis2.form.ui.binding.getFeatureType
import org.dhis2.form.ui.event.RecyclerViewUiEvents
import org.dhis2.form.ui.idling.FormCountingIdlingResource
import org.dhis2.form.ui.intent.FormIntent
import org.dhis2.form.ui.validation.validators.FieldMaskValidator
import org.dhis2.usescases.uiboost.data.model.media.Audio
import org.dhis2.usescases.uiboost.data.model.media.DataElement
import org.dhis2.usescases.uiboost.data.model.media.MediaStoreConfig
import org.dhis2.usescases.uiboost.data.model.media.Video
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.ValueType
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

private const val DIRECTORY_DOWNLOAD_DHS2 = "dhis2"

class FormViewModel(
    private val repository: FormRepository,
    private val dispatcher: DispatcherProvider,
    private val geometryController: GeometryController = GeometryController(GeometryParserImpl()),
    private val openErrorLocation: Boolean = false,
) : ViewModel() {

    val loading = MutableLiveData(true)
    val showToast = MutableLiveData<Int>()
    val focused = MutableLiveData<Boolean>()
    val showInfo = MutableLiveData<InfoUiModel>()
    val confError = MutableLiveData<List<RulesUtilsProviderConfigurationError>>()

    private val _items = MutableLiveData<List<FieldUiModel>>()
    val items: LiveData<List<FieldUiModel>> = _items

    private val _savedValue = MutableLiveData<RowAction>()
    val savedValue: LiveData<RowAction> = _savedValue

    private val _queryData = MutableLiveData<RowAction>()
    val queryData = _queryData

    private val _dataIntegrityResult = MutableLiveData<DataIntegrityCheckResult>()
    val dataIntegrityResult = _dataIntegrityResult

    private val _completionPercentage = MutableLiveData<Float>()
    val completionPercentage = _completionPercentage

    private val _calculationLoop = MutableLiveData(false)
    val calculationLoop = _calculationLoop

    private val _pendingIntents = MutableSharedFlow<FormIntent>()

    private val _mediaDataStore = MutableStateFlow<MediaStoreConfig?>(null)
    val mediaDataStore: StateFlow<MediaStoreConfig?> = _mediaDataStore

    private val _isLoadingMedia = MutableStateFlow(false)
    val isLoadingMedia = _isLoadingMedia.asStateFlow()

    private val _mediaEntities = MutableStateFlow(mutableListOf<DialogMediaEntity>())
    val mediaEntities = _mediaEntities.asStateFlow()

    init {
        viewModelScope.launch {
            _pendingIntents
                .distinctUntilChanged { old, new ->
                    if (old is FormIntent.OnFinish && new is FormIntent.OnFinish) {
                        false
                    } else {
                        old == new
                    }
                }
                .map { intent -> createRowActionStore(intent) }
                .flowOn(dispatcher.io())
                .collect { result -> displayResult(result) }
        }
        loadData()
    }

    private fun displayResult(result: Pair<RowAction, StoreResult>) {
        result.second.valueStoreResult?.let {
            when (it) {
                ValueStoreResult.VALUE_CHANGED -> {
                    result.first.let {
                        _savedValue.value = it
                    }
                    processCalculatedItems()
                }

                ValueStoreResult.ERROR_UPDATING_VALUE -> {
                    loading.postValue(false)
                    showToast.value = R.string.update_field_error
                    processCalculatedItems(true)
                }

                ValueStoreResult.UID_IS_NOT_DE_OR_ATTR -> {
                    Timber.tag(TAG).d("${result.first.id} is not a data element or attribute")
                    processCalculatedItems()
                }

                ValueStoreResult.VALUE_NOT_UNIQUE -> {
                    showInfo.value = InfoUiModel(
                        R.string.error,
                        R.string.unique_warning
                    )
                    processCalculatedItems()
                }

                ValueStoreResult.VALUE_HAS_NOT_CHANGED -> {
                    processCalculatedItems(true)
                }

                ValueStoreResult.TEXT_CHANGING -> {
                    result.first.let {
                        Timber.d("${result.first.id} is changing its value")
                        _queryData.value = it
                    }
                }

                ValueStoreResult.FINISH -> {
                    processCalculatedItems(finish = true)
                }

                ValueStoreResult.FILE_SAVED -> {
                    /*Do nothing*/
                }
            }
        }
    }

    fun submitIntent(intent: FormIntent) {
        viewModelScope.launch {
            _pendingIntents.emit(intent)
        }
    }

    private fun createRowActionStore(it: FormIntent): Pair<RowAction, StoreResult> {
        val rowAction = rowActionFromIntent(it)

        if (rowAction.type == ActionType.ON_FOCUS) {
            focused.postValue(true)
        } else if (rowAction.type == ActionType.ON_SAVE) {
            loading.postValue(true)
        }

        val result = processUserAction(rowAction)
        return Pair(rowAction, result)
    }

    private fun processUserAction(action: RowAction): StoreResult {
        return when (action.type) {
            ActionType.ON_SAVE -> {
                if (action.valueType == ValueType.COORDINATE) {
                    repository.setFieldRequestingCoordinates(action.id, false)
                }
                repository.updateErrorList(action)
                if (action.error != null) {
                    StoreResult(
                        action.id,
                        ValueStoreResult.VALUE_HAS_NOT_CHANGED
                    )
                } else {
                    val saveResult = repository.save(action.id, action.value, action.extraData)
                    if (saveResult?.valueStoreResult != ValueStoreResult.ERROR_UPDATING_VALUE) {
                        repository.updateValueOnList(action.id, action.value, action.valueType)
                    } else {
                        repository.updateErrorList(
                            action.copy(
                                error = Throwable(saveResult.valueStoreResultMessage)
                            )
                        )
                    }
                    saveResult ?: StoreResult(
                        action.id,
                        ValueStoreResult.VALUE_CHANGED
                    )
                }
            }

            ActionType.ON_FOCUS, ActionType.ON_NEXT -> {
                val storeResult = saveLastFocusedItem(action)
                repository.setFocusedItem(action)
                storeResult
            }

            ActionType.ON_TEXT_CHANGE -> {
                repository.updateValueOnList(action.id, action.value, action.valueType)
                StoreResult(
                    action.id,
                    ValueStoreResult.TEXT_CHANGING
                )
            }

            ActionType.ON_SECTION_CHANGE -> {
                repository.updateSectionOpened(action)
                StoreResult(
                    action.id,
                    ValueStoreResult.VALUE_HAS_NOT_CHANGED
                )
            }

            ActionType.ON_CLEAR -> {
                repository.removeAllValues()
                StoreResult(
                    action.id,
                    ValueStoreResult.VALUE_CHANGED
                )
            }

            ActionType.ON_FINISH -> {
                repository.setFocusedItem(action)
                StoreResult(
                    "",
                    ValueStoreResult.FINISH
                )
            }

            ActionType.ON_REQUEST_COORDINATES -> {
                repository.setFieldRequestingCoordinates(action.id, true)
                StoreResult(
                    action.id,
                    ValueStoreResult.VALUE_HAS_NOT_CHANGED
                )
            }

            ActionType.ON_CANCELL_REQUEST_COORDINATES -> {
                repository.setFieldRequestingCoordinates(action.id, false)
                StoreResult(
                    action.id,
                    ValueStoreResult.VALUE_HAS_NOT_CHANGED
                )
            }

            ActionType.ON_STORE_FILE -> {
                val saveResult = repository.storeFile(action.id, action.value)
                when (saveResult?.valueStoreResult) {
                    ValueStoreResult.FILE_SAVED -> {
                        processUserAction(
                            rowActionFromIntent(
                                FormIntent.OnSave(
                                    uid = action.id,
                                    value = saveResult.uid,
                                    valueType = action.valueType
                                )
                            )
                        )
                    }

                    null -> StoreResult(
                        action.id,
                        ValueStoreResult.VALUE_HAS_NOT_CHANGED
                    )

                    else -> saveResult
                }
            }
        }
    }

    private fun saveLastFocusedItem(rowAction: RowAction) = getLastFocusedTextItem()?.let {
        val error = checkFieldError(it.valueType, it.value, it.fieldMask)
        if (error != null) {
            val action = rowActionFromIntent(
                FormIntent.OnSave(it.uid, it.value, it.valueType, it.fieldMask)
            )
            repository.updateErrorList(action)
            StoreResult(
                rowAction.id,
                ValueStoreResult.VALUE_HAS_NOT_CHANGED
            )
        } else {
            val intent = getSaveIntent(it)
            val action = rowActionFromIntent(intent)
            val result = repository.save(it.uid, it.value, action.extraData)
            repository.updateValueOnList(it.uid, it.value, it.valueType)
            repository.updateErrorList(action)
            result
        }
    } ?: StoreResult(
        rowAction.id,
        ValueStoreResult.VALUE_HAS_NOT_CHANGED
    )

    fun valueTypeIsTextField(valueType: ValueType?, renderType: UiRenderType? = null): Boolean {
        return if (valueType == null) {
            false
        } else {
            valueType.isNumeric ||
                    valueType.isText && renderType?.isPolygon() != true ||
                    valueType == ValueType.URL ||
                    valueType == ValueType.EMAIL ||
                    valueType == ValueType.PHONE_NUMBER
        }
    }

    private fun getLastFocusedTextItem() = repository.currentFocusedItem()?.takeIf {
        it.optionSet == null && valueTypeIsTextField(it.valueType, it.renderingType)
    }

    private fun getSaveIntent(field: FieldUiModel) = when (field.valueType) {
        ValueType.COORDINATE -> FormIntent.SaveCurrentLocation(
            field.uid,
            field.value,
            getFeatureType(field.renderingType).name
        )

        else -> FormIntent.OnSave(field.uid, field.value, field.valueType, field.fieldMask)
    }

    private fun rowActionFromIntent(intent: FormIntent): RowAction {
        return when (intent) {
            is FormIntent.OnClear -> createRowAction(
                uid = "",
                value = null,
                actionType = ActionType.ON_CLEAR
            )

            is FormIntent.ClearValue -> createRowAction(intent.uid, null)
            is FormIntent.SelectLocationFromCoordinates -> {
                val error = checkFieldError(
                    ValueType.COORDINATE,
                    intent.coordinates,
                    null
                )
                createRowAction(
                    uid = intent.uid,
                    value = intent.coordinates,
                    extraData = intent.extraData,
                    error = error,
                    valueType = ValueType.COORDINATE
                )
            }

            is FormIntent.SelectLocationFromMap -> setCoordinateFieldValue(
                fieldUid = intent.uid,
                featureType = intent.featureType,
                coordinates = intent.coordinates
            )

            is FormIntent.SaveCurrentLocation -> {
                val error = checkFieldError(
                    ValueType.COORDINATE,
                    intent.value,
                    null
                )
                createRowAction(
                    uid = intent.uid,
                    value = intent.value,
                    extraData = intent.featureType,
                    error = error,
                    valueType = ValueType.COORDINATE
                )
            }

            is FormIntent.OnNext -> createRowAction(
                uid = intent.uid,
                value = intent.value,
                actionType = ActionType.ON_NEXT
            )

            is FormIntent.OnSave -> {
                val error = checkFieldError(
                    intent.valueType,
                    intent.value,
                    intent.fieldMask
                )

                createRowAction(
                    uid = intent.uid,
                    value = intent.value,
                    error = error,
                    valueType = intent.valueType
                )
            }

            is FormIntent.OnFocus -> createRowAction(
                uid = intent.uid,
                value = intent.value,
                actionType = ActionType.ON_FOCUS
            )

            is FormIntent.OnTextChange -> createRowAction(
                uid = intent.uid,
                value = intent.value,
                actionType = ActionType.ON_TEXT_CHANGE,
                valueType = intent.valueType
            )

            is FormIntent.OnSection -> createRowAction(
                uid = intent.sectionUid,
                value = null,
                actionType = ActionType.ON_SECTION_CHANGE
            )

            is FormIntent.OnFinish -> createRowAction(
                uid = "",
                value = null,
                actionType = ActionType.ON_FINISH
            )

            is FormIntent.OnRequestCoordinates ->
                createRowAction(
                    uid = intent.uid,
                    value = null,
                    actionType = ActionType.ON_REQUEST_COORDINATES
                )

            is FormIntent.OnCancelRequestCoordinates ->
                createRowAction(
                    uid = intent.uid,
                    value = null,
                    actionType = ActionType.ON_CANCELL_REQUEST_COORDINATES
                )

            is FormIntent.OnStoreFile ->
                createRowAction(
                    uid = intent.uid,
                    value = intent.filePath,
                    actionType = ActionType.ON_STORE_FILE,
                    valueType = intent.valueType
                )
        }
    }

    private fun checkFieldError(
        valueType: ValueType?,
        fieldValue: String?,
        fieldMask: String?,
    ): Throwable? {
        if (fieldValue.isNullOrEmpty()) {
            return null
        }

        return fieldValue.let { value ->
            var error =
                when (
                    val result = valueType?.validator?.validate(value)
                ) {
                    is Result.Failure -> result.failure
                    else -> null
                }

            fieldMask?.let { mask ->
                error = when (val result = FieldMaskValidator(mask).validate(value)) {
                    is Result.Failure -> result.failure
                    else -> error
                }
            }
            error
        }
    }

    private fun createRowAction(
        uid: String,
        value: String?,
        extraData: String? = null,
        error: Throwable? = null,
        actionType: ActionType = ActionType.ON_SAVE,
        valueType: ValueType? = null,
    ) = RowAction(
        id = uid,
        value = value,
        extraData = extraData,
        error = error,
        type = actionType,
        valueType = valueType
    )

    fun onItemsRendered() {
        loading.value = false
    }

    private fun setCoordinateFieldValue(
        fieldUid: String,
        featureType: String,
        coordinates: String?,
    ): RowAction {
        val type = FeatureType.valueOf(featureType)
        val geometryCoordinates = coordinates?.let {
            geometryController.generateLocationFromCoordinates(
                type,
                coordinates
            )?.coordinates()
        }

        val error = if (type == FeatureType.POINT) {
            checkFieldError(ValueType.COORDINATE, geometryCoordinates, null)
        } else {
            null
        }

        return createRowAction(
            uid = fieldUid,
            value = geometryCoordinates,
            extraData = featureType,
            error = error,
            valueType = ValueType.COORDINATE
        )
    }

    fun getFocusedItemUid(): String? {
        return items.value?.first { it.focused }?.uid
    }

    private fun processCalculatedItems(skipProgramRules: Boolean = false, finish: Boolean = false) {
        FormCountingIdlingResource.increment()
        viewModelScope.launch(dispatcher.io()) {
            val result = async {
                repository.composeList(skipProgramRules)
            }
            _items.postValue(result.await())
            if (finish) {
                runDataIntegrityCheck()
            }
        }
    }

    fun updateConfigurationErrors() {
        confError.value = repository.getConfigurationErrors() ?: emptyList()
    }

    fun runDataIntegrityCheck(backButtonPressed: Boolean? = null) {
        viewModelScope.launch {
            val result = async(dispatcher.io()) {
                repository.runDataIntegrityCheck(allowDiscard = backButtonPressed ?: false)
            }
            try {
                _dataIntegrityResult.postValue(result.await())
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                _items.postValue(repository.composeList())
            }
        }
    }

    fun calculateCompletedFields() {
        viewModelScope.launch {
            val result = async(dispatcher.io()) {
                repository.completedFieldsPercentage(_items.value ?: emptyList())
            }
            try {
                _completionPercentage.postValue(result.await())
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun displayLoopWarningIfNeeded() {
        viewModelScope.launch {
            val result = async(dispatcher.io()) {
                repository.calculationLoopOverLimit()
            }
            try {
                _calculationLoop.postValue(result.await())
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun discardChanges() {
        repository.backupOfChangedItems().forEach {
            submitIntent(FormIntent.OnSave(it.uid, it.value, it.valueType, it.fieldMask))
        }
    }

    fun saveDataEntry() {
        getLastFocusedTextItem()?.let {
            submitIntent(getSaveIntent(it))
        }
        submitIntent(FormIntent.OnFinish())
    }

    fun loadData() {
        loading.postValue(true)
        viewModelScope.launch(dispatcher.io()) {
            val result = async {
                repository.fetchFormItems(openErrorLocation)
            }
            try {
                _items.postValue(result.await())
            } catch (e: Exception) {
                Timber.e(e)
                _items.postValue(emptyList())
            }
        }
    }

    fun clearFocus() {
        repository.clearFocusItem()
    }

    fun getUpdatedData(uiEvent: RecyclerViewUiEvents.OpenChooserIntent): RowAction {
        val currentField = queryData.value
        return when (currentField?.id) {
            uiEvent.uid -> currentField.copy(
                type = ActionType.ON_SAVE,
                error = checkFieldError(
                    currentField.valueType,
                    currentField.value,
                    null
                )
            )

            else -> RowAction(
                id = uiEvent.uid,
                value = uiEvent.value,
                type = ActionType.ON_SAVE
            )
        }
    }

    companion object {
        const val TAG = "FormViewModel"
    }

    fun getMediaDataStore() {
        viewModelScope.launch {
            repository.getMediaDataStore().collectLatest {
                _mediaDataStore.value = it
                Timber.d("$it")
            }
        }
    }

    private fun getFileExtension(responseBody: ResponseBody): String? {
        val contentType = responseBody.contentType()
        if (contentType != null) {
            val mediaContentType = contentType.toString()
            Timber.d("mediaType: $mediaContentType")
            val parts = mediaContentType.split(";")
            val mediaType = parts[0].trim()

            val extensionStart = mediaType.lastIndexOf('/')
            if (extensionStart != -1 && extensionStart < mediaType.length - 1) {
                return mediaType.substring(extensionStart + 1)
            }
        }
        return null
    }

    private fun loadLocalMediaPath(uid: String): String? {
        return getLocalMediaPath2(uid)
    }

    suspend fun loadMedias(
        videos: List<Video> = emptyList(),
        audios: List<Audio> = emptyList(),
    ) {
        videos.map { video ->

            val mediaFilePath = loadLocalMediaPath(video.id)

            val mediaAlreadyDownloaded =
                !mediaFilePath.isNullOrBlank() && mediaFilePath.isNotEmpty()

            if (mediaAlreadyDownloaded) {
                Timber.d("Local media exist: [true]")
            } else {
                Timber.d("Local media exist: [false]")
                Timber.d("Download media file: [true]")
                downloadMedia(video.id)
            }
        }

        audios.map { audio ->

            val mediaFilePath = loadLocalMediaPath(audio.id)

            val mediaAlreadyDownloaded =
                !mediaFilePath.isNullOrBlank() && mediaFilePath.isNotEmpty()

            if (mediaAlreadyDownloaded) {
                Timber.d("Local media exist: [true]")
            } else {
                Timber.d("Local media exist: [false]")
                Timber.d("Download media file: [true]")
                downloadMedia(audio.id)
            }
        }
        loadAllMediaPaths(videos = videos, audios = audios)
    }

    private suspend fun downloadMedia(uid: String) {
        try {
            Timber.d("Start downloading the media with uid [$uid]!")

            val body = repository.downloadMediaToLocal(uid = uid)

            if (body != null) {
                val fileExtension = getFileExtension(body)
                val directory = createDownloadDirectory(DIRECTORY_DOWNLOAD_DHS2)
                val file = createFile(directory, uid, fileExtension!!)

                val mediaPath = createMediaFilePath(DIRECTORY_DOWNLOAD_DHS2, uid, fileExtension)

                saveMediaToFile(body, file)

                Timber.d("Media with uid [$uid] downloaded!")
                Timber.d("Media path: [${mediaPath}]")
            } else {
                Timber.d("Null response on download media with uid: [$uid]")
            }
        } catch (ex: Exception) {
            Timber.d("Download error on media with uid [$uid]!")
            ex.printStackTrace()
        }
    }

    private fun saveMediaToFile(body: ResponseBody, file: File) {
        val outputStream = FileOutputStream(file)
        val buffer = ByteArray(4096)
        var bytesRead: Int

        val inputStream = body.byteStream()
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }

        outputStream.close()
        inputStream.close()
    }

    private fun createMediaFilePath(directory: String, uid: String, fileExtension: String): String {
        return "$directory/$uid.$fileExtension"
    }

    private fun createDownloadDirectory(directoryName: String): File {
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            directoryName
        )
        directory.mkdirs()
        return directory
    }

    private fun createFile(directory: File, uid: String, fileExtension: String): File {
        return File(directory, "$uid.$fileExtension")
    }

    private fun getLocalMediaPath2(uid: String): String? {
        var path: String? = null

        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "dhis2"
        )
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isFile && file.nameWithoutExtension == uid) {
                    path = file.absolutePath
                    break
                }
            }
        } else {
            Timber.d("Directory does not exist or cannot be accessed.")
        }
        return path
    }

    private fun loadAllMediaPaths(
        videos: List<Video> = emptyList(),
        audios: List<Audio> = emptyList(),
    ) {
        val mediaEntitiesList = mutableListOf<DialogMediaEntity>()

        videos.forEach { video ->

            val mediaLocalPath = getLocalMediaPath2(uid = video.id)
            val duration = getMediaDuration(mediaLocalPath = mediaLocalPath)

            var lastUpdated: String? = "null"
            runBlocking {
                val task = async {
                    val details = repository.getMediaDetails(uid = video.id)
                    lastUpdated = details?.lastUpdated
                }
                awaitAll(task)
            }

            val mediaEntity = DialogMediaEntity(
                title = video.name,
                duration = duration,
                dateOfLastUpdate = formatDate(date = lastUpdated),
                url = mediaLocalPath ?: "No path provided!",
                dialogMediaType = DialogMediaType.VIDEO
            )
            mediaEntitiesList.add(mediaEntity)
        }

        audios.forEach { audio ->

            val mediaLocalPath = getLocalMediaPath2(uid = audio.id)
            val duration = getMediaDuration(mediaLocalPath = mediaLocalPath)

            var lastUpdated: String? = "null"
            runBlocking {
                val task = async {
                    val details: MediaDetails? = repository.getMediaDetails(uid = audio.id)
                    lastUpdated = details?.lastUpdated
                }
                awaitAll(task)
            }

            val mediaEntity = DialogMediaEntity(
                title = audio.name,
                duration = duration,
                dateOfLastUpdate = formatDate(date = lastUpdated),
                url = mediaLocalPath ?: "No path provided!",
                dialogMediaType = DialogMediaType.AUDIO
            )
            mediaEntitiesList.add(mediaEntity)
        }

        mediaEntities.value.clear()
        mediaEntities.value.addAll(mediaEntitiesList)
        setMediaLoading(loading = false)
    }

    private fun formatDate(date: String?): String {
        // Parse the input date string
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        val parsedDate = dateFormat.parse(date)

        // Format the date to the desired output format (day-month-year)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return outputFormat.format(parsedDate!!)
    }

    private fun getMediaDuration(mediaLocalPath: String?): String {
        return if (mediaLocalPath.isNullOrEmpty() || mediaLocalPath.isBlank()) {
            "null"
        } else {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(mediaLocalPath)
            val duration =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
                    ?: 0L
            retriever.release()

            val minutes = duration % (60 * 60 * 1000) / (60 * 1000)
            val seconds = duration % (60 * 1000) / 1000

            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun checkDataElement(uid: String): DataElement? {
        val mediaDataStoreConfig = mediaDataStore.value

        var dataElementList: List<DataElement>? = null
        mediaDataStoreConfig?.let { mediaStoreValue ->

            val dataElementMaps = mediaStoreValue.map { mediaStoreItem ->
                mediaStoreItem.dataElements
            }
            val filteredDataElements = dataElementMaps.map { dataElements ->
                dataElements?.let {
                    dataElements.filter { dataElement ->
                        dataElement.dataElement == uid
                    }
                }
            }
            dataElementList = filteredDataElements.getOrNull(0)
        }
        return dataElementList?.getOrNull(0)
    }

    /**
     * Method to get server media details from web and store to DB
     * Return MediaDetails MediaDetails or Null
     */
    fun getMediaDetails(uid: String): MediaDetails? {
        var details: MediaDetails? = null
        viewModelScope.launch {
            details = repository.getMediaDetails(uid)
            Timber.tag("MEDIA_DETAILS").d("${details.toString()}")
            if (details != null) {
                Timber.tag("HAS_MEDIA_THAN_STORE").d("${details.toString()}")
                repository.storeLocalMediaDetails(details!!)
            }
        }
        return details
    }

    /**
     * Method to get all media details from local DB
     * Return MediaDetails List<MediaDetails?>
     */
    fun getAllLocalMediaDetails(mediaDetails: MediaDetails): List<MediaDetails?> {
        var details: List<MediaDetails?> = emptyList()
        viewModelScope.launch {
            details = repository.getAllLocalMediaDetails()
            Timber.tag("ALL_MEDIA_DETAILS").d("$details")
        }
        return details
    }

    /**
     * Method to get the media details by it Id from DB
     * Return MediaDetails or Null
     */
    fun getLocalMediaDetails(uid: String): MediaDetails? {
        var details: MediaDetails? = null
        viewModelScope.launch {
            details = repository.getLocalMediaDetails(uid)
            Timber.tag("ONE_MEDIA_DETAILS").d("${details.toString()}")
        }
        return details
    }

    fun setMediaLoading(loading: Boolean) {
        _isLoadingMedia.value = loading
    }
}
