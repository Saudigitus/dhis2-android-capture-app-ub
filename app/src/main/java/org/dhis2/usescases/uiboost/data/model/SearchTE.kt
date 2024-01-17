package org.dhis2.usescases.uiboost.data.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.dhis2.form.model.ActionType
import org.dhis2.form.model.RowAction
import org.dhis2.usescases.uiboost.data.model.Mapper.translateJsonToObject

data class SearchTE(
    val value: String
) {
    private fun toJson(): String = Mapper.translateJsonToObject().writeValueAsString(this)

    companion object {
        fun stringToObject(value: String?): SearchTE? = if (value != null) {
            SearchTE(value =  value)
        } else {
            null
        }

        fun fromJson(json: String?): SearchTE? = if (json != null) {
            translateJsonToObject()
                .readValue(json, SearchTE::class.java)
        } else {
            null
        }
    }

    override fun toString(): String {
        return toJson()
    }
}

object Mapper {
    fun translateJsonToObject(): ObjectMapper {
        return jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }
}

fun SearchTE.toRowAction(id: String) = RowAction(
    id = id,
    value = this.value,
    type = ActionType.ON_SAVE
)
