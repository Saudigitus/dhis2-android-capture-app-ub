package org.dhis2.usescases.teiDashboard

import org.dhis2.commons.filters.data.FilterPresenter
import org.dhis2.commons.resources.ResourceManager
import org.dhis2.commons.schedulers.SchedulerProvider
import org.dhis2.data.dhislogic.DhisProgramUtils
import org.dhis2.data.dhislogic.DhisTrackedEntityInstanceUtils
import org.dhis2.usescases.main.program.ProgramViewModel
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramType

class CustomDashboardrepositoryImpl(
    private val d2: D2,
    private val filterPresenter: FilterPresenter,
    private val dhisProgramUtils: DhisProgramUtils,
    private val dhisTeiUtils: DhisTrackedEntityInstanceUtils,
    private val resourceManager: ResourceManager,
    private val schedulerProvider: SchedulerProvider
) {

    fun List<ProgramViewModel>.applyFilters(): List<ProgramViewModel> {
        return map { programModel ->
            val program = d2.programModule().programs().uid(programModel.uid).blockingGet()
            val (count, hasOverdue) =
                if (program.programType() == ProgramType.WITHOUT_REGISTRATION) {
                    getSingleEventCount(program)
                } else {
                    getTrackerTeiCountAndOverdue(program)
                }
            programModel.copy(
                count = count,
                hasOverdueEvent = hasOverdue,
                filtersAreActive = filterPresenter.areFiltersActive()
            )
        }
    }

    private fun getSingleEventCount(program: Program): Pair<Int, Boolean> {
        return Pair(
            filterPresenter.filteredEventProgram(program)
                .blockingGet().filter { event -> event.syncState() != State.RELATIONSHIP }.size,
            false
        )
    }

    private fun getTrackerTeiCountAndOverdue(program: Program): Pair<Int, Boolean> {
        val teiIds = filterPresenter.filteredTrackerProgram(program)
            .offlineFirst().blockingGetUids()
        val mCount = teiIds.size
        val mOverdue = dhisTeiUtils.hasOverdueInProgram(teiIds, program)

        return Pair(mCount, mOverdue)
    }

}