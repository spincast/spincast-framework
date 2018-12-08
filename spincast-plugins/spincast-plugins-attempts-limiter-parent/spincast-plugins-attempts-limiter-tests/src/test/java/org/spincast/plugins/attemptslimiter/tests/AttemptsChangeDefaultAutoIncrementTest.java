package org.spincast.plugins.attemptslimiter.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.attemptslimiter.Attempt;
import org.spincast.plugins.attemptslimiter.AttemptCriteria;
import org.spincast.plugins.attemptslimiter.AttemptRule;
import org.spincast.plugins.attemptslimiter.AttemptsAutoIncrementType;
import org.spincast.plugins.attemptslimiter.config.SpincastAttemptsLimiterPluginConfig;
import org.spincast.plugins.attemptslimiter.config.SpincastAttemptsLimiterPluginConfigDefault;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class AttemptsChangeDefaultAutoIncrementTest extends AttemptsLimiterTestBase {


    //==========================================
    // NOTE!
    //
    // Those tests may fail is you debug them slowly
    // since they are based on time.
    //==========================================

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastAttemptsLimiterPluginConfig.class).to(TestAttemptsLimiterPluginConfig.class).in(Scopes.SINGLETON);
            }
        });
    }

    public static class TestAttemptsLimiterPluginConfig extends SpincastAttemptsLimiterPluginConfigDefault {

        @Inject
        public TestAttemptsLimiterPluginConfig(SpincastConfig spincastConfig) {
            super(spincastConfig);
        }

        @Override
        public AttemptsAutoIncrementType getDefaultAttemptAutoIncrementType() {
            return AttemptsAutoIncrementType.NEVER;
        }
    }

    @Override
    protected Set<AttemptRule> getTestSpincastAttemptRules() {

        Set<AttemptRule> set = new HashSet<AttemptRule>();
        set.add(getAttemptFactory().createAttemptRule("login",
                                                      2,
                                                      Duration.of(1, ChronoUnit.SECONDS)));
        set.add(getAttemptFactory().createAttemptRule("confirmOrder",
                                                      1,
                                                      Duration.of(1, ChronoUnit.SECONDS)));
        return set;
    }

    @Test
    public void testDefaultConfigChanged() throws Exception {

        assertEquals(0, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        Attempt attempt = getAttemptsManager().attempt("confirmOrder",
                                                       AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        assertEquals(0, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        assertEquals(0, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        attempt.incrementAttemptsCount();

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptCriteria.of("userId", "toto"));
        assertTrue(attempt.isMaxReached());

        assertEquals(1, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));
    }

}
