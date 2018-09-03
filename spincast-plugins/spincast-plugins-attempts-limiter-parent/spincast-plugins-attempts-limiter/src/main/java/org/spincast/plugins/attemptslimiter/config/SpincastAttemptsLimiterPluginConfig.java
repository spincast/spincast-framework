package org.spincast.plugins.attemptslimiter.config;

import org.spincast.plugins.attemptslimiter.Attempt;
import org.spincast.plugins.attemptslimiter.AttemptsAutoIncrementType;
import org.spincast.plugins.attemptslimiter.AttemptsManager;

/**
 * Configurations for the Spincast Attempts Limiter plugin.
 */
public interface SpincastAttemptsLimiterPluginConfig {

    /**
     * Should the scheduled task to delete old attempts in the database
     * be automatically added?
     */
    public boolean isAutoBindDeleteOldAttemptsScheduledTask();

    /**
     * The number of minutes between two launches of
     * the scheduled task that will clean the database from old
     * attempts, if {@link #isAutoBindDeleteOldAttemptsScheduledTask()}
     * is enabled.
     */
    public int getDeleteOldAttemptsScheduledTaskIntervalMinutes();

    /**
     * Should the {@link AttemptsManager#attempt(String, org.spincast.plugins.attemptslimiter.AttemptCriteria...)}
     * method automatically increment the number of attempts <em>by default</em>,
     * when not specified otherwise?
     * <p>
     * If you don't let the method increment the number of attempts,
     * you are responsible to call {@link Attempt#incrementAttemptsCount()}
     * by yourself, when required.
     * <p>
     * Defaults to {@link AttemptsAutoIncrementType#ALWAYS}.
     */
    public AttemptsAutoIncrementType getDefaultAttemptAutoIncrementType();

}
