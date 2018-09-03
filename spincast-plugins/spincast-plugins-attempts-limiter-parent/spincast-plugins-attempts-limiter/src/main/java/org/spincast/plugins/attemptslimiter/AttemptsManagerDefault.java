package org.spincast.plugins.attemptslimiter;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.plugins.attemptslimiter.config.SpincastAttemptsLimiterPluginConfig;

import com.google.inject.Inject;

public class AttemptsManagerDefault implements AttemptsManager {

    protected final static Logger logger = LoggerFactory.getLogger(AttemptsManagerDefault.class);

    private final SpincastAttemptsLimiterPluginRepository spincastAttemptsLimiterPluginRepository;
    private Map<String, AttemptRule> attemptRulesByActionName;
    private final AttemptFactory attemptFactory;
    private final SpincastAttemptsLimiterPluginConfig spincastAttemptsLimiterPluginConfig;

    @Inject
    public AttemptsManagerDefault(SpincastAttemptsLimiterPluginRepository SpincastAttemptsLimiterPluginRepository,
                                  AttemptFactory attemptFactory,
                                  SpincastAttemptsLimiterPluginConfig spincastAttemptsLimiterPluginConfig) {
        this.spincastAttemptsLimiterPluginRepository = SpincastAttemptsLimiterPluginRepository;
        this.attemptFactory = attemptFactory;
        this.attemptRulesByActionName = new HashMap<String, AttemptRule>();
        this.spincastAttemptsLimiterPluginConfig = spincastAttemptsLimiterPluginConfig;
    }

    protected SpincastAttemptsLimiterPluginRepository getSpincastAttemptsLimiterPluginRepository() {
        return this.spincastAttemptsLimiterPluginRepository;
    }

    @Override
    public Map<String, AttemptRule> getAttemptRulesByActionName() {
        if (this.attemptRulesByActionName == null) {
            this.attemptRulesByActionName = new HashMap<String, AttemptRule>();
        }
        return this.attemptRulesByActionName;
    }

    protected AttemptRule getAttemptRule(String actionName) {
        return getAttemptRulesByActionName().get(actionName);
    }

    protected AttemptFactory getAttemptFactory() {
        return this.attemptFactory;
    }

    protected SpincastAttemptsLimiterPluginConfig getSpincastAttemptsLimiterPluginConfig() {
        return this.spincastAttemptsLimiterPluginConfig;
    }

    @Override
    public Attempt attempt(String actionName, AttemptCriteria... criterias) {
        return attempt(actionName, getSpincastAttemptsLimiterPluginConfig().getDefaultAttemptAutoIncrementType(), criterias);
    }

    @Override
    public Attempt attempt(String actionName,
                           AttemptsAutoIncrementType attemptsAutoIncrementType,
                           AttemptCriteria... criterias) {

        Objects.requireNonNull(attemptsAutoIncrementType, "The attemptsAutoIncrementType can't be NULL");

        AttemptRule attemptRule = getAttemptRule(actionName);
        if (attemptRule == null) {
            logger.error("No " + AttemptRule.class.getSimpleName() + " found for " +
                         "action '" + actionName + "'. The action will be blcoked!");
            return getAttemptFactory().createAttempt(true, actionName, criterias);
        }

        //==========================================
        // Get the number of attempts per criteria 
        // since time X
        //==========================================
        boolean maxReached = false;
        Instant sinceDate = Instant.now().minus(attemptRule.getDuration());
        Map<String, Integer> attemptsNumberPerCriterias =
                getSpincastAttemptsLimiterPluginRepository().getAttemptsNumberPerCriteriaSince(actionName, sinceDate, criterias);
        if (attemptsNumberPerCriterias != null) {

            for (Entry<String, Integer> entry : attemptsNumberPerCriterias.entrySet()) {

                //==========================================
                // Max reached for this criteria?
                //==========================================
                boolean maxReachedForCriteria = (entry.getValue() >= attemptRule.getNbrMaxAttemptsPerDuration());
                if (maxReachedForCriteria) {
                    maxReached = true;

                    StringBuilder b = new StringBuilder("Number of attempts max reached for action '" + actionName +
                                                        "', with criteria '" + entry.getKey() +
                                                        "':\n");
                    for (AttemptCriteria criteria : criterias) {
                        b.append(criteria.getName() + " : " + criteria.getValue() + "\n");
                    }

                    logger.debug(b.toString());
                    break;
                }
            }
        }

        Attempt attempt = getAttemptFactory().createAttempt(maxReached, actionName, criterias);

        //==========================================
        // Should the number of attempts
        // be automatically incremented?
        //==========================================
        if (attemptsAutoIncrementType == AttemptsAutoIncrementType.ALWAYS ||
            (attemptsAutoIncrementType == AttemptsAutoIncrementType.IF_MAX_REACHED && maxReached) ||
            (attemptsAutoIncrementType == AttemptsAutoIncrementType.IF_MAX_NOT_REACHED && !maxReached)) {
            attempt.incrementAttemptsCount();
        }

        return attempt;
    }

    @Override
    public void registerAttempRule(AttemptRule attemptRule) {
        Objects.requireNonNull(attemptRule, "The attemptRule can't be NULL");
        getAttemptRulesByActionName().put(attemptRule.getActionName(), attemptRule);
    }

    @Override
    public void registerAttempRule(String actionName, int nbrMaxAttemptsPerDuration, Duration duration) {

        AttemptRule attemptRule =
                getAttemptFactory().createAttemptRule(actionName,
                                                      nbrMaxAttemptsPerDuration,
                                                      duration);
        registerAttempRule(attemptRule);
    }

}
