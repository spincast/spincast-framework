package org.spincast.plugins.attemptslimiter;

import java.util.HashSet;
import java.util.Set;

import org.spincast.plugins.attemptslimiter.config.SpincastAttemptsLimiterPluginConfig;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTask;

import com.google.inject.Inject;

/**
 * Provider of scheduled tasks 
 */
public class SpincastAttemptsLimiterPluginScheduledTaskProviderDefault implements
                                                                       SpincastAttemptsLimiterPluginScheduledTaskProvider {

    private final SpincastAttemptsLimiterPluginConfig spincastAttemptsLimiterPluginConfig;
    private final DeleteOldAttemptsScheduledTask deleteOldAttemptsScheduledTask;

    @Inject
    public SpincastAttemptsLimiterPluginScheduledTaskProviderDefault(SpincastAttemptsLimiterPluginConfig spincastAttemptsLimiterPluginConfig,
                                                                     DeleteOldAttemptsScheduledTask deleteOldAttemptsScheduledTask) {
        this.spincastAttemptsLimiterPluginConfig = spincastAttemptsLimiterPluginConfig;
        this.deleteOldAttemptsScheduledTask = deleteOldAttemptsScheduledTask;
    }

    protected SpincastAttemptsLimiterPluginConfig getSpincastAttemptsLimiterPluginConfig() {
        return this.spincastAttemptsLimiterPluginConfig;
    }

    protected DeleteOldAttemptsScheduledTask getDeleteOldAttemptsScheduledTask() {
        return this.deleteOldAttemptsScheduledTask;
    }

    @Override
    public Set<SpincastScheduledTask> get() {

        Set<SpincastScheduledTask> scheduledTasks = new HashSet<SpincastScheduledTask>();

        if (getSpincastAttemptsLimiterPluginConfig().isAutoBindDeleteOldAttemptsScheduledTask()) {
            scheduledTasks.add(getDeleteOldAttemptsScheduledTask());
        }

        return scheduledTasks;
    }

}
