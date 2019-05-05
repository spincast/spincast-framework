package org.spincast.testing.junitrunner.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.BeforeAfterClassMethodsProvider;
import org.spincast.testing.junitrunner.IgnoreErrorButStopLoops;
import org.spincast.testing.junitrunner.RepeatUntilFailure;
import org.spincast.testing.junitrunner.RepeatedClassAfterMethodProvider;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;

@RunWith(SpincastJUnitRunner.class)
@RepeatUntilFailure(value = 5, sleep = 50)
public class RepeatUntilFailureStopWhenErrorTest implements BeforeAfterClassMethodsProvider, RepeatedClassAfterMethodProvider {

    private static int testClassCalledTotal = 0;

    @Override
    public void beforeClass() {
        testClassCalledTotal++;
    }

    @Test
    @IgnoreErrorButStopLoops // custom error handling!
    public void test1() throws Exception {

        //==========================================
        // Throw an exception at loop #2
        //==========================================
        if (testClassCalledTotal == 2) {
            fail("test error");
        }
    }

    @Override
    public void afterClassLoops() {
        //==========================================
        // Will only have executed 2 times
        //==========================================
        assertEquals(2, testClassCalledTotal);
    }

    @Override
    public void beforeClassException(Throwable ex) {
        // ok
    }

    @Override
    public void afterClass() {
        // ok
    }

}
