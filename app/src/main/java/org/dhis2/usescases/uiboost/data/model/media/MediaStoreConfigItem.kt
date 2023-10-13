package org.dhis2.usescases.uiboost.data.model.media

data class MediaStoreConfigItem(
    val attributes: List<Attribute>,
    val dataElements: List<DataElement>
)