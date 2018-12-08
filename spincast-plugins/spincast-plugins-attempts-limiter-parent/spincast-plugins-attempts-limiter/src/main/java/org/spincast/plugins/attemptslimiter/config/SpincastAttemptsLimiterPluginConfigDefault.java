package org.spincast.plugins.attemptslimiter.config;

import org.spincast.core.config.SpincastConfig;
import org.spincast.plugins.attemptslimiter.AttemptsAutoIncrementType;

import com.google.inject.Inject;

/**
 * Default configurations for Spincast Attempts Limiter plugin.
 */
public class SpincastAttemptsLimiterPluginConfigDefault implements SpincastAttemptsLimiterPluginConfig {

    private final SpincastConfig spincastConfig;

    @Inject
    public SpincastAttemptsLimiterPluginConfigDefault(SpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

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

    @Override
    public boolean isValidationEnabled() {
        return !getSpincastConfig().isDevelopmentMode();
    }
}
