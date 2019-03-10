package org.spincast.testing.junitrunner.tests;

import static org.junit.Assert.fail;

import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.BeforeAfterClassMethodsProvider;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;
import org.spincast.testing.junitrunner.tests.utils.SpincastJUnitRunnerTester;
import org.spincast.testing.junitrunner.tests.utils.TestExpectedToFailProvider;

@RunWith(SpincastJUnitRunnerTester.class)
public class NoTestAndNoExpectingInstanciationExceptionTest implements
                                                            BeforeAfterClassMethodsProvider,
                                                            TestExpectedToFailProvider {

    @Override
    public String getTestExpectedToFail() {
        return SpincastJUnitRunner.SPINCAST_TEST_NAME_NO_TESTS_AND_NO_EXPECTION_EXCEPTION_ANNOTATION;
    }

    @Override
    public void beforeClass() {
        // ok
    }

    @Override
    public void afterClass() {
        fail("Test initiation was expected to fail.");
    }

    @Override
    public void beforeClassException(Throwable ex) {
        // ok
    }

}
