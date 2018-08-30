package org.spincast.plugins.crons.tests;

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
import org.spincast.plugins.crons.SpincastCronJob;
import org.spincast.plugins.crons.SpincastCronJobBase;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Modules;

public class CronsSetBindingTest extends CronTestBase {

    protected static int[] flag = new int[]{0};
    protected static int[] flag2 = new int[]{0};

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                Multibinder<Set<SpincastCronJob>> cronsSetsMultibinder =
                        Multibinder.newSetBinder(binder(), Key.get(new TypeLiteral<Set<SpincastCronJob>>() {}));
                cronsSetsMultibinder.addBinding().toProvider(TestCronsProvider.class);
            }
        });
    }

    public static class TestCronsProvider implements Provider<Set<SpincastCronJob>> {

        private final TestCronJob testCronJob;
        private final TestCronJob2 testCronJob2;

        @Inject
        public TestCronsProvider(TestCronJob testCronJob, TestCronJob2 testCronJob2) {
            this.testCronJob = testCronJob;
            this.testCronJob2 = testCronJob2;
        }

        @Override
        public Set<SpincastCronJob> get() {
            Set<SpincastCronJob> crons = new HashSet<SpincastCronJob>();
            crons.add(this.testCronJob);
            crons.add(this.testCronJob2);
            return crons;
        }
    }

    public static class TestCronJob extends SpincastCronJobBase {

        @Override
        public Trigger getTrigger() {
            return TriggerBuilder.newTrigger().startNow().build();
        }

        @Override
        protected void executeSafe(JobExecutionContext context) {
            flag[0]++;
        }
    }

    public static class TestCronJob2 extends SpincastCronJobBase {

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
    public void cronRan() throws Exception {

        //==========================================
        // Crons are started async when the 
        // server starts
        //==========================================
        Thread.sleep(300);
        assertEquals(1, flag[0]);
        assertTrue(flag2[0] > 4);
    }

}
