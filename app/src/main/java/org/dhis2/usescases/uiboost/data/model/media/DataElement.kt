package org.dhis2.usescases.uiboost.data.model.media

data class DataElement(
    val audio: List<Audio>,
    val dataElement: String,
    val description: String,
    val icon: Icon,
    val video: List<Video>
)
