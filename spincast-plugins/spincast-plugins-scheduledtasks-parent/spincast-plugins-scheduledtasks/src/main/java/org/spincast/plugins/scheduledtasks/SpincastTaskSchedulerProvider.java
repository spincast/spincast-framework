package org.spincast.plugins.scheduledtasks;

import org.quartz.Scheduler;

import com.google.inject.Provider;

/**
 * Custom Scheduler that uses our own scheduled tasks factory.
 */
public interface SpincastTaskSchedulerProvider extends Provider<Scheduler> {
    // nothing required
}
