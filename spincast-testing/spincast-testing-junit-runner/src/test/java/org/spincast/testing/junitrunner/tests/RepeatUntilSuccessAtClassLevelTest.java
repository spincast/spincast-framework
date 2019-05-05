package org.spincast.testing.junitrunner.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.BeforeAfterClassMethodsProvider;
import org.spincast.testing.junitrunner.RepeatUntilSuccess;
import org.spincast.testing.junitrunner.RepeatedClassAfterMethodProvider;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;

@RunWith(SpincastJUnitRunner.class)
@RepeatUntilSuccess(value = 5, sleep = 50)
public class RepeatUntilSuccessAtClassLevelTest implements BeforeAfterClassMethodsProvider, RepeatedClassAfterMethodProvider {

    private static int testClassCalledTotal = 0;

    @Override
    public void beforeClass() {
        testClassCalledTotal++;
    }

    @Test
    public void test1() throws Exception {
        if (testClassCalledTotal < 3) {
            throw new RuntimeException("test error");
        }
    }

    @Override
    public void afterClassLoops() {
        //==========================================
        // Will only have executed 3 times
        //==========================================
        assertEquals(3, testClassCalledTotal);
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
