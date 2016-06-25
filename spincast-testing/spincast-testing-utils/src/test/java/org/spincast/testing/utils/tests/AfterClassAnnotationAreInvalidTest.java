package org.spincast.testing.utils.tests;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.utils.tests.utils.ITestingAfterClassAnnotationAreInvalid;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;

@RunWith(SpincastJUnitRunnerTester.class)
public class AfterClassAnnotationAreInvalidTest implements ITestingAfterClassAnnotationAreInvalid {

    @AfterClass
    public static void afterClassAnnotation() {
        // ok
    }

    @Test
    public void test1() throws Exception {
        // ok
    }
}
