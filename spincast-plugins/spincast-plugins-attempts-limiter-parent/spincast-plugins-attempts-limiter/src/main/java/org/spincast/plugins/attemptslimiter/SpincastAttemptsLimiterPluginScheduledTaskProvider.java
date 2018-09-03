package org.spincast.plugins.attemptslimiter;

import java.util.Set;

import org.spincast.plugins.scheduledtasks.SpincastScheduledTask;

import com.google.inject.Provider;

/**
 * Provider of scheduled tasks.
 */
public interface SpincastAttemptsLimiterPluginScheduledTaskProvider extends Provider<Set<SpincastScheduledTask>> {
    // nothing required
}
