package org.spincast.plugins.session.config;

import java.util.Set;

import org.spincast.plugins.scheduledtasks.SpincastScheduledTask;

import com.google.inject.Provider;

/**
 * Provider for the plugin's scheduled tasks.
 */
public interface SpincastSessionPluginScheduledTaskProvider extends Provider<Set<SpincastScheduledTask>> {
    // nothing required
}
