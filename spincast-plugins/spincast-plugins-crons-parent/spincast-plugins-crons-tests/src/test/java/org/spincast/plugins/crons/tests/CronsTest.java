package org.spincast.plugins.crons.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import org.junit.Test;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.crons.SpincastCronJob;
import org.spincast.plugins.crons.SpincastCronJobBase;

import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Modules;

public class CronsTest extends CronTestBase {

    protected static int[] flag = new int[]{0};
    protected static int[] flag2 = new int[]{0};

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                Multibinder<SpincastCronJob> cronsMultibinder = Multibinder.newSetBinder(binder(), SpincastCronJob.class);
                cronsMultibinder.addBinding().to(TestCronJob.class).in(Scopes.SINGLETON);
                cronsMultibinder.addBinding().to(TestCronJob2.class).in(Scopes.SINGLETON);
            }
        });
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
