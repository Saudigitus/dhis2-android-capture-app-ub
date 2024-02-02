package org.dhis2.usescases.teiDashboard.data

import org.dhis2.R
import org.dhis2.commons.resources.ResourceManager
import org.dhis2.ui.MetadataIconData
import org.hisp.dhis.android.core.program.Program

class ProgramWithEnrollmentMapper(private val resourceManager: ResourceManager) {

    fun map(
        program: Program,
        countEnrollment: Int,
        countDescription: Int,
        typeName: String,
        enrollmentStatus: Boolean,
    ): ProgramWithEnrollment {
        return ProgramWithEnrollment(
            programId = program.uid(),
            displayName = program.displayName()!!,
            programType = program.programType()!!.name,
            typeName = typeName,
            enrollmentStatus = enrollmentStatus,
            countDescription = countDescription,
            countEnrollment = countEnrollment,
            metadataIconData = MetadataIconData(
                programColor = resourceManager.getColorOrDefaultFrom(program.style()?.color()),
                iconResource = resourceManager.getObjectStyleDrawableResource(
                    program.style()?.icon(),
                    R.drawable.ic_default_outline
                )
            )
        )
    }

//    fun map(
//        dataSet: DataSet,
//        dataSetInstanceSummary: DataSetInstanceSummary,
//        recordCount: Int,
//        dataSetLabel: String,
//        filtersAreActive: Boolean
//    ): ProgramViewModel {
//        return ProgramViewModel(
//            uid = dataSetInstanceSummary.dataSetUid(),
//            title = dataSetInstanceSummary.dataSetDisplayName(),
//            metadataIconData = MetadataIconData(
//                programColor = resourceManager.getColorOrDefaultFrom(dataSet.style()?.color()),
//                iconResource = resourceManager.getObjectStyleDrawableResource(
//                    dataSet.style()?.icon(),
//                    R.drawable.ic_default_outline
//                )
//            ),
//            count = recordCount,
//            type = null,
//            typeName = dataSetLabel,
//            programType = "",
//            description = dataSet.description(),
//            onlyEnrollOnce = false,
//            accessDataWrite = dataSet.access().data().write(),
//            state = dataSetInstanceSummary.state(),
//            hasOverdueEvent = false,
//            filtersAreActive = filtersAreActive,
//            downloadState = ProgramDownloadState.NONE
//        )
//    }
//
//    fun map(
//        programViewModel: ProgramViewModel,
//        downloadState: ProgramDownloadState
//    ): ProgramViewModel {
//        return programViewModel.copy(
//            downloadState = downloadState
//        )
//    }

}