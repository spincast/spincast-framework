package org.spincast.plugins.formsprotection.config;

import java.util.Set;

import org.spincast.plugins.scheduledtasks.SpincastScheduledTask;

import com.google.inject.Provider;

/**
 * Provider for the plugin's scheduled tasks.
 */
public interface SpincastFormsProtectionPluginScheduledTaskProvider extends Provider<Set<SpincastScheduledTask>> {
    // nothing required
}
