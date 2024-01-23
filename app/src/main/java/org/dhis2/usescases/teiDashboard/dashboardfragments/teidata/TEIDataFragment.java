package org.dhis2.usescases.teiDashboard.dashboardfragments.teidata;

import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static org.dhis2.commons.Constants.ENROLLMENT_UID;
import static org.dhis2.commons.Constants.EVENT_CREATION_TYPE;
import static org.dhis2.commons.Constants.EVENT_PERIOD_TYPE;
import static org.dhis2.commons.Constants.EVENT_REPEATABLE;
import static org.dhis2.commons.Constants.EVENT_SCHEDULE_INTERVAL;
import static org.dhis2.commons.Constants.ORG_UNIT;
import static org.dhis2.commons.Constants.PROGRAM_UID;
import static org.dhis2.commons.Constants.TEI_UID;
import static org.dhis2.commons.Constants.TRACKED_ENTITY_INSTANCE;
import static org.dhis2.usescases.teiDashboard.DataConstantsKt.GO_TO_ENROLLMENT;
import static org.dhis2.usescases.teiDashboard.DataConstantsKt.GO_TO_ENROLLMENT_PROGRAM;
import static org.dhis2.utils.analytics.AnalyticsConstants.CREATE_EVENT_TEI;
import static org.dhis2.utils.analytics.AnalyticsConstants.TYPE_EVENT_TEI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.dhis2.App;
import org.dhis2.R;
import org.dhis2.commons.Constants;
import org.dhis2.commons.animations.ViewAnimationsKt;
import org.dhis2.commons.data.EventCreationType;
import org.dhis2.commons.data.EventViewModel;
import org.dhis2.commons.data.StageSection;
import org.dhis2.commons.dialogs.CustomDialog;
import org.dhis2.commons.dialogs.DialogClickListener;
import org.dhis2.commons.dialogs.calendarpicker.CalendarPicker;
import org.dhis2.commons.dialogs.calendarpicker.OnDatePickerListener;
import org.dhis2.commons.dialogs.imagedetail.ImageDetailBottomDialog;
import org.dhis2.commons.filters.FilterItem;
import org.dhis2.commons.filters.FilterManager;
import org.dhis2.commons.filters.FiltersAdapter;
import org.dhis2.commons.orgunitselector.OUTreeFragment;
import org.dhis2.commons.orgunitselector.OrgUnitSelectorScope;
import org.dhis2.commons.resources.ObjectStyleUtils;
import org.dhis2.commons.sync.SyncContext;
import org.dhis2.databinding.FragmentTeiDataBinding;
import org.dhis2.ui.ThemeManager;
import org.dhis2.usescases.enrollment.EnrollmentActivity;
import org.dhis2.usescases.eventsWithoutRegistration.eventInitial.EventInitialActivity;
import org.dhis2.usescases.general.FragmentGlobalAbstract;
import org.dhis2.usescases.main.program.ProgramDownloadState;
import org.dhis2.usescases.main.program.ProgramViewModel;
import org.dhis2.usescases.programStageSelection.ProgramStageSelectionActivity;
import org.dhis2.usescases.teiDashboard.DashboardProgramModel;
import org.dhis2.usescases.teiDashboard.DashboardViewModel;
import org.dhis2.usescases.teiDashboard.TeiDashboardMobileActivity;
import org.dhis2.usescases.teiDashboard.adapters.ProgramDashboardAdapter;
import org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.teievents.CategoryDialogInteractions;
import org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.teievents.EventAdapter;
import org.dhis2.usescases.teiDashboard.dashboardfragments.teidata.teievents.EventCatComboOptionSelector;
import org.dhis2.usescases.teiDashboard.data.ProgramWithEnrollment;
import org.dhis2.usescases.teiDashboard.teiProgramList.EnrollmentViewModel;
import org.dhis2.usescases.teiDashboard.teiProgramList.TeiProgramListContract;
import org.dhis2.usescases.teiDashboard.teiProgramList.TeiProgramListRepository;
import org.dhis2.usescases.teiDashboard.ui.DetailsButtonKt;
import org.dhis2.utils.DateUtils;
import org.dhis2.utils.category.CategoryDialog;
import org.dhis2.utils.dialFloatingActionButton.DialItem;
import org.dhis2.utils.granularsync.SyncStatusDialog;
import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import timber.log.Timber;

