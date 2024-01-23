package org.dhis2.usescases.teiDashboard

import dhis2.org.analytics.charts.Charts
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import org.dhis2.commons.data.tuples.Pair
import org.dhis2.commons.data.tuples.Trio
import org.dhis2.commons.resources.ResourceManager
import org.dhis2.usescases.main.program.ProgramViewModel
import org.dhis2.usescases.teiDashboard.data.ProgramWithEnrollment
import org.dhis2.utils.AuthorityException
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.category.CategoryCombo
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.ProgramRuleActionType
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute
import org.hisp.dhis.android.core.relationship.RelationshipType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import timber.log.Timber

class DashboardRepositoryCustomImpl(
    private val d2: D2,
    val resources: ResourceManager,
    val enrollmentUid: String,
    val charts: Charts,
    val teiUid: String,
    val programUid: String,
    val teiAttributesProvider: TeiAttributesProvider? =
        null
) : DashboardRepository {
    override fun getProgramStages(programStages: String?): Observable<MutableList<ProgramStage>> {
        return d2.programModule().programStages().byProgramUid().eq(programStages).get().toObservable()
    }

    override fun getEnrollment(): Observable<Enrollment> {
        return d2.enrollmentModule().enrollments().uid(enrollmentUid).get().toObservable()
    }
    private fun getEnrollment(programId: String, trackerId: String): Boolean {
        return d2.enrollmentModule()
            .enrollments()
            .byProgram().eq(programId)
            .byTrackedEntityInstance().eq(trackerId)
            .byStatus().eq(EnrollmentStatus.ACTIVE)
            .blockingIsEmpty()
    }

    override fun getTEIEnrollmentEvents(
        programUid: String?,
        teiUid: String?
    ): Observable<MutableList<Event>> {
        TODO("Not yet implemented")
    }

    override fun getEnrollmentEventsWithDisplay(
        programUid: String?,
        teiUid: String?
    ): Observable<MutableList<Event>> {
        TODO("Not yet implemented")
    }

    override fun getTEIAttributeValues(
        programUid: String?,
        teiUid: String?
    ): Observable<MutableList<TrackedEntityAttributeValue>> {
        TODO("Not yet implemented")
    }

    override fun allPrograms(trackedEntityId: String?): Flowable<MutableList<ProgramViewModel>> {
        TODO("Not yet implemented")
    }

    override fun getIndicators(programUid: String?): Flowable<MutableList<ProgramIndicator>> {
        TODO("Not yet implemented")
    }

    override fun setFollowUp(enrollmentUid: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateState(eventModel: Event?, newStatus: EventStatus?): Event {

        try {
            d2.eventModule().events().uid(eventModel!!.uid()).setStatus(newStatus)
        } catch (d2Error: D2Error) {
            Timber.e(d2Error)
        }

        return d2.eventModule().events().uid(eventModel!!.uid()).blockingGet()
    }

    override fun completeEnrollment(enrollmentUid: String): Flowable<Enrollment> {
        TODO("Not yet implemented")
    }

    override fun displayGenerateEvent(eventUid: String?): Observable<ProgramStage> {
        TODO("Not yet implemented")
    }

    override fun getLegendColorForIndicator(
        programIndicator: ProgramIndicator?,
        value: String?
    ): Observable<Trio<ProgramIndicator, String, String>> {
        TODO("Not yet implemented")
    }

    override fun getObjectStyle(uid: String?): Int {
        TODO("Not yet implemented")
    }

    override fun relationshipsForTeiType(teType: String?): Observable<MutableList<Pair<RelationshipType, String>>> {
        TODO("Not yet implemented")
    }

    override fun catComboForProgram(program: String?): Observable<CategoryCombo> {
        TODO("Not yet implemented")
    }

    override fun isStageFromProgram(stageUid: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun catOptionCombo(catComboUid: String?): CategoryOptionCombo {
        TODO("Not yet implemented")
    }

    override fun setDefaultCatOptCombToEvent(eventUid: String?) {
        TODO("Not yet implemented")
    }

    override fun getTrackedEntityInstance(teiUid: String?): Observable<TrackedEntityInstance> {
        TODO("Not yet implemented")
    }

    override fun getProgramTrackedEntityAttributes(programUid: String?): Observable<MutableList<ProgramTrackedEntityAttribute>> {
        TODO("Not yet implemented")
    }

    override fun getTeiOrgUnits(
        teiUid: String,
        programUid: String?
    ): Observable<MutableList<OrganisationUnit>> {
        TODO("Not yet implemented")
    }

    override fun getTeiActivePrograms(
        teiUid: String?,
        showOnlyActive: Boolean
    ): Observable<MutableList<Program>> {
        TODO("Not yet implemented")
    }

    override fun getTEIEnrollments(teiUid: String?): Observable<MutableList<Enrollment>> {
        return d2.enrollmentModule().enrollments().byTrackedEntityInstance().eq(teiUid).byDeleted()
            .eq(false).get().toObservable()
    }

    override fun saveCatOption(eventUid: String?, catOptionComboUid: String?) {
        try {
            d2.eventModule().events().uid(eventUid).setAttributeOptionComboUid(catOptionComboUid)
        } catch (d2Error: D2Error) {
            Timber.e(d2Error)
        }
    }

    override fun deleteTeiIfPossible(): Single<Boolean> {
        return Single.fromCallable {
            val local = d2.trackedEntityModule()
                .trackedEntityInstances()
                .uid(teiUid)
                .blockingGet()
                .state() == State.TO_POST
            val hasAuthority = d2.userModule()
                .authorities()
                .byName().eq("F_TEI_CASCADE_DELETE")
                .one().blockingExists()
            local || hasAuthority
        }.flatMap { canDelete: Boolean ->
            if (canDelete) {
                return@flatMap d2.trackedEntityModule()
                    .trackedEntityInstances()
                    .uid(teiUid)
                    .delete()
                    .andThen<Boolean>(Single.fromCallable<Boolean> { true })
            } else {
                return@flatMap Single.fromCallable<Boolean> { false }
            }
        }
    }

    override fun deleteEnrollmentIfPossible(enrollmentUid: String?): Single<Boolean> {
        return Single.fromCallable {
            val local = d2.enrollmentModule()
                .enrollments()
                .uid(enrollmentUid)
                .blockingGet().state() == State.TO_POST
            val hasAuthority = d2.userModule()
                .authorities()
                .byName().eq("F_ENROLLMENT_CASCADE_DELETE")
                .one().blockingExists()
            local || hasAuthority
        }.flatMap { canDelete: Boolean ->
            if (canDelete) {
                return@flatMap Single.fromCallable<Boolean> {
                    val enrollmentObjectRepository =
                        d2.enrollmentModule()
                            .enrollments().uid(enrollmentUid)
                    enrollmentObjectRepository.setStatus(
                        enrollmentObjectRepository.blockingGet().status()
                    )
                    enrollmentObjectRepository.blockingDelete()
                    !d2.enrollmentModule().enrollments().byTrackedEntityInstance().eq(teiUid)
                        .byDeleted().isFalse
                        .byStatus().eq(EnrollmentStatus.ACTIVE).blockingGet().isEmpty()
                }
            } else {
                return@flatMap Single.error<Boolean>(
                    AuthorityException(
                        null
                    )
                )
            }
        }
    }

    override fun getNoteCount(): Single<Int> {
        return d2.enrollmentModule().enrollments()
            .withNotes()
            .uid(enrollmentUid)
            .get()
            .map { enrollment: Enrollment -> if (enrollment.notes() != null) enrollment.notes()!!.size else 0 }
    }

    override fun getEnrollmentStatus(enrollmentUid: String?): EnrollmentStatus {
        return d2.enrollmentModule().enrollments().uid(enrollmentUid).blockingGet().status()!!
    }

    override fun updateEnrollmentStatus(
        enrollmentUid: String?,
        status: EnrollmentStatus?
    ): Observable<StatusChangeResultCode> {
        return try {
            if (d2.programModule().programs().uid(programUid).blockingGet().access().data()
                    .write()
            ) {
                if (reopenCheck(status!!)) {
                    d2.enrollmentModule().enrollments().uid(enrollmentUid).setStatus(status)
                    Observable.just<StatusChangeResultCode>(StatusChangeResultCode.CHANGED)
                } else {
                    Observable.just<StatusChangeResultCode>(StatusChangeResultCode.ACTIVE_EXIST)
                }
            } else {
                Observable.just<StatusChangeResultCode>(StatusChangeResultCode.WRITE_PERMISSION_FAIL)
            }
        } catch (error: D2Error) {
            Observable.just<StatusChangeResultCode>(StatusChangeResultCode.FAILED)
        }
    }
    private fun reopenCheck(status: EnrollmentStatus): Boolean {
        return status != EnrollmentStatus.ACTIVE || d2.enrollmentModule().enrollments()
            .byProgram().eq(programUid)
            .byTrackedEntityInstance().eq(teiUid)
            .byStatus().eq(EnrollmentStatus.ACTIVE)
            .blockingIsEmpty()
    }

    override fun programHasRelationships(): Boolean {
        return if (programUid != null) {
            val teiTypeUid = d2.programModule().programs()
                .uid(programUid)
                .blockingGet()
                .trackedEntityType()
                ?.uid()
            !relationshipsForTeiType(teiTypeUid).blockingFirst().isEmpty()
        } else {
            false
        }
    }

    override fun programHasAnalytics(): Boolean {
        return if (programUid != null) {
            val enrollmentScopeRulesUids = d2.programModule().programRules()
                .byProgramUid().eq(programUid)
                .byProgramStageUid().isNull
                .blockingGetUids()
            val hasDisplayRuleActions = !d2.programModule().programRuleActions()
                .byProgramRuleUid().`in`(enrollmentScopeRulesUids)
                .byProgramRuleActionType()
                .`in`(ProgramRuleActionType.DISPLAYKEYVALUEPAIR, ProgramRuleActionType.DISPLAYTEXT)
                .blockingIsEmpty()
            val hasProgramIndicator =
                !d2.programModule().programIndicators().byProgramUid().eq(programUid)
                    .blockingIsEmpty()
            val hasCharts = charts != null && !charts.geEnrollmentCharts(enrollmentUid).isEmpty()
            hasDisplayRuleActions || hasProgramIndicator || hasCharts
        } else {
            false
        }
    }

    override fun getTETypeName(): String {
        return getTrackedEntityInstance(teiUid).flatMap { tei: TrackedEntityInstance ->
            d2.trackedEntityModule().trackedEntityTypes()
                .uid(tei.trackedEntityType())
                .get()
                .toObservable()
        }.blockingFirst().displayName()!!
    }

    override fun getProgramDashboard(
        ou: String?,
        trackerId: String?,
        trackedEntityId: String?
    ): MutableList<ProgramWithEnrollment> {
        TODO("Not yet implemented")
    }

    override fun getEnrollmentOU(programUd: String?, trackerId: String?): Enrollment {
        return d2.enrollmentModule()
            .enrollments()
            .byProgram().eq(programUid)
            .byTrackedEntityInstance().eq(trackerId)
            .one()
            .blockingGet()
    }

    override fun testEnrollment(): MutableList<Program> {
        return d2.programModule().programs()
            .blockingGet()
    }
}