package org.spincast.testing.utils.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.utils.tests.utils.ITestingBeforeClassAnnotationAreInvalid;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;

@RunWith(SpincastJUnitRunnerTester.class)
public class BeforeClassAnnotationAreInvalidTest implements ITestingBeforeClassAnnotationAreInvalid {

    @BeforeClass
    public static void beforeClassAnnotation() {
        // ok
    }

    @Test
    public void test1() throws Exception {
        // ok
    }
}
