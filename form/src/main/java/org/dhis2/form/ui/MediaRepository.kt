package org.dhis2.form.ui

import org.dhis2.form.data.media.MediaDetails

interface MediaRepository {
    suspend fun getMediaDetails(uid: String): MediaDetails?
    suspend fun storeLocalMediaDetails(mediaDetails: MediaDetails)
    suspend fun getAllLocalMediaDetails(): List<MediaDetails?>
    suspend fun getLocalMediaDetails(uid: String): MediaDetails?
}