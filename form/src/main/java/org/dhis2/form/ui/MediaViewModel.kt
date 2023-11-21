package org.dhis2.form.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.dhis2.form.data.media.MediaDetails
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject internal constructor(
    private val mediaRepository: MediaRepository,
    )  : ViewModel() {

    fun getMediaDetails(uid: String): MediaDetails? {
        var details: MediaDetails? = null
        viewModelScope.launch {
            details = mediaRepository.getMediaDetails(uid)
            println("MEDIA_DETAILS: ${details.toString()}")
        }
        details?.let { storeLocalMediaDetails(it) }
        return details
    }

    /*fun storeLocalMediaDetails(mediaDetails: MediaDetails) {

        viewModelScope.launch {
            val details = mediaDetailsDAO.create(mediaDetails)
            println("STORE_RESP: ${details.toString()}")
        }
    }

    fun getAllLocalMediaDetails(mediaDetails: MediaDetails): List<MediaDetails?> {
        var details: List<MediaDetails?> = emptyList()
        viewModelScope.launch {
            details = mediaDetailsDAO.getAll()
            println("ALL_MEDIA_DETAILS: ${details.toString()}")
        }
        return details
    }*/

    fun storeLocalMediaDetails(mediaDetails: MediaDetails) {

        viewModelScope.launch {
            val details = mediaRepository.storeLocalMediaDetails(mediaDetails)
            println("STORE_RESP: ${details.toString()}")
        }
    }

    fun getAllLocalMediaDetails(mediaDetails: MediaDetails): List<MediaDetails?> {
        var details: List<MediaDetails?> = emptyList()
        viewModelScope.launch {
            details = mediaRepository.getAllLocalMediaDetails()
            println("ALL_MEDIA_DETAILS: ${details.toString()}")
        }
        return details
    }

    fun getLocalMediaDetails(uid: String): MediaDetails? {
        var details: MediaDetails? = null
        viewModelScope.launch {
            details = mediaRepository.getLocalMediaDetails(uid)
            println("ONE_MEDIA_DETAILS: ${details.toString()}")
        }
        return details
    }
    }