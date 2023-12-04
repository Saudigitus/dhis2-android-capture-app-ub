package org.dhis2.usescases.uiboost.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import org.dhis2.usescases.uiboost.data.model.DataStoreAppConfig
import org.dhis2.usescases.uiboost.data.model.media.MediaStoreConfig
import org.dhis2.usescases.uiboost.data.util.Constants
import javax.inject.Inject
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.datastore.DataStoreEntry
import org.hisp.dhis.android.core.relationship.RelationshipItem

class UBDataStoreRepositoryImpl @Inject constructor(
    private val d2: D2
) : UBDataStoreRepository {
    override suspend fun downloadDataStore() {
        d2.dataStoreModule().dataStoreDownloader().download()
        val result = d2.dataStoreModule().dataStore().blockingGet()
    }

//    override suspend fun getDataStore(): Flow<List<DataStoreEntry>> {
//      val dataStore = d2.dataStoreModule()
//          .dataStore().byKey().eq(Constants.MEDIA_DATA_STORE_KEY).blockingGet()
//
//        return flowOf(dataStore)
//    }

    override suspend fun getFilteredMediaDataStore(): Flow<MediaStoreConfig?> {
        val dataStore = MediaStoreConfig.fromJson(
            d2.dataStoreModule().dataStore().byKey()
                .eq(Constants.MEDIA_DATA_STORE_KEY).blockingGet()
                .getOrNull(
                    0
                )?.value()
        )
        return flowOf(dataStore)
    }

    override suspend fun countTeiRelationship(uid: String) = withContext(Dispatchers.IO) {
        return@withContext d2.relationshipModule().relationshipTypes()
            .byAvailableForTrackedEntityInstance(uid)
            .blockingCount()
    }
}