public class TEIDataFragment extends FragmentGlobalAbstract implements TEIDataContracts.View, ProgramDashboardAdapter.OnItemClickListener, TeiProgramListContract.View {

    private static final int REQ_DETAILS = 1001;
    private static final int REQ_EVENT = 2001;

    private static final int RC_GENERATE_EVENT = 1501;
    private static final int RC_EVENTS_COMPLETED = 1601;

    private static final int REFERAL_ID = 3;
    private static final int ADD_NEW_ID = 2;
    private static final int SCHEDULE_ID = 1;

    private static final String PREF_COMPLETED_EVENT = "COMPLETED_EVENT";

    private FragmentTeiDataBinding binding;

    private String programUidBase;

    @Inject
    TEIDataPresenter presenter;

    @Inject
    FilterManager filterManager;

    @Inject
    FiltersAdapter filtersAdapter;

    @Inject
    ThemeManager themeManager;

    @Inject
    D2 d2;


    private EventAdapter adapter;
    private CustomDialog dialog;
    private ProgramStage programStageFromEvent;
    private final ObservableBoolean followUp = new ObservableBoolean(false);
    private EventCatComboOptionSelector eventCatComboOptionSelector;

    private Context context;
    private DashboardViewModel dashboardViewModel;
    private DashboardProgramModel dashboardModel;
    private TeiDashboardMobileActivity activity;
    private PopupMenu popupMenu;

    private ProgramDashboardAdapter programDashboardAdapter;

    private RecyclerView recyclerView;
    public String programUid;

    private Date selectedEnrollmentDate;
    private CompositeDisposable compositeDisposable;

    private TeiProgramListRepository teiProgramListRepository;

    public void setProgramUid(String programUid) {
        this.programUid = programUid;
    }

    public static TEIDataFragment newInstance(String programUid, String teiUid, String enrollmentUid, List<ProgramWithEnrollment> programs) {
        TEIDataFragment fragment = new TEIDataFragment();
        Bundle args = new Bundle();
        args.putString("PROGRAM_UID", programUid);
        args.putString("TEI_UID", teiUid);
        args.putString("ENROLLMENT_UID", enrollmentUid);
        args.putParcelableArrayList("PROGRAMS", (ArrayList<? extends Parcelable>) programs);
//        args.putParcelableArrayList("PROGRAMS_2", (ArrayList<? extends Parcelable>) programs2.blockingSingle());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.setProgramUid(getArguments().getString("PROGRAM_UID"));
        activity = (TeiDashboardMobileActivity) context;
        ((App) context.getApplicationContext())
                .dashboardComponent()
                .plus(new TEIDataModule(this,
                        getArguments().getString("PROGRAM_UID"),
                        getArguments().getString("TEI_UID"),
                        getArguments().getString("ENROLLMENT_UID")
                ))
                .inject(this);

        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onStart() {
        super.onStart();
        dashboardViewModel = ViewModelProviders.of(activity).get(DashboardViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tei_data, container, false);
        binding.setPresenter(presenter);
        activity.observeGrouping().observe(getViewLifecycleOwner(), group -> {
            showLoadingProgress(true);
            binding.setIsGrouping(group);
            presenter.onGroupingChanged(group);
        });
        activity.observeFilters().observe(getViewLifecycleOwner(), this::showHideFilters);
        activity.updatedEnrollment().observe(getViewLifecycleOwner(), this::updateEnrollment);

        try {
            binding.filterLayout.setAdapter(filtersAdapter);
        } catch (Exception e) {
            Timber.e(e);
        }


        binding.recyclerDashboardProgram.setLayoutManager(new LinearLayoutManager(getActivity()));

        programDashboardAdapter = new ProgramDashboardAdapter(getArguments().getParcelableArrayList("PROGRAMS"), programUid, this);
        binding.recyclerDashboardProgram.setAdapter(programDashboardAdapter);


        teiProgramListRepository = new TeiProgramListRepository() {
            @NonNull
            @Override
            public Observable<List<EnrollmentViewModel>> activeEnrollments(String trackedEntityId) {
                return null;
            }

            @NonNull
            @Override
            public Observable<List<EnrollmentViewModel>> otherEnrollments(String trackedEntityId) {
                return null;
            }

            @NonNull
            @Override
            public Flowable<List<ProgramViewModel>> allPrograms(String trackedEntityId) {
                return null;
            }

            @NonNull
            @Override
            public Observable<List<Program>> alreadyEnrolledPrograms(String trackedEntityId) {
                return Observable.fromCallable(() ->
                                d2.enrollmentModule().enrollments()
                                        .byTrackedEntityInstance().eq(trackedEntityId)
                                        .byDeleted().eq(false).blockingGet())
                        .flatMapIterable(enrollments -> enrollments)
                        .map(enrollment -> d2.programModule().programs().byUid().eq(enrollment.program()).one().blockingGet())
                        .toList()
                        .toObservable();
            }

            @NonNull
            @Override
            public Observable<String> saveToEnroll(@NonNull String orgUnit, @NonNull String programUid, @NonNull String teiUid, Date enrollmentDate) {
                return d2.enrollmentModule().enrollments().add(
                                EnrollmentCreateProjection.builder()
                                        .organisationUnit(orgUnit)
                                        .program(programUid)
                                        .trackedEntityInstance(teiUid)
                                        .build())
                        .map(enrollmentUid ->
                                d2.enrollmentModule().enrollments().uid(enrollmentUid))
                        .map(enrollmentRepository -> {
                            if (d2.programModule().programs().uid(programUid).blockingGet().displayIncidentDate()) {
                                enrollmentRepository.setIncidentDate(DateUtils.getInstance().getToday());
                            }
                            enrollmentRepository.setEnrollmentDate(enrollmentDate);
                            enrollmentRepository.setFollowUp(false);
                            return enrollmentRepository.blockingGet().uid();
                        }).toObservable();
            }

            @Override
            public Observable<List<OrganisationUnit>> getOrgUnits(String programUid) {
                if (programUid != null)
                    return d2.organisationUnitModule().organisationUnits().byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                            .byProgramUids(Collections.singletonList(programUid)).get().toObservable();
                else
                    return d2.organisationUnitModule().organisationUnits().byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE).get().toObservable();
            }

            @Override
            public String getProgramColor(@NonNull String programUid) {
                Program program = d2.programModule().programs().byUid().eq(programUid).one().blockingGet();
                return program.style() != null ? program.style().color() : null;
            }

            @Override
            public Program getProgram(String programUid) {
                Program program = d2.programModule().programs().byUid().eq(programUid).one().blockingGet();
                return program;
            }

            @Override
            public ProgramViewModel updateProgramViewModel(ProgramViewModel programViewModel, ProgramDownloadState programDownloadState) {
//                return programViewModelMapper.map(programViewModel, programDownloadState);
                return null;
            }
        };

        return binding.getRoot();
    }

