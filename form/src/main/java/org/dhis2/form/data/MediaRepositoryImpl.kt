package org.dhis2.form.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.dhis2.form.data.media.MediaDetails
import org.dhis2.form.local.MediaDetailsDAO
import org.dhis2.form.ui.MediaRepository

class MediaRepositoryImpl(
    private val mediaDetailsDAO: MediaDetailsDAO
) : MediaRepository {
    override suspend fun getMediaDetails(uid: String): MediaDetails? {
        TODO("Not yet implemented")
    }

    override suspend fun storeLocalMediaDetails(mediaDetails: MediaDetails)  = withContext(
        Dispatchers.IO)  {
        val resp = mediaDetailsDAO.create(mediaDetails)
    }

    override suspend fun getAllLocalMediaDetails(): List<MediaDetails?>  = withContext(Dispatchers.IO)  {
        return@withContext mediaDetailsDAO.getAll()

    }

    override suspend fun getLocalMediaDetails(uid: String): MediaDetails?  = withContext(Dispatchers.IO) {
        return@withContext mediaDetailsDAO.getDetailsById(uid)
    }
}