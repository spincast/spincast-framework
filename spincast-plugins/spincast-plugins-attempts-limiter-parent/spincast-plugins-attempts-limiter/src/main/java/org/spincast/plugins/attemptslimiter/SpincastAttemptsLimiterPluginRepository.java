package org.spincast.plugins.attemptslimiter;

import java.time.Instant;
import java.util.Map;

/**
 * Repository for attempts.
 */
public interface SpincastAttemptsLimiterPluginRepository {

    /**
     * Save a new attempt for an action, given some criterias.
     */
    public void saveNewAttempt(String actionName, AttemptCriteria... criterias);

    /**
     * Returne the number of attempt made for an action, by criterias. 
     * The keys are the criterias' names.
     */
    public Map<String, Integer> getAttemptsNumberPerCriteriaSince(String actionName,
                                                                  Instant sinceDate,
                                                                  AttemptCriteria... criterias);

    /**
     * Deletes all attempts of the action older than 
     * the specified date.
     */
    public void deleteAttemptsOlderThan(String actionName, Instant date);

    /**
     * Deletes all attempts of the specified action and criterias.
     */
    public void deleteAttempts(String actionName, AttemptCriteria... criterias);

}
