package org.spincast.testing.junitrunner.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.BeforeAfterClassMethodsProvider;
import org.spincast.testing.junitrunner.ExpectingFailure;
import org.spincast.testing.junitrunner.RepeatUntilSuccess;
import org.spincast.testing.junitrunner.RepeatedClassAfterMethodProvider;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;

@RunWith(SpincastJUnitRunner.class)
@RepeatUntilSuccess(value = 5, sleep = 50)
//==========================================
// We expect a failure because there will be
// no success after the 5 loops so SpincastJUnitRunner
// will add an error!
//==========================================
@ExpectingFailure
public class RepeatUntilSuccessAtClassLevelNoSuccessTest implements BeforeAfterClassMethodsProvider,
                                                         RepeatedClassAfterMethodProvider {

    private static int testClassCalledTotal = 0;

    @Override
    public void beforeClass() {
        testClassCalledTotal++;
    }

    @Test
    public void test1() throws Exception {
        throw new RuntimeException("test error");
    }

    @Override
    public void afterClassLoops() {
        //==========================================
        // Will only have executed 3 times
        //==========================================
        assertEquals(5, testClassCalledTotal);
    }

    @Override
    public void beforeClassException(Throwable ex) {
        // ok
    }

    @Override
    public void afterClass() {
        // ok
        System.out.println();
    }

}
