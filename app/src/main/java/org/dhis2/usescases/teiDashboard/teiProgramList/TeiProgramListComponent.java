package org.dhis2.usescases.teiDashboard.teiProgramList;

import org.dhis2.commons.di.dagger.PerActivity;
import org.dhis2.usescases.teiDashboard.TeiDashboardMobileActivity;
import org.dhis2.usescases.teiDashboard.TeiDashboardModule;

import dagger.Subcomponent;

/**
 * Created by Cristian on 13/02/2018.
 *
 */

@PerActivity
@Subcomponent(modules = TeiProgramListModule.class)
//@Subcomponent(modules = {TeiDashboardModule.class, TeiProgramListModule.class})
public interface TeiProgramListComponent {
    void inject(TeiProgramListActivity activity);
    void inject(TeiDashboardMobileActivity mobileActivity);
}