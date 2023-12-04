package org.dhis2.usescases.uiboost.data.repository

import kotlinx.coroutines.flow.Flow
import org.dhis2.usescases.uiboost.data.model.DataStoreAppConfig
import org.dhis2.usescases.uiboost.data.model.media.MediaStoreConfig
import org.hisp.dhis.android.core.datastore.DataStoreEntry

interface UBDataStoreRepository {

    suspend fun downloadDataStore()

//    suspend fun getDataStore(): Flow<List<DataStoreEntry>>
    suspend fun getFilteredMediaDataStore(): Flow<MediaStoreConfig?>
    suspend fun countTeiRelationship(uid: String): Int
}
