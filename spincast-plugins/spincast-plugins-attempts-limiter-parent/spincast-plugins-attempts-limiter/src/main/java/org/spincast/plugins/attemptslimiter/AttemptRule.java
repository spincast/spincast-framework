package org.spincast.plugins.attemptslimiter;

import java.time.Duration;

/**
 * An attempt rule.
 * <p>
 * You need to register those using
 * {@link AttemptsManager#registerAttempRule(AttemptRule)}.
 */
public interface AttemptRule {

    /**
     * The action to protect.
     */
    public String getActionName();

    /**
     * The number of allowed attempts in a given
     * duration.
     */
    public int getNbrMaxAttemptsPerDuration();

    /**
     * The duration in which a maximum number
     * of attempts is allowed.
     */
    public Duration getDuration();
}
