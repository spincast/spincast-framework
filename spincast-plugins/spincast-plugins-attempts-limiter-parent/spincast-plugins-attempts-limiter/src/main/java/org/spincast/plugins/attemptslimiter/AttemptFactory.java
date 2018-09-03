package org.spincast.plugins.attemptslimiter;

import java.time.Duration;

public interface AttemptFactory {

    public Attempt createAttempt(boolean maxReached,
                                 String actionName,
                                 AttemptCriteria... criterias);

    public AttemptRule createAttemptRule(String actionName,
                                         int nbrMaxAttemptsPerDuration,
                                         Duration duration);

}
