package org.spincast.plugins.scheduledtasks.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTask;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTaskBase;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Modules;

public class ScheduledTaskDisableIntTestingModeByDefaultTest extends ScheduledTaskTestBase {

    protected static int[] flag = new int[]{0};

    @Override
    protected void registerTestSpincastScheduledTaskRegistrer(Binder binder) {
        // override, for default behabior
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                Multibinder<SpincastScheduledTask> scheduledTasksMultibinder =
                        Multibinder.newSetBinder(binder(), SpincastScheduledTask.class);
                scheduledTasksMultibinder.addBinding().to(TestScheduledTask.class).in(Scopes.SINGLETON);
            }
        });
    }

    public static class TestScheduledTask extends SpincastScheduledTaskBase {

        @Override
        public Trigger getTrigger() {
            return TriggerBuilder.newTrigger().startNow().build();
        }

        @Override
        protected void executeSafe(JobExecutionContext context) {
            flag[0]++;
        }
    }

    @Test
    public void scheduledTaskNotRan() throws Exception {

        //==========================================
        // Scheduled Tasks are started async when the 
        // server starts
        //==========================================
        Thread.sleep(200);
        assertEquals(0, flag[0]);
    }

}
