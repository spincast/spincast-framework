package org.spincast.testing.junitrunner.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.BeforeAfterClassMethodsProvider;
import org.spincast.testing.junitrunner.Repeat;
import org.spincast.testing.junitrunner.RepeatedClassAfterMethodProvider;
import org.spincast.testing.junitrunner.tests.utils.SpincastJUnitRunnerTester;

@RunWith(SpincastJUnitRunnerTester.class)
@Repeat(value = 3, sleep = 0)
public class RepeatTest implements BeforeAfterClassMethodsProvider, RepeatedClassAfterMethodProvider {

    private int test1Called = 0;
    private int test2Called = 0;

    private static int test1CalledTotal = 0;
    private static int test2CalledTotal = 0;

    private long test2CalledTime = 0;

    @Override
    public void beforeClass() {
        this.test1Called = 0;
        this.test2Called = 0;
    }

    @Override
    public void beforeClassException(Throwable ex) {
        // ok
    }

    @Test
    public void test1() throws Exception {
        this.test1Called++;
        test1CalledTotal++;
    }

    @Test
    @Repeat(value = 2, sleep = 500)
    public void test2() throws Exception {

        if (this.test2Called == 0) {
            this.test2CalledTime = System.currentTimeMillis();
        } else if (this.test2Called == 1) {
            long sleep = System.currentTimeMillis() - this.test2CalledTime;
            assertTrue(sleep + "", sleep >= 400);
        }

        this.test2Called++;
        test2CalledTotal++;
    }

    @Override
    public void afterClass() {
        assertEquals(1, this.test1Called);
        assertEquals(2, this.test2Called);
    }

    @Override
    public void afterClassLoops() {
        assertEquals(3, test1CalledTotal);
        assertEquals(6, test2CalledTotal);
    }

}
