package org.dhis2.usescases.uiboost.data.model


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class RelationshipConfig(
    @JsonProperty("config")
    val config: List<Config>
) {
    private fun toJson(): String = Mapper.translateJsonToObject().writeValueAsString(this)

    companion object {
        @JvmStatic
        fun fromJson(json: String?): RelationshipConfig? = if (json != null) {
            Mapper.translateJsonToObject()
                .readValue(json, RelationshipConfig::class.java)
        } else {
            null
        }
    }

    override fun toString(): String {
        return toJson()
    }
}