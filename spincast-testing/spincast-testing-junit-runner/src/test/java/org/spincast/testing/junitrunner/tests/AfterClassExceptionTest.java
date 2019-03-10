package org.spincast.testing.junitrunner.tests;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.BeforeAfterClassMethodsProvider;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;
import org.spincast.testing.junitrunner.tests.utils.SpincastJUnitRunnerTester;
import org.spincast.testing.junitrunner.tests.utils.TestExpectedToFailProvider;

@RunWith(SpincastJUnitRunnerTester.class)
public class AfterClassExceptionTest implements BeforeAfterClassMethodsProvider, TestExpectedToFailProvider {

    @Override
    public String getTestExpectedToFail() {
        return SpincastJUnitRunner.SPINCAST_TEST_NAME_AFTER_CLASS_METHOD_VALIDATION;
    }

    @Override
    public void beforeClass() {
        // ok
    }

    @Test
    public void test1() {
        // ok
    }

    @Override
    public void afterClass() {
        fail();
    }

    @Override
    public void beforeClassException(Throwable ex) {
        // ok
    }

}
