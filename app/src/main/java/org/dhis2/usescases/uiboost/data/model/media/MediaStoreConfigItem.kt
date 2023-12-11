package org.dhis2.usescases.uiboost.data.model.media

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
data class MediaStoreConfigItem(
    val attributes: List<Attribute>? = null,
    val dataElements: List<DataElement>? = null
)
