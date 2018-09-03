package org.spincast.plugins.attemptslimiter.config;

import org.spincast.plugins.attemptslimiter.AttemptsAutoIncrementType;

/**
 * Default configurations for Spincast Attempts Limiter plugin.
 */
public class SpincastAttemptsLimiterPluginConfigDefault implements SpincastAttemptsLimiterPluginConfig {

    @Override
    public boolean isAutoBindDeleteOldAttemptsScheduledTask() {
        return true;
    }

    @Override
    public int getDeleteOldAttemptsScheduledTaskIntervalMinutes() {
        return 10;
    }

    @Override
    public AttemptsAutoIncrementType getDefaultAttemptAutoIncrementType() {
        return AttemptsAutoIncrementType.ALWAYS;
    }
}
