package org.spincast.testing.utils.tests;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.utils.IBeforeAfterClassMethodsProvider;
import org.spincast.testing.utils.SpincastJUnitRunner;
import org.spincast.testing.utils.tests.utils.ITestExpectedToFailProvider;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;

@RunWith(SpincastJUnitRunnerTester.class)
public class AfterClassExceptionTest implements IBeforeAfterClassMethodsProvider, ITestExpectedToFailProvider {

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

}
