package org.dhis2.usescases.uiboost.data.model.media

data class Attribute(
    val attribute: String,
    val audio: List<Audio>,
    val description: String,
    val icon: Icon,
    val video: List<Video>
)
