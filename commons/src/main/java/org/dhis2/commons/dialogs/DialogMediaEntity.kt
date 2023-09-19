package org.dhis2.commons.dialogs

data class DialogMediaEntity(
    val title: String,
    val duration: String,
    val dateOfLastUpdate: String,
    val url: String,
    val dialogMediaType: DialogMediaType,
)