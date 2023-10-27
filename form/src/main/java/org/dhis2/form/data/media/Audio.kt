package org.dhis2.usescases.uiboost.data.model.media

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class Audio(
    val id: String,
    val name: String
)
