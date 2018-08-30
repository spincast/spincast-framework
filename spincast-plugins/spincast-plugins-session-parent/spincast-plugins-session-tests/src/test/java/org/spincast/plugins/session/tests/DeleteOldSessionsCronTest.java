package org.spincast.plugins.session.tests;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;
import org.quartz.Scheduler;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.crons.SpincastCronJob;
import org.spincast.plugins.crons.SpincastCronJobRegister;
import org.spincast.plugins.crons.SpincastCronJobRegistrerDefault;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class DeleteOldSessionsCronTest extends SessionTestBase {

    @Override
    public void beforeClassException(Throwable ex) {
        super.beforeClassException(ex);
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastCronJobRegister.class).to(TestSpincastCronJobRegistrer.class).in(Scopes.SINGLETON);
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

    @Override
    public void beforeClass() {
        deleteOldSessionsCalled[0] = 0;
        super.beforeClass();
    }

    @Test
    public void test() throws Exception {
        Thread.sleep(500);
        assertEquals(1, deleteOldSessionsCalled[0]);
    }
}
