package org.dhis2.usescases.uiboost.data.model

import org.dhis2.ui.MetadataIconData
import org.dhis2.usescases.main.program.ProgramDownloadState
import org.hisp.dhis.android.core.common.State

data class ProgramViewModelCustom(
    val uid: String,
    val title: String,
    val metadataIconData: MetadataIconData,
    val count: Int,
    val type: String?,
    val typeName: String,
    val programType: String,
    val description: String?,
    val onlyEnrollOnce: Boolean,
    val accessDataWrite: Boolean,
    val state: State,
    val hasOverdueEvent: Boolean,
    val filtersAreActive: Boolean,
    val downloadState: ProgramDownloadState,
    val downloadActive: Boolean = false,
    val reference: String
)
