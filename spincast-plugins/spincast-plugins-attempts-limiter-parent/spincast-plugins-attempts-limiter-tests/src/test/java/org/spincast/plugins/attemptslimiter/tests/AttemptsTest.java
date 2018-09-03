package org.spincast.plugins.attemptslimiter.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.spincast.plugins.attemptslimiter.Attempt;
import org.spincast.plugins.attemptslimiter.AttemptCriteria;
import org.spincast.plugins.attemptslimiter.AttemptRule;
import org.spincast.plugins.attemptslimiter.AttemptsAutoIncrementType;

public class AttemptsTest extends AttemptsLimiterTestBase {


    //==========================================
    // NOTE!
    //
    // Those tests may fail is you debug them slowly
    // since they are based on time.
    //==========================================


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
    public void actionNameDoesntExist() throws Exception {
        Attempt attempt =
                getAttemptsManager().attempt("nope", AttemptCriteria.of("tutu", "titi"));
        assertTrue(attempt.isMaxReached());
    }

    @Test
    public void simple() throws Exception {

        Attempt attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("userId", "toto"));
        assertTrue(attempt.isMaxReached());

        Thread.sleep(1000);

        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("userId", "toto"));
        assertTrue(attempt.isMaxReached());
    }

    @Test
    public void differentCriterias() throws Exception {

        //==========================================
        // Valid attempts
        //==========================================
        Attempt attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("ip", "127.0.0.1"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("ip", "127.0.0.1"));
        assertFalse(attempt.isMaxReached());

        //==========================================
        // Too many attempts for those
        //==========================================
        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("userId", "toto"));
        assertTrue(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("ip", "127.0.0.1"));
        assertTrue(attempt.isMaxReached());

        //==========================================
        // Other values for the same criterias is
        // ok!
        //==========================================
        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("userId", "toto2"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("ip", "127.0.0.2"));
        assertFalse(attempt.isMaxReached());

        //==========================================
        // Sleep
        //==========================================
        Thread.sleep(1000);

        //==========================================
        // Ok again
        //==========================================
        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("ip", "127.0.0.1"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("ip", "127.0.0.1"));
        assertFalse(attempt.isMaxReached());

        //==========================================
        // Too many attempts again
        //==========================================
        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("userId", "toto"));
        assertTrue(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login", AttemptCriteria.of("ip", "127.0.0.1"));
        assertTrue(attempt.isMaxReached());
    }

    @Test
    public void multiCriteriasAtLeastOneInvalid() throws Exception {

        Attempt attempt = getAttemptsManager().attempt("login",
                                                       AttemptCriteria.of("userId", "toto"),
                                                       AttemptCriteria.of("other2", "val2"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login",
                                               AttemptCriteria.of("userId", "toto"),
                                               AttemptCriteria.of("other2", "val2"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login",
                                               AttemptCriteria.of("userId", "new"),
                                               AttemptCriteria.of("other2", "val2"));
        assertTrue(attempt.isMaxReached());
    }

    @Test
    public void multiCriteriasAtLeastOneInvalid2() throws Exception {

        Attempt attempt = getAttemptsManager().attempt("login",
                                                       AttemptCriteria.of("AAAAA", "11111"),
                                                       AttemptCriteria.of("BBBBB", "22222"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login",
                                               AttemptCriteria.of("AAAAA", "11111"),
                                               AttemptCriteria.of("BBBBB", "22222"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login",
                                               AttemptCriteria.of("CCCCC", "33333"),
                                               AttemptCriteria.of("DDDDD", "44444"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login",
                                               AttemptCriteria.of("CCCCC", "33333"),
                                               AttemptCriteria.of("DDDDD", "44444"));
        assertFalse(attempt.isMaxReached());

        //==========================================
        // Still one criteria with too many attempt
        //==========================================

        attempt = getAttemptsManager().attempt("login",
                                               AttemptCriteria.of("AAAAA", "11111"),
                                               AttemptCriteria.of("BBBBB", "ok"));
        assertTrue(attempt.isMaxReached());


        attempt = getAttemptsManager().attempt("login",
                                               AttemptCriteria.of("AAAAA", "ok"),
                                               AttemptCriteria.of("BBBBB", "22222"));
        assertTrue(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login",
                                               AttemptCriteria.of("CCCCC", "33333"),
                                               AttemptCriteria.of("DDDDD", "ok"));
        assertTrue(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login",
                                               AttemptCriteria.of("CCCCC", "ok"),
                                               AttemptCriteria.of("DDDDD", "44444"));
        assertTrue(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("login",
                                               AttemptCriteria.of("AAAAA", "11111"),
                                               AttemptCriteria.of("FFFFF", "55555"));
        assertTrue(attempt.isMaxReached());

        //==========================================
        // One attempt criteria = ok
        //==========================================
        attempt = getAttemptsManager().attempt("login",
                                               AttemptCriteria.of("FFFFF", "55555"));
        assertFalse(attempt.isMaxReached());
    }

    @Test
    public void onePerSecond() throws Exception {

        Attempt attempt = getAttemptsManager().attempt("confirmOrder",
                                                       AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptCriteria.of("userId", "toto"));
        assertTrue(attempt.isMaxReached());

        Thread.sleep(1100);

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());
    }

    @Test
    public void autoIncrementDefault() throws Exception {

        Attempt attempt = getAttemptsManager().attempt("confirmOrder",
                                                       AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptCriteria.of("userId", "toto"));
        assertTrue(attempt.isMaxReached());
    }

    @Test
    public void autoIncrementAlwaysExplicit() throws Exception {

        Attempt attempt = getAttemptsManager().attempt("confirmOrder",
                                                       AttemptsAutoIncrementType.ALWAYS,
                                                       AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptCriteria.of("userId", "toto"));
        assertTrue(attempt.isMaxReached());
    }

    @Test
    public void autoIncrementNever() throws Exception {

        assertEquals(0, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        Attempt attempt = getAttemptsManager().attempt("confirmOrder",
                                                       AttemptsAutoIncrementType.NEVER,
                                                       AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptsAutoIncrementType.NEVER,
                                               AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptsAutoIncrementType.NEVER,
                                               AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptsAutoIncrementType.NEVER,
                                               AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        assertEquals(0, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));
    }

    @Test
    public void autoIncrementNeverManually() throws Exception {

        assertEquals(0, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));


        Attempt attempt = getAttemptsManager().attempt("confirmOrder",
                                                       AttemptsAutoIncrementType.NEVER,
                                                       AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        assertEquals(0, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        attempt.incrementAttemptsCount();

        assertEquals(1, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptsAutoIncrementType.NEVER,
                                               AttemptCriteria.of("userId", "toto"));
        assertTrue(attempt.isMaxReached());

        assertEquals(1, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));
    }

    @Test
    public void autoIncrementIfMaxReached() throws Exception {

        assertEquals(0, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        Attempt attempt = getAttemptsManager().attempt("confirmOrder",
                                                       AttemptsAutoIncrementType.IF_MAX_REACHED,
                                                       AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptsAutoIncrementType.IF_MAX_REACHED,
                                               AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptsAutoIncrementType.IF_MAX_REACHED,
                                               AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptsAutoIncrementType.IF_MAX_REACHED,
                                               AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        assertEquals(0, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));
    }

    @Test
    public void autoIncrementIfMaxReached2() throws Exception {

        assertEquals(0, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        Attempt attempt = getAttemptsManager().attempt("confirmOrder",
                                                       AttemptsAutoIncrementType.IF_MAX_REACHED,
                                                       AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        assertEquals(0, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        assertEquals(1, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptsAutoIncrementType.IF_MAX_REACHED,
                                               AttemptCriteria.of("userId", "toto"));
        assertTrue(attempt.isMaxReached());

        assertEquals(2, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));
    }

    @Test
    public void attemptRulesByActionName() throws Exception {
        Map<String, AttemptRule> attemptRulesByActionName = getAttemptsManager().getAttemptRulesByActionName();
        assertEquals(2, attemptRulesByActionName.size());
    }

    @Test
    public void onlyIncrementOnce() throws Exception {

        assertEquals(0, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        Attempt attempt = getAttemptsManager().attempt("confirmOrder",
                                                       AttemptCriteria.of("userId", "toto"));

        assertEquals(1, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        attempt.incrementAttemptsCount();
        attempt.incrementAttemptsCount();

        assertEquals(1, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));
    }

    @Test
    public void deleteAttempts() throws Exception {

        assertEquals(0, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        Attempt attempt = getAttemptsManager().attempt("confirmOrder",
                                                       AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        assertEquals(1, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptCriteria.of("userId", "toto"));
        assertTrue(attempt.isMaxReached());

        assertEquals(2, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        // Other criteria
        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptCriteria.of("userId", "titititittiti"));
        assertFalse(attempt.isMaxReached());

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptCriteria.of("userId", "toto"));
        assertTrue(attempt.isMaxReached());

        assertEquals(3, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));

        // count by criteria
        assertEquals(3, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));
        assertEquals(1, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "titititittiti")));

        // delete the last type of attempt only
        attempt.deleteAttempts();

        assertEquals(0, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));
        assertEquals(1, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "titititittiti")));

        attempt = getAttemptsManager().attempt("confirmOrder",
                                               AttemptCriteria.of("userId", "toto"));
        assertFalse(attempt.isMaxReached());

        assertEquals(1, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "toto")));
        assertEquals(1, getAttemptsCount("confirmOrder", AttemptCriteria.of("userId", "titititittiti")));

    }

}
