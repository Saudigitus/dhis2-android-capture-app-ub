package org.dhis2.usescases.uiboost.viewmodels

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.dhis2.commons.filters.FilterManager
import org.dhis2.commons.matomo.MatomoAnalyticsController
import org.dhis2.commons.schedulers.SchedulerProvider
import org.dhis2.data.service.SyncStatusController
import org.dhis2.usescases.main.program.IdentifyProgramType
import org.dhis2.usescases.main.program.ProgramRepository
import org.dhis2.usescases.main.program.ProgramView
import org.dhis2.usescases.main.program.ProgramViewModel
import org.dhis2.usescases.main.program.StockManagementMapper
import org.dhis2.usescases.uiboost.data.model.DataStoreAppConfig
import org.dhis2.usescases.uiboost.data.model.media.MediaStoreConfig
import org.dhis2.usescases.uiboost.data.repository.UBDataStoreRepository
import org.hisp.dhis.android.core.datastore.DataStoreEntry
import timber.log.Timber

class HomeProgramViewModel @Inject internal constructor(
    private val view: ProgramView,
    private val programRepository: ProgramRepository,
    private val uBoostRepository: UBDataStoreRepository,
    private val schedulerProvider: SchedulerProvider,
    private val filterManager: FilterManager,
    private val matomoAnalyticsController: MatomoAnalyticsController,
    private val syncStatusController: SyncStatusController,
    private val identifyProgramType: IdentifyProgramType,
    private val stockManagementMapper: StockManagementMapper
) : ViewModel() {
    private val refreshData = PublishProcessor.create<Unit>()
    var disposable: CompositeDisposable = CompositeDisposable()

    val _programs = MutableStateFlow<List<ProgramViewModel>>(emptyList())
    val programs: StateFlow<List<ProgramViewModel>> = _programs

    private val _dataStoreDataElement = MutableStateFlow<List<DataStoreEntry>>(emptyList())
    val dataStoreMedia: StateFlow<List<DataStoreEntry>> = _dataStoreDataElement

    private val _mediaDataStoreFiltered = MutableStateFlow<MediaStoreConfig?>(null)
    val mediaDataStoreFiltered: StateFlow<MediaStoreConfig?> = _mediaDataStoreFiltered

    init {
        getPrograms()
    }

    fun getPrograms() {
        val applyFiler = PublishProcessor.create<FilterManager>()
        programRepository.clearCache()

        disposable.add(
            applyFiler
                .switchMap {
                    refreshData.debounce(
                        500,
                        TimeUnit.MILLISECONDS,
                        schedulerProvider.io()
                    ).startWith(Unit).switchMap {
                        programRepository.homeItems(
                            syncStatusController.observeDownloadProcess().value!!
                        )
                    }
                }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                    { programs ->
                        _programs.value = programs
                        view.swapProgramModelData(programs)
                        Timber.tag("PGRAM").d("$programs")
                    },
                    { throwable -> Timber.d(throwable) },
                    { Timber.tag("INIT DATA").d("LOADING ENDED") }
                )
        )

        disposable.add(
            filterManager.asFlowable()
                .startWith(filterManager)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                    {
                        view.showFilterProgress()
                        applyFiler.onNext(filterManager)
                    },
                    { Timber.e(it) }
                )
        )

        disposable.add(
            filterManager.ouTreeFlowable()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                    { view.openOrgUnitTreeSelector() },
                    { Timber.e(it) }
                )
        )
    }

    fun getMediaDataStore() {
        runBlocking(Dispatchers.IO) {
            launch {
                uBoostRepository.getDataStore().collectLatest {
                    _dataStoreDataElement.value = (it)
                }
                uBoostRepository.getFilteredMediaDataStore().collectLatest {
                    _mediaDataStoreFiltered.value = it
                }
            }
        }

    }
}
