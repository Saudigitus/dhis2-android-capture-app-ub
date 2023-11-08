package org.dhis2.form.data

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable
import org.dhis2.form.data.media.MediaDetails

@Serializable
data class MediaDetailsConfig(
    @JsonProperty("mediaDetails")
    val mediaDetails: List<MediaDetails>? = emptyList()
)