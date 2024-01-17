package org.dhis2.usescases.uiboost.data.model


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Config(
    @JsonProperty("program")
    val program: String,
    @JsonProperty("relationShipType")
    val relationShipType: List<RelationshipType>
)