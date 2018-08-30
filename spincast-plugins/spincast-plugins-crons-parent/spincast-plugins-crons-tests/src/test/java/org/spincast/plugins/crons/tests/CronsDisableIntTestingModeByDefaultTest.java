package org.spincast.plugins.crons.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.crons.SpincastCronJob;
import org.spincast.plugins.crons.SpincastCronJobBase;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Modules;

public class CronsDisableIntTestingModeByDefaultTest extends CronTestBase {

    protected static int[] flag = new int[]{0};

    @Override
    protected void registerTestSpincastCronJobRegistrer(Binder binder) {
        // override, for default behabior
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                Multibinder<SpincastCronJob> cronsMultibinder = Multibinder.newSetBinder(binder(), SpincastCronJob.class);
                cronsMultibinder.addBinding().to(TestCronJob.class).in(Scopes.SINGLETON);
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

    @Test
    public void cronNotRan() throws Exception {

        //==========================================
        // Crons are started async when the 
        // server starts
        //==========================================
        Thread.sleep(200);
        assertEquals(0, flag[0]);
    }

}
