package org.spincast.plugins.crons.tests;

import java.util.List;
import java.util.Set;

import org.quartz.Scheduler;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.crons.SpincastCronJob;
import org.spincast.plugins.crons.SpincastCronJobRegister;
import org.spincast.plugins.crons.SpincastCronJobRegistrerDefault;
import org.spincast.plugins.crons.SpincastCronsPlugin;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public abstract class CronTestBase extends NoAppStartHttpServerTestingBase {


    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastCronsPlugin());
        return extraPlugins;
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                registerTestSpincastCronJobRegistrer(binder());
            }
        });
    }

    public static class TestSpincastCronJobRegistrer extends SpincastCronJobRegistrerDefault {

        @Inject
        public TestSpincastCronJobRegistrer(Set<SpincastCronJob> cronJobs, Set<Set<SpincastCronJob>> cronJobsSets,
                                            Scheduler scheduler, SpincastConfig spincastConfig) {
            super(cronJobs, cronJobsSets, scheduler, spincastConfig);
        }

        @Override
        protected boolean registerCronJobInTestingMode() {
            //==========================================
            // We need to activate the crons even in testing mode
            //==========================================
            return true;
        }
    }

    protected void registerTestSpincastCronJobRegistrer(Binder binder) {
        binder.bind(SpincastCronJobRegister.class).to(TestSpincastCronJobRegistrer.class).in(Scopes.SINGLETON);
    }

}
