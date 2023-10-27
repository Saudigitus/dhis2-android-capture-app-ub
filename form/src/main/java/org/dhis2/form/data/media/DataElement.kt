package org.dhis2.usescases.uiboost.data.model.media

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class DataElement(
    val audio: List<Audio>,
    val dataElement: String,
    val description: String,
    val icon: Icon,
    val video: List<Video>
)
