package org.spincast.testing.utils.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.utils.SpincastJUnitRunner;
import org.spincast.testing.utils.tests.utils.TestExpectedToFailProvider;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;

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
