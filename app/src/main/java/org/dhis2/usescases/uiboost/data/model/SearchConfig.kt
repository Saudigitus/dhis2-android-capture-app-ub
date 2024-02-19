package org.dhis2.usescases.uiboost.data.model


import com.fasterxml.jackson.annotation.JsonProperty

data class SearchConfig(
    @JsonProperty("attributes")
    val attributes: List<Attribute> = emptyList()
) {
    private fun toJson(): String = Mapper.translateJsonToObject().writeValueAsString(this)

    companion object {
        @JvmStatic
        fun fromJson(json: String?): SearchConfig? = if (json != null) {
            Mapper.translateJsonToObject()
                .readValue(json, SearchConfig::class.java)
        } else {
            null
        }
    }

    override fun toString(): String {
        return toJson()
    }
}