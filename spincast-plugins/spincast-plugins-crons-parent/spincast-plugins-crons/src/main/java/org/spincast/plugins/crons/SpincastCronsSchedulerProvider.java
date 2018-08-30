package org.spincast.plugins.crons;

import org.quartz.Scheduler;

import com.google.inject.Provider;

/**
 * Custom Scheduler that uses our own cron job factory.
 */
public interface SpincastCronsSchedulerProvider extends Provider<Scheduler> {
    // nothing required
}
