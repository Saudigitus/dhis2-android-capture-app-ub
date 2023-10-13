package org.dhis2.usescases.uiboost.data.model.media

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.serialization.Serializable
import org.dhis2.usescases.uiboost.data.model.media.MediaStoreConfig.Mapper.translateJsonToObject

@Serializable
class MediaStoreConfig : ArrayList<MediaStoreConfigItem>() {

    private fun toJson(): String = translateJsonToObject().writeValueAsString(this)

    companion object {
        fun fromJson(json: String?): MediaStoreConfig? = if (json != null) {
            translateJsonToObject()
                .readValue(json, MediaStoreConfig::class.java)
        } else {
            null
        }
    }

    override fun toString(): String {
        return toJson()
    }

    object Mapper {
        fun translateJsonToObject(): ObjectMapper {
            return jacksonObjectMapper().apply {
                propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
            }
        }
    }
}
