package org.spincast.testing.junitrunner.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.BeforeAfterClassMethodsProvider;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;

@RunWith(SpincastJUnitRunner.class)
public class BeforeAfterClassMethodsProviderTest implements BeforeAfterClassMethodsProvider {

    protected static int nbrInBeforeClass = 0;
    protected static int nbrInAfterClass = 0;
    protected static int nbrInTest1 = 0;
    protected static int nbrInTest2 = 0;

    @Override
    public void beforeClass() {
        nbrInBeforeClass++;
        assertEquals(1, nbrInBeforeClass);
        assertEquals(0, nbrInAfterClass);
        assertEquals(0, nbrInTest1);
        assertEquals(0, nbrInTest2);
    }

    @Override
    public void afterClass() {
        nbrInAfterClass++;
        assertEquals(1, nbrInBeforeClass);
        assertEquals(1, nbrInAfterClass);
        assertEquals(1, nbrInTest1);
        assertEquals(1, nbrInTest2);
    }

    @Test
    public void test1() throws Exception {
        nbrInTest1++;
        assertEquals(1, nbrInBeforeClass);
        assertEquals(0, nbrInAfterClass);
    }

    @Test
    public void test2() throws Exception {
        nbrInTest2++;
        assertEquals(1, nbrInBeforeClass);
        assertEquals(0, nbrInAfterClass);
    }

    @Override
    public void beforeClassException(Throwable ex) {
        // ok
    }

}
