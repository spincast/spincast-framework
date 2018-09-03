package org.spincast.plugins.attemptslimiter;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class AttemptDefault implements Attempt {

    private final boolean maxReached;
    private final String actionName;
    private final AttemptCriteria[] criterias;
    private final SpincastAttemptsLimiterPluginRepository spincastAttemptsLimiterPlguinRepository;
    private boolean attemptsCountIncremented = false;

    @AssistedInject
    public AttemptDefault(SpincastAttemptsLimiterPluginRepository spincastAttemptsLimiterPlguinRepository,
                          @Assisted boolean maxReached,
                          @Assisted String actionName,
                          @Assisted AttemptCriteria... criterias) {
        this.spincastAttemptsLimiterPlguinRepository = spincastAttemptsLimiterPlguinRepository;
        this.maxReached = maxReached;
        this.actionName = actionName;
        this.criterias = criterias;
    }

    protected SpincastAttemptsLimiterPluginRepository getSpincastAttemptsLimiterPluginRepository() {
        return this.spincastAttemptsLimiterPlguinRepository;
    }

    public String getActionName() {
        return this.actionName;
    }

    public AttemptCriteria[] getCriterias() {
        return this.criterias;
    }

    @Override
    public boolean isMaxReached() {
        return this.maxReached;
    }

    @Override
    public void incrementAttemptsCount() {
        if (this.attemptsCountIncremented) {
            return;
        }
        this.attemptsCountIncremented = true;
        getSpincastAttemptsLimiterPluginRepository().saveNewAttempt(getActionName(), getCriterias());
    }

    @Override
    public void deleteAttempts() {
        getSpincastAttemptsLimiterPluginRepository().deleteAttempts(getActionName(), getCriterias());
    }

    @Override
    public String toString() {
        return getActionName() + (isMaxReached() ? " - max reached!" : " - allowed");
    }

}
