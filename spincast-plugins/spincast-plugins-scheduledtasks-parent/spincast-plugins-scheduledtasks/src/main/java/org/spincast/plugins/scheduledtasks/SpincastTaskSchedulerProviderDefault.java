package org.spincast.plugins.scheduledtasks;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.spincast.core.utils.SpincastStatics;

import com.google.inject.Inject;

public class SpincastTaskSchedulerProviderDefault implements SpincastTaskSchedulerProvider {

    private final SpincastScheduledTaskFactory spincastScheduledTaskFactory;

    @Inject
    public SpincastTaskSchedulerProviderDefault(SpincastScheduledTaskFactory spincastScheduledTaskFactory) {
        this.spincastScheduledTaskFactory = spincastScheduledTaskFactory;
    }

    protected SpincastScheduledTaskFactory getSpincastScheduledTaskFactory() {
        return this.spincastScheduledTaskFactory;
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
            scheduler.setJobFactory(getSpincastScheduledTaskFactory());
            scheduler.start();
            return scheduler;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }
}