    private void updateEnrollment(String enrollmentUid) {
        presenter.getEnrollment(enrollmentUid);
    }

    private void updateFabItems() {
        List<DialItem> dialItems = new ArrayList<>();
        dialItems.add(
                new DialItem(REFERAL_ID, getString(R.string.referral), R.drawable.ic_arrow_forward)
        );
        dialItems.add(
                new DialItem(ADD_NEW_ID, getString(R.string.add_new), R.drawable.ic_note_add)
        );
        dialItems.add(
                new DialItem(SCHEDULE_ID, getString(R.string.schedule_new), R.drawable.ic_date_range)
        );
        binding.dialFabLayout.addDialItems(dialItems, clickedId -> {
            switch (clickedId) {
                case REFERAL_ID:
                    createEvent(EventCreationType.REFERAL, 0);
                    break;
                case ADD_NEW_ID:
                    createEvent(EventCreationType.ADDNEW, 0);
                    break;
                case SCHEDULE_ID:
                    createEvent(EventCreationType.SCHEDULE, 0);
                    break;
                default:
                    break;
            }
            return Unit.INSTANCE;
        });
    }

    @Override
    public void setEnrollment(Enrollment enrollment) {
        binding.setEnrollment(enrollment);
        dashboardViewModel.updateDashboard(dashboardModel);
        if (adapter != null) {
            adapter.setEnrollment(enrollment);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.init();
        dashboardViewModel.dashboardModel().observe(this, this::setData);
        dashboardViewModel.eventUid().observe(this, this::displayGenerateEvent);
    }

    @Override
    public void onPause() {
        presenter.setOpeningFilterToNone();
        presenter.onDettach();
        super.onPause();
    }

    @Override
    public void setEnrollmentData(Program program, Enrollment enrollment) {
        if (adapter != null) {
            adapter.setEnrollment(enrollment);
        }
        binding.setProgram(program);
        binding.setEnrollment(enrollment);
        if (enrollment != null) {
            followUp.set(enrollment.followUp() != null ? enrollment.followUp() : false);
        }
        binding.setFollowup(followUp);
    }

    @Override
    public void setTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance, OrganisationUnit organisationUnit) {
        binding.setTrackEntity(trackedEntityInstance);
        binding.cardFront.orgUnit.setText(organisationUnit.displayName());
    }

