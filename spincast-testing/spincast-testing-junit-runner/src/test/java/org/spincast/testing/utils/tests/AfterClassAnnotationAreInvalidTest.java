package org.spincast.testing.utils.tests;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.utils.SpincastJUnitRunner;
import org.spincast.testing.utils.tests.utils.ITestExpectedToFailProvider;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;

@RunWith(SpincastJUnitRunnerTester.class)
public class AfterClassAnnotationAreInvalidTest implements ITestExpectedToFailProvider {

    @Override
    public String getTestExpectedToFail() {
        return SpincastJUnitRunner.SPINCAST_TEST_NAME_AFTER_CLASS_ANNOTATIONS_VALIDATION;
    }

    @AfterClass
    public static void afterClassAnnotation() {
        // ok
    }

    @Test
    public void test1() throws Exception {
        // ok
    }

}
