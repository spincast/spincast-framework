package org.spincast.plugins.attemptslimiter;

import java.time.Duration;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class AttemptRuleDefault implements AttemptRule {

    private final String actionName;
    private final int nbrMaxAttemptsPerDuration;
    private final Duration duration;

    @AssistedInject
    public AttemptRuleDefault(@Assisted String actionName,
                              @Assisted int nbrMaxAttemptsPerDuration,
                              @Assisted Duration duration) {
        super();
        this.actionName = actionName;
        this.nbrMaxAttemptsPerDuration = nbrMaxAttemptsPerDuration;
        this.duration = duration;
    }

    @Override
    public String getActionName() {
        return this.actionName;
    }

    @Override
    public int getNbrMaxAttemptsPerDuration() {
        return this.nbrMaxAttemptsPerDuration;
    }

    @Override
    public Duration getDuration() {
        return this.duration;
    }
}