    public void setData(DashboardProgramModel nprogram) {
        this.dashboardModel = nprogram;

        if (nprogram != null && nprogram.getCurrentEnrollment() != null) {
            binding.dialFabLayout.setFabVisible(true);
            presenter.setDashboardProgram(this.dashboardModel);
            eventCatComboOptionSelector = new EventCatComboOptionSelector(nprogram.getCurrentProgram().categoryComboUid(),
                    getChildFragmentManager(),
                    new CategoryDialogInteractions() {
                        @Override
                        public void showDialog(
                                @NonNull String categoryComboUid,
                                @Nullable Date dateControl,
                                @NonNull FragmentManager fragmentManager,
                                @NonNull Function1<? super String, Unit> onItemSelected) {
                            CategoryDialogInteractions.DefaultImpls.showDialog(this,
                                    categoryComboUid,
                                    dateControl,
                                    fragmentManager,
                                    onItemSelected);
                        }
                    });
            binding.setDashboardModel(nprogram);
            updateFabItems();
        } else if (nprogram != null) {
            binding.dialFabLayout.setFabVisible(false);
            binding.teiRecycler.setAdapter(new DashboardProgramAdapter(presenter, nprogram));
            binding.teiRecycler.addItemDecoration(new DividerItemDecoration(getAbstracContext(), DividerItemDecoration.VERTICAL));
            binding.setDashboardModel(nprogram);
            showLoadingProgress(false);
        }

        DetailsButtonKt.setButtonContent(
                binding.cardFront.detailsButton,
                activity.presenter.getTEType(),
                () -> {
                    presenter.seeDetails(binding.cardFront.cardData, dashboardModel);
                    return Unit.INSTANCE;
                }
        );

        binding.executePendingBindings();

        if (getSharedPreferences().getString(PREF_COMPLETED_EVENT, null) != null) {
            presenter.displayGenerateEvent(getSharedPreferences().getString(PREF_COMPLETED_EVENT, null));
            getSharedPreferences().edit().remove(PREF_COMPLETED_EVENT).apply();
        }


    }

