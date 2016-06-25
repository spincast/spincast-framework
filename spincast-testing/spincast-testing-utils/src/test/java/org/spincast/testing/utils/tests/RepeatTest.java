package org.spincast.testing.utils.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.utils.IBeforeAfterClassMethodsProvider;
import org.spincast.testing.utils.IRepeatedClassAfterMethodProvider;
import org.spincast.testing.utils.Repeat;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;

@RunWith(SpincastJUnitRunnerTester.class)
@Repeat(value = 3, sleep = 0)
public class RepeatTest implements IBeforeAfterClassMethodsProvider, IRepeatedClassAfterMethodProvider {

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

    @Test
    public void test1() throws Exception {
        this.test1Called++;
        test1CalledTotal++;
    }

    @Test
    @Repeat(value = 2, sleep = 500)
    public void test2() throws Exception {

        if(this.test2Called == 0) {
            this.test2CalledTime = System.currentTimeMillis();
        } else if(this.test2Called == 1) {
            long sleep = System.currentTimeMillis() - this.test2CalledTime;
            assertTrue(sleep >= 500);
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
