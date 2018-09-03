package org.spincast.plugins.session.tests;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;
import org.quartz.Scheduler;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTask;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTaskRegister;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTaskRegistrerDefault;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class DeleteOldSessionsScheduledTaskTest extends SessionTestBase {

    @Override
    public void beforeClassException(Throwable ex) {
        super.beforeClassException(ex);
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastScheduledTaskRegister.class).to(TestSpincastScheduledTaskRegistrer.class).in(Scopes.SINGLETON);
            }
        });
    }

    public static class TestSpincastScheduledTaskRegistrer extends SpincastScheduledTaskRegistrerDefault {

        @Inject
        public TestSpincastScheduledTaskRegistrer(Set<SpincastScheduledTask> scheduledTasks,
                                                  Set<Set<SpincastScheduledTask>> scheduledTaskSets,
                                                  Scheduler scheduler, SpincastConfig spincastConfig) {
            super(scheduledTasks, scheduledTaskSets, scheduler, spincastConfig);
        }

        @Override
        protected boolean registerScheduledTasksInTestingMode() {
            //==========================================
            // We need to activate the scheduled tasks even in testing mode
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
        Thread.sleep(1000);
        assertEquals(1, deleteOldSessionsCalled[0]);
    }
}
