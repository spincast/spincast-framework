package org.spincast.plugins.attemptslimiter;

import org.spincast.plugins.attemptslimiter.config.SpincastAttemptsLimiterPluginConfig;

/**
 * Represents the current attempt.
 * <p>
 * Use its {@link #isMaxReached()} method to know
 * if the action associated with this attempt should
 * be blocked or allowed.
 */
public interface Attempt {

    /**
     * Max number of attempts reached. Your
     * code should deny the protected action.
     */
    public boolean isMaxReached();

    /**
     * Increments the number of attempts.
     * <p>
     * You have to call this method manually if
     * you don't let the {@link AttemptsManager#attempt(String, org.spincast.plugins.attemptslimiter.AttemptCriteria...)}
     * method do it.
     * <p>
     * Note that calling this method multiple times won't result in
     * multiple increments. The count will be incremented only once!
     * <p>
     * @see {@link SpincastAttemptsLimiterPluginConfig#getDefaultAttemptAutoIncrementType()}
     * and {@link AttemptsManager#attempt(String, AttemptAutoIncrementType, AttemptCriteria...)}.
     */
    public void incrementAttemptsCount();

    /**
     * Deletes all attempts for this action and criterias.
     * <p>
     * In some situations, you may want to clear the attempts
     * when an action is succesfull.
     */
    public void deleteAttempts();


}
