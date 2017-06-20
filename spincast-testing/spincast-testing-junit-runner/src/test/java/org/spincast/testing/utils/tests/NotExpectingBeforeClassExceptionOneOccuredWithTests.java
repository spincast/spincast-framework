package org.spincast.testing.utils.tests;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.utils.BeforeAfterClassMethodsProvider;
import org.spincast.testing.utils.SpincastJUnitRunner;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;
import org.spincast.testing.utils.tests.utils.TestExpectedToFailProvider;

@RunWith(SpincastJUnitRunnerTester.class)
public class NotExpectingBeforeClassExceptionOneOccuredWithTests implements
                                                                 BeforeAfterClassMethodsProvider,
                                                                 TestExpectedToFailProvider {

    @Override
    public String getTestExpectedToFail() {
        return SpincastJUnitRunner.SPINCAST_TEST_NAME_BEFORE_CLASS_METHOD_VALIDATION;
    }

    @Override
    public void beforeClass() {
        fail("init error!");
    }

    @Override
    public void afterClass() {
        fail("Was expecting an instanciation exception!");
    }

    @Override
    public void beforeClassException(Throwable ex) {
        // ok
    }

    @Test
    public void test1() throws Exception {
        fail("Was expecting an instanciation exception!");
    }

}
