package org.spincast.plugins.session.config;

import java.util.Set;

import org.spincast.plugins.crons.SpincastCronJob;

import com.google.inject.Provider;

/**
 * Provider for the plugin's cron jobs.
 */
public interface SpincastSessionPluginCronJobProvider extends Provider<Set<SpincastCronJob>> {
    // nothing required
}
