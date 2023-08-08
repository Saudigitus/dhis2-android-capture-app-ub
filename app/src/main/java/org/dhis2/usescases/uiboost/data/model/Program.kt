package org.dhis2.usescases.uiboost.data.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class Program(
    val hidden: String,
    val icon: String,
    val program: String
)
