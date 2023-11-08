package org.dhis2.form.data.media

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.core.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.*
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.module.kotlin.*
import kotlinx.serialization.Serializable

@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
data class MediaDetails (
    @PrimaryKey val id: String,
    val name: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null
) {
    fun toJson() = mapper.writeValueAsString(this)

    companion object {
        fun fromJson(json: String) = mapper.readValue<MediaDetails>(json)
    }
}

val mapper = jacksonObjectMapper().apply {
    propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
}
