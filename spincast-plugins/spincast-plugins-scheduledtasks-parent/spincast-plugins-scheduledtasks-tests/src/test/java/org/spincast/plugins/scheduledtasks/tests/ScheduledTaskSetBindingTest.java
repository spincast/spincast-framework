package org.spincast.plugins.scheduledtasks.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTask;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTaskBase;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Modules;

public class ScheduledTaskSetBindingTest extends ScheduledTaskTestBase {

    protected static int[] flag = new int[]{0};
    protected static int[] flag2 = new int[]{0};

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                Multibinder<Set<SpincastScheduledTask>> scheduledTaskSetsMultibinder =
                        Multibinder.newSetBinder(binder(), Key.get(new TypeLiteral<Set<SpincastScheduledTask>>() {}));
                scheduledTaskSetsMultibinder.addBinding().toProvider(TestScheduledTasksProvider.class);
            }
        });
    }

    public static class TestScheduledTasksProvider implements Provider<Set<SpincastScheduledTask>> {

        private final TestScheduledTask testScheduledTask;
        private final TestScheduledTask2 testScheduledTask2;

        @Inject
        public TestScheduledTasksProvider(TestScheduledTask testScheduledTask, TestScheduledTask2 testScheduledTask2) {
            this.testScheduledTask = testScheduledTask;
            this.testScheduledTask2 = testScheduledTask2;
        }

        @Override
        public Set<SpincastScheduledTask> get() {
            Set<SpincastScheduledTask> scheduledTasks = new HashSet<SpincastScheduledTask>();
            scheduledTasks.add(this.testScheduledTask);
            scheduledTasks.add(this.testScheduledTask2);
            return scheduledTasks;
        }
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