    @SuppressLint("CheckResult")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_DETAILS) {
                activity.getPresenter().init();
            }
        }
    }

    @Override
    public void setFilters(List<FilterItem> filterItems) {
        filtersAdapter.submitList(filterItems);
    }

    @Override
    public void hideFilters() {
        activity.hideFilter();
    }

    @Override
    public Flowable<StageSection> observeStageSelection(Program currentProgram, Enrollment currentEnrollment) {
        if (adapter == null) {
            adapter = new EventAdapter(presenter, currentProgram);
            adapter.setEnrollment(currentEnrollment);
            binding.teiRecycler.setAdapter(adapter);
        }
        return adapter.stageSelector();
    }

    @Override
    public void setEvents(List<EventViewModel> events, boolean canAddEvents) {

        binding.setCanAddEvents(canAddEvents);

        if (events.isEmpty()) {
            binding.emptyTeis.setVisibility(View.VISIBLE);
            if (binding.dialFabLayout.isFabVisible()) {
                binding.emptyTeis.setText(R.string.empty_tei_add);
            } else {
                binding.emptyTeis.setText(R.string.empty_tei_no_add);
            }
        } else {
            binding.emptyTeis.setVisibility(View.GONE);
            adapter.submitList(events);

            for (EventViewModel eventViewModel : events) {
                if (eventViewModel.isAfterToday(DateUtils.getInstance().getToday())) {
                    binding.teiRecycler.scrollToPosition(events.indexOf(eventViewModel));
                }
            }
        }
        showLoadingProgress(false);
    }

    private void showLoadingProgress(boolean showProgress) {
        if (showProgress) {
            binding.loadingProgress.getRoot().setVisibility(View.VISIBLE);
        } else {
            binding.loadingProgress.getRoot().setVisibility(View.GONE);
        }
    }

    @Override
    public Consumer<ProgramStage> displayGenerateEvent() {
        return programStageModel -> {
            this.programStageFromEvent = programStageModel;
            if (programStageModel.displayGenerateEventBox() || programStageModel.allowGenerateNextVisit()) {
                dialog = new CustomDialog(
                        getContext(),
                        getString(R.string.dialog_generate_new_event),
                        getString(R.string.message_generate_new_event),
                        getString(R.string.button_ok),
                        getString(R.string.cancel),
                        RC_GENERATE_EVENT,
                        new DialogClickListener() {
                            @Override
                            public void onPositive() {
                                createEvent(EventCreationType.SCHEDULE, programStageFromEvent.standardInterval() != null ? programStageFromEvent.standardInterval() : 0);
                            }

                            @Override
                            public void onNegative() {
                                if (Boolean.TRUE.equals(programStageFromEvent.remindCompleted()))
                                    presenter.areEventsCompleted();
                            }
                        });
                dialog.show();
            } else if (Boolean.TRUE.equals(programStageModel.remindCompleted()))
                showDialogCloseProgram();
        };
    }

    private void showDialogCloseProgram() {

        dialog = new CustomDialog(
                getContext(),
                getString(R.string.event_completed),
                getString(R.string.complete_enrollment_message),
                getString(R.string.button_ok),
                getString(R.string.cancel),
                RC_EVENTS_COMPLETED,
                new DialogClickListener() {
                    @Override
                    public void onPositive() {
                        presenter.completeEnrollment();
                    }

                    @Override
                    public void onNegative() {
                    }
                });
        dialog.show();
    }

    @Override
    public Consumer<Single<Boolean>> areEventsCompleted() {
        return eventsCompleted -> {
            if (eventsCompleted.blockingGet()) {
                dialog = new CustomDialog(
                        getContext(),
                        getString(R.string.event_completed_title),
                        getString(R.string.event_completed_message),
                        getString(R.string.button_ok),
                        getString(R.string.cancel),
                        RC_EVENTS_COMPLETED,
                        new DialogClickListener() {
                            @Override
                            public void onPositive() {
                                presenter.completeEnrollment();
                            }

                            @Override
                            public void onNegative() {
                            }
                        });
                dialog.show();
            }

        };
    }

    @Override
    public Consumer<EnrollmentStatus> enrollmentCompleted() {
        return enrollmentStatus -> {
            if (enrollmentStatus == EnrollmentStatus.COMPLETED)
                activity.updateStatus();
        };
    }

    private void createEvent(EventCreationType eventCreationType, Integer scheduleIntervalDays) {
        if (isAdded()) {
            analyticsHelper().setEvent(TYPE_EVENT_TEI, eventCreationType.name(), CREATE_EVENT_TEI);
            Bundle bundle = new Bundle();
            bundle.putString(PROGRAM_UID, dashboardModel.getCurrentEnrollment().program());
            bundle.putString(TRACKED_ENTITY_INSTANCE, dashboardModel.getTei().uid());
            if (presenter.enrollmentOrgUnitInCaptureScope(dashboardModel.getCurrentOrgUnit().uid())) {
                bundle.putString(ORG_UNIT, dashboardModel.getCurrentOrgUnit().uid());
            }
            bundle.putString(ENROLLMENT_UID, dashboardModel.getCurrentEnrollment().uid());
            bundle.putString(EVENT_CREATION_TYPE, eventCreationType.name());
            bundle.putInt(EVENT_SCHEDULE_INTERVAL, scheduleIntervalDays);
            Intent intent = new Intent(getContext(), ProgramStageSelectionActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, REQ_EVENT);
        }
    }

    @Override
    public void switchFollowUp(boolean followUp) {
        this.followUp.set(followUp);
    }

    @Override
    public void displayGenerateEvent(String eventUid) {
        if (eventUid != null) {
            presenter.displayGenerateEvent(eventUid);
            dashboardViewModel.updateEventUid(null);
        }
    }

    @Override
    public void restoreAdapter(String programUid, String teiUid, String enrollmentUid) {
        activity.startActivity(TeiDashboardMobileActivity.intent(activity, teiUid, programUid, enrollmentUid));
        activity.finish();
    }

    @Override
    public void seeDetails(Intent intent, Bundle bundle) {
        this.startActivityForResult(intent, REQ_DETAILS, bundle);
    }

    @Override
    public void openEventDetails(Intent intent, Bundle bundle) {
        this.startActivityForResult(intent, REQ_EVENT, bundle);
    }

    @Override
    public void openEventInitial(Intent intent) {
        this.startActivityForResult(intent, REQ_EVENT, null);
    }

    @Override
    public void openEventCapture(Intent intent) {
        this.startActivityForResult(intent, REQ_EVENT, null);
    }

    @Override
    public void showTeiImage(String filePath, String defaultIcon) {
        if (filePath.isEmpty() && defaultIcon.isEmpty()) {
            binding.cardFront.teiImage.setVisibility(View.GONE);
        } else {
            binding.cardFront.teiImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(new File(filePath))
                    .error(
                            ObjectStyleUtils.getIconResource(context, defaultIcon, R.drawable.photo_temp_gray)
                    )
                    .transition(withCrossFade())
                    .transform(new CircleCrop())
                    .into(binding.cardFront.teiImage);
            binding.cardFront.teiImage.setOnClickListener(view -> {
                File fileToShow = new File(filePath);
                if (fileToShow.exists()) {
                    new ImageDetailBottomDialog(
                            null,
                            fileToShow
                    ).show(getChildFragmentManager(), ImageDetailBottomDialog.TAG);
                }
            });
        }
    }

    @Override
    public void showNewEventOptions(View anchor, ProgramStage stage) {
        popupMenu = new PopupMenu(context, anchor);
        popupMenu.inflate(R.menu.dashboard_event_creation);

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.schedulenew:
                    goToEventInitial(EventCreationType.SCHEDULE, stage);
                    break;
                case R.id.addnew:
                    goToEventInitial(EventCreationType.ADDNEW, stage);
                    break;
                case R.id.referral:
                    goToEventInitial(EventCreationType.REFERAL, stage);
                    break;
            }
            return true;
        });
        popupMenu.show();

    }

    @Override
    public void hideDueDate() {
        popupMenu.getMenu().findItem(R.id.schedulenew).setVisible(false);
    }

    private void goToEventInitial(EventCreationType eventCreationType, ProgramStage programStage) {
        Intent intent = new Intent(activity, EventInitialActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(PROGRAM_UID, dashboardModel.getCurrentProgram().uid());
        bundle.putString(TRACKED_ENTITY_INSTANCE, dashboardModel.getTei().uid());
        if (presenter.enrollmentOrgUnitInCaptureScope(dashboardModel.getCurrentOrgUnit().uid())) {
            bundle.putString(ORG_UNIT, dashboardModel.getCurrentOrgUnit().uid());
        }
        bundle.putString(ENROLLMENT_UID, dashboardModel.getCurrentEnrollment().uid());
        bundle.putString(EVENT_CREATION_TYPE, eventCreationType.name());
        bundle.putBoolean(EVENT_REPEATABLE, programStage.repeatable());
        bundle.putSerializable(EVENT_PERIOD_TYPE, programStage.periodType());
        bundle.putString(Constants.PROGRAM_STAGE_UID, programStage.uid());
        bundle.putInt(EVENT_SCHEDULE_INTERVAL, programStage.standardInterval() != null ? programStage.standardInterval() : 0);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQ_EVENT);
    }

    private void showHideFilters(boolean showFilters) {
        if (showFilters) {
            ViewAnimationsKt.expand(binding.filterLayout, false, () -> {
                binding.teiData.setVisibility(View.GONE);
                binding.filterLayout.setVisibility(View.VISIBLE);
                return Unit.INSTANCE;
            });

        } else {
            ViewAnimationsKt.collapse(binding.filterLayout, () -> {
                binding.teiData.setVisibility(View.VISIBLE);
                binding.filterLayout.setVisibility(View.GONE);
                return Unit.INSTANCE;
            });


        }
    }

    @Override
    public void showPeriodRequest(FilterManager.PeriodRequest periodRequest) {
        if (periodRequest == FilterManager.PeriodRequest.FROM_TO) {
            DateUtils.getInstance().fromCalendarSelector(
                    activity,
                    FilterManager.getInstance()::addPeriod);
        } else {
            DateUtils.getInstance().showPeriodDialog(
                    activity,
                    FilterManager.getInstance()::addPeriod,
                    true);
        }
    }

    @Override
    public void openOrgUnitTreeSelector(String programUid) {
        new OUTreeFragment.Builder()
                .showAsDialog()
                .withPreselectedOrgUnits(
                        FilterManager.getInstance().getOrgUnitUidsFilters()
                )
                .onSelection(selectedOrgUnits -> {
                    presenter.setOrgUnitFilters((List<OrganisationUnit>) selectedOrgUnits);
                    return Unit.INSTANCE;
                })
                .build().show(getChildFragmentManager(), "OUTreeFragment");
    }

    @Override
    public void showSyncDialog(String eventUid, String enrollmentUid) {
        new SyncStatusDialog.Builder()
                .withContext(this, null)
                .withSyncContext(
                        new SyncContext.EnrollmentEvent(eventUid, enrollmentUid)
                )
                .onDismissListener(hasChanged -> {
                    if (hasChanged)
                        FilterManager.getInstance().publishData();

                }).show(enrollmentUid);
    }

    @Override
    public void displayCatComboOptionSelectorForEvents(List<EventViewModel> data) {
        eventCatComboOptionSelector.setEventsWithoutCatComboOption(data);
        eventCatComboOptionSelector.requestCatComboOption(
                (eventUid, categoryOptionComboUid) -> {
                    presenter.changeCatOption(eventUid, categoryOptionComboUid);
                    return null;
                }
        );
    }

    @Override
    public void showProgramRuleErrorMessage(@NonNull String message) {
        activity.runOnUiThread(() -> showDescription(message));
    }

    @Override
    public void showCatOptComboDialog(String catComboUid) {
        new CategoryDialog(
                CategoryDialog.Type.CATEGORY_OPTION_COMBO,
                catComboUid,
                false,
                null,
                selectedCatOptionCombo -> {
                    presenter.filterCatOptCombo(selectedCatOptionCombo);
                    return null;
                }
        ).show(
                getChildFragmentManager(),
                CategoryDialog.Companion.getTAG()
        );
    }

    @Override
    public void onItemClick(int position, List<ProgramWithEnrollment> mData) {
        String programUid = getArguments().getString("PROGRAM_UID");
        String teiUid = getArguments().getString("TEI_UID");
        String enrollmentUid = getArguments().getString("ENROLLMENT_UID");

        enroll(programUid, teiUid);
    }

    private void showCustomCalendar(String programUid, String uid, OUTreeFragment orgUnitDialog) {
        CalendarPicker dialog = new CalendarPicker(getContext());

        Program selectedProgram = getProgramFromUid(programUid);
        if (selectedProgram != null && !selectedProgram.selectEnrollmentDatesInFuture()) {
            dialog.setMaxDate(new Date(System.currentTimeMillis()));
        }

        if (selectedProgram != null) {
            dialog.setTitle(selectedProgram.enrollmentDateLabel());
        }

        dialog.setListener(new OnDatePickerListener() {
            @Override
            public void onNegativeClick() {
                dialog.dismiss();
            }

            @Override
            public void onPositiveClick(@NotNull DatePicker datePicker) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(Calendar.YEAR, datePicker.getYear());
                selectedCalendar.set(Calendar.MONTH, datePicker.getMonth());
                selectedCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
                selectedCalendar.set(Calendar.MINUTE, 0);
                selectedCalendar.set(Calendar.SECOND, 0);
                selectedCalendar.set(Calendar.MILLISECOND, 0);
                selectedEnrollmentDate = selectedCalendar.getTime();

                compositeDisposable.add(getOrgUnits(programUid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(allOrgUnits -> {
                                    List<OrganisationUnit> orgUnits = filterOrgUnits(allOrgUnits);
                                    handleCalendarResult(orgUnitDialog, orgUnits, programUid, uid);
                                },
                                Timber::d
                        ));
            }
        });

        dialog.show();
    }

    public void enroll(String programUid, String uid) {
        selectedEnrollmentDate = Calendar.getInstance().getTime();
        OUTreeFragment orgUnitDialog = new OUTreeFragment.Builder()
                .showAsDialog()
                .singleSelection()
                .onSelection(selectedOrgUnits -> {
                    if (!selectedOrgUnits.isEmpty())
                        enrollInOrgUnit(selectedOrgUnits.get(0).uid(), programUid, uid, selectedEnrollmentDate);
                    return Unit.INSTANCE;
                })
                .orgUnitScope(new OrgUnitSelectorScope.ProgramCaptureScope(programUid))
                .build();

        showCustomCalendar(programUid, uid, orgUnitDialog);
    }

    private void handleCalendarResult(
            OUTreeFragment orgUnitDialog,
            List<OrganisationUnit> orgUnits,
            String programUid,
            String uid) {
        if (orgUnits.size() > 1) {
            orgUnitDialog.show(getAbstracContext().getSupportFragmentManager(), "OrgUnitEnrollment");
        } else if (!orgUnits.isEmpty()) {
            enrollInOrgUnit(orgUnits.get(0).uid(), programUid, uid, selectedEnrollmentDate);
        } else {
            displayMessage(getContext().getString(R.string.no_org_units));
        }
    }

    private List<OrganisationUnit> filterOrgUnits(List<OrganisationUnit> allOrgUnits) {
        ArrayList<OrganisationUnit> orgUnits = new ArrayList<>();
        for (OrganisationUnit orgUnit : allOrgUnits) {
            boolean afterOpening = false;
            boolean beforeClosing = false;
            if (orgUnit.openingDate() == null || !selectedEnrollmentDate.before(orgUnit.openingDate()))
                afterOpening = true;
            if (orgUnit.closedDate() == null || !selectedEnrollmentDate.after(orgUnit.closedDate()))
                beforeClosing = true;
            if (afterOpening && beforeClosing)
                orgUnits.add(orgUnit);
        }
        return orgUnits;
    }

    public Program getProgramFromUid(String programUid) {
        return teiProgramListRepository.getProgram(programUid);
    }

    public Observable<List<OrganisationUnit>> getOrgUnits(String programUid) {
        return teiProgramListRepository.getOrgUnits(programUid);
    }

    private void enrollInOrgUnit(String orgUnitUid, String programUid, String teiUid, Date enrollmentDate) {
        compositeDisposable.add(
                teiProgramListRepository.saveToEnroll(orgUnitUid, programUid, teiUid, enrollmentDate)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(enrollmentUid -> {
                                    goToEnrollmentScreen(enrollmentUid, programUid);
                                },
                                Timber::d)
        );
    }

    @Override
    public void setActiveEnrollments(List<EnrollmentViewModel> enrollments) {

    }

    @Override
    public void setOtherEnrollments(List<EnrollmentViewModel> enrollments) {

    }

    @Override
    public void setPrograms(List<ProgramViewModel> programs) {

    }

    @Override
    public void goToEnrollmentScreen(String enrollmentUid, String programUid) {
        themeManager.setProgramTheme(programUid);
//        updateToolbar(programUid);
//        Intent data = new Intent();
//        data.putExtra("GO_TO_ENROLLMENT", enrollmentUid);
//        data.putExtra("GO_TO_ENROLLMENT_PROGRAM", programUid);
//        getActivity().setResult(RESULT_OK, data);

        Intent refundActivity = new Intent(getContext(), EnrollmentActivity.class);
        refundActivity.putExtra("amount", enrollmentUid);
        refundActivity.putExtra("amountCredited", programUid);
        startActivity(refundActivity);

//        Intent intent = EnrollmentActivity.Companion.getIntent(this,
//                data.getStringExtra(GO_TO_ENROLLMENT),
//                data.getStringExtra(GO_TO_ENROLLMENT_PROGRAM),
//                EnrollmentActivity.EnrollmentMode.NEW,
//                false);
//        startActivity(intent);
//        finish();


        Intent intent = EnrollmentActivity.Companion.getIntent(getActivity(),
                enrollmentUid,
                programUid,
                EnrollmentActivity.EnrollmentMode.NEW,
                false);
        startActivity(intent);

        getActivity().finish();
    }

    @Override
    public void changeCurrentProgram(String program, String uid) {

    }

    @Override
    public void displayBreakGlassError(String trackedEntityTypeName) {

    }

    @Override
    public void displayAccessError() {

    }

    private void updateToolbar(String programUid) {
//        themeManager.getThemePrimaryColor(
//                programUid,
//                programColor -> {
//                    binding.toolbar.setBackgroundColor(programColor);
//                    return Unit.INSTANCE;
//                },
//                themeColorRes -> {
//                    binding.toolbar.setBackgroundColor(ContextCompat.getColor(this, themeColorRes));
//                    return Unit.INSTANCE;
//                });
    }

//    public Program getProgramFromUid(String programUid) {
//        return teiProgramListRepository.getProgram(programUid);
//    }

}