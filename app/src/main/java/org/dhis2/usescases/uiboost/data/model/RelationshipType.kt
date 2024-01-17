package org.dhis2.usescases.uiboost.data.model


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class RelationshipType(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String
)