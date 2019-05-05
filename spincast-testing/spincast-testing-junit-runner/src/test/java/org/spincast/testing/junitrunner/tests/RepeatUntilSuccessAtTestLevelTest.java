package org.spincast.testing.junitrunner.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.BeforeAfterClassMethodsProvider;
import org.spincast.testing.junitrunner.RepeatUntilSuccess;
import org.spincast.testing.junitrunner.RepeatedClassAfterMethodProvider;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;

@RunWith(SpincastJUnitRunner.class)
public class RepeatUntilSuccessAtTestLevelTest implements BeforeAfterClassMethodsProvider, RepeatedClassAfterMethodProvider {

    private static int testClassCalledTotal = 0;
    private static int testCalledTotal = 0;

    @Override
    public void beforeClass() {
        testClassCalledTotal++;
    }

    @Test
    @RepeatUntilSuccess(value = 5, sleep = 50)
    public void test1() throws Exception {
        testCalledTotal++;
        if (testCalledTotal < 3) {
            throw new RuntimeException("test error");
        }
    }

    @Override
    public void afterClassLoops() {
        assertEquals(1, testClassCalledTotal);
        assertEquals(3, testCalledTotal);
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
