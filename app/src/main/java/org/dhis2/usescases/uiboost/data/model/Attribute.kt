package org.dhis2.usescases.uiboost.data.model


import com.fasterxml.jackson.annotation.JsonProperty

data class Attribute(
    @JsonProperty("uid")
    val uid: String,
    @JsonProperty("name")
    val name: String
)