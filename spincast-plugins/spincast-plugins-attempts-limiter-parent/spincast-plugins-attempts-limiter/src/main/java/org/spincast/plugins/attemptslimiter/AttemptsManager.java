package org.spincast.plugins.attemptslimiter;

import java.time.Duration;
import java.util.Map;

import org.spincast.plugins.attemptslimiter.config.SpincastAttemptsLimiterPluginConfig;

/**
 * Attempts manager.
 */
public interface AttemptsManager {

    /**
     * Gets the current {@link Attempt}, given the action to protect
     * and criterias. With this object you can:
     * <ul>
     * <li>
     * Know if the action should be allowed or blocked,
     * by calling {@link Attempt#isMaxReached()}.
     * </li>
     * <li>
     * Manually increment the number of attempts using {@link Attempt#incrementAttemptsCount()}, 
     * if you didn't let the {{@link #attempt(String, AttemptCriteria...)}} method do
     * it automatically 
     * (see {@link SpincastAttemptsLimiterPluginConfig#getDefaultAttemptAutoIncrementType()}).
     * </li>
     * </ul>
     * <p>
     */
    public Attempt attempt(String attemptName, AttemptCriteria... criterias);

    /**
     * Gets the current {@link Attempt}, given the action to protect
     * and criterias. With this object you can:
     * <ul>
     * <li>
     * Know if the action should be allowed or blocked,
     * by calling {@link Attempt#isMaxReached()}.
     * </li>
     * <li>
     * Manually increment the number of attempts using {@link Attempt#incrementAttemptsCount()}, 
     * if you didn't let the {{@link #attempt(String, AttemptAutoIncrementType, AttemptCriteria...))}} method do
     * it automatically.
     * </li>
     * </ul>
     * 
     * @param attemptsAutoIncrementType Specifies if the method should increment the
     * number of attempts by itself. If you don't let it do it, you need to increment
     * the number of attempts by yourself, by calling {@link Attempt#incrementAttemptsCount()}
     * on the returned object. 
     */
    public Attempt attempt(String attemptName,
                           AttemptsAutoIncrementType attemptsAutoIncrementType,
                           AttemptCriteria... criterias);

    /**
     * Registers a {@link AttemptRule} rule.
     */
    public void registerAttempRule(AttemptRule attemptRule);

    /**
     * Creates and registers a {@link AttemptRule} 
     * rule from the required informations.
     */
    public void registerAttempRule(String actionName,
                                   int nbrMaxAttemptsPerDuration,
                                   Duration duration);

    /**
     * Gets all the registered {@link AttemptRule} rules, 
     * by action names.
     */
    public Map<String, AttemptRule> getAttemptRulesByActionName();

}
