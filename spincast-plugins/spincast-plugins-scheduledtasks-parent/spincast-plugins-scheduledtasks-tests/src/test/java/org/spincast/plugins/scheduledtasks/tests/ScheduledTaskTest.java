package org.spincast.plugins.scheduledtasks.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import org.junit.Test;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTask;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTaskBase;

import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Modules;

public class ScheduledTaskTest extends ScheduledTaskTestBase {

    protected static int[] flag = new int[]{0};
    protected static int[] flag2 = new int[]{0};

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                Multibinder<SpincastScheduledTask> scheduledTaskMultibinder =
                        Multibinder.newSetBinder(binder(), SpincastScheduledTask.class);
                scheduledTaskMultibinder.addBinding().to(TestScheduledTask.class).in(Scopes.SINGLETON);
                scheduledTaskMultibinder.addBinding().to(TestScheduledTask2.class).in(Scopes.SINGLETON);
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

    public static class TestScheduledTask2 extends SpincastScheduledTaskBase {

        @Override
        public Trigger getTrigger() {
            return TriggerBuilder.newTrigger()
                                 .startNow()
                                 .withSchedule(simpleSchedule().withIntervalInMilliseconds(50).repeatForever())
                                 .build();
        }

        @Override
        protected void executeSafe(JobExecutionContext context) {
            flag2[0]++;
        }
    }

    @Test
    public void scheduledTaskRan() throws Exception {

        //==========================================
        // Scheduled Tasks are started async when the 
        // server starts
        //==========================================
        Thread.sleep(300);
        assertEquals(1, flag[0]);
        assertTrue(flag2[0] > 4);
    }

}
