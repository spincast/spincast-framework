package org.spincast.plugins.crons;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.spincast.core.utils.SpincastStatics;

import com.google.inject.Inject;

public class SpincastCronsSchedulerProviderDefault implements SpincastCronsSchedulerProvider {

    private final SpincastCronJobFactory spincastCronJobFactory;

    @Inject
    public SpincastCronsSchedulerProviderDefault(SpincastCronJobFactory spincastCronJobFactory) {
        this.spincastCronJobFactory = spincastCronJobFactory;
    }

    protected SpincastCronJobFactory getSpincastCronJobFactory() {
        return this.spincastCronJobFactory;
    }

    @Override
    public Scheduler get() {

        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            //==========================================
            // "Clear" is required (at least during during tests
            // because StdSchedulerFactory.getDefaultScheduler()
            // reuses a static instance!
            //==========================================
            scheduler.clear();
            scheduler.setJobFactory(getSpincastCronJobFactory());
            scheduler.start();
            return scheduler;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }
}
