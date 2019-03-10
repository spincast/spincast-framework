package org.spincast.plugins.scheduledtasks.tests;

import java.util.List;
import java.util.Set;

import org.quartz.Scheduler;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTask;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTaskRegister;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTaskRegistrerDefault;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTasksPlugin;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public abstract class ScheduledTaskTestBase extends NoAppStartHttpServerTestingBase {


    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastScheduledTasksPlugin());
        return extraPlugins;
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                registerTestSpincastScheduledTaskRegistrer(binder());
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

    protected void registerTestSpincastScheduledTaskRegistrer(Binder binder) {
        binder.bind(SpincastScheduledTaskRegister.class).to(TestSpincastScheduledTaskRegistrer.class).in(Scopes.SINGLETON);
    }

}
