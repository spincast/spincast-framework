package org.spincast.testing.junitrunner.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;
import org.spincast.testing.junitrunner.tests.utils.SpincastJUnitRunnerTester;
import org.spincast.testing.junitrunner.tests.utils.TestExpectedToFailProvider;

@RunWith(SpincastJUnitRunnerTester.class)
public class BeforeClassAnnotationAreInvalidTest implements TestExpectedToFailProvider {

    @Override
    public String getTestExpectedToFail() {
        return SpincastJUnitRunner.SPINCAST_TEST_NAME_BEFORE_CLASS_ANNOTATIONS_VALIDATION;
    }

    @BeforeClass
    public static void beforeClassAnnotation() {
        // ok
    }

    @Test
    public void test1() throws Exception {
        // ok
    }

}
