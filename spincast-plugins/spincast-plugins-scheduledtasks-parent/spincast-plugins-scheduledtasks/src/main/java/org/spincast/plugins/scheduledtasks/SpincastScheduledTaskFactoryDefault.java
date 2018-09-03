package org.spincast.plugins.scheduledtasks;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;


public class SpincastScheduledTaskFactoryDefault implements SpincastScheduledTaskFactory {

    private final Provider<Injector> injectorProvider;

    @Inject
    public SpincastScheduledTaskFactoryDefault(Provider<Injector> injectorProvider) {
        this.injectorProvider = injectorProvider;
    }

    public Injector getInjector() {
        return this.injectorProvider.get();
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        Class<? extends Job> clazz = bundle.getJobDetail().getJobClass();
        return getInjector().getInstance(clazz);
    }
}
