package org.spincast.plugins.formsprotection.config;

import java.util.Set;

import org.spincast.plugins.crons.SpincastCronJob;

import com.google.inject.Provider;

/**
 * Provider for the plugin's cron jobs.
 */
public interface SpincastFormsProtectionPluginCronJobProvider extends Provider<Set<SpincastCronJob>> {
    // nothing required
}
