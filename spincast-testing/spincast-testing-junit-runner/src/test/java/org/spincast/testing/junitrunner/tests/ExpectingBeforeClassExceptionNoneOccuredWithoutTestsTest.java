package org.spincast.testing.junitrunner.tests;

import static org.junit.Assert.fail;

import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.BeforeAfterClassMethodsProvider;
import org.spincast.testing.junitrunner.ExpectingBeforeClassException;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;
import org.spincast.testing.junitrunner.tests.utils.SpincastJUnitRunnerTester;
import org.spincast.testing.junitrunner.tests.utils.TestExpectedToFailProvider;

@RunWith(SpincastJUnitRunnerTester.class)
@ExpectingBeforeClassException
public class ExpectingBeforeClassExceptionNoneOccuredWithoutTestsTest implements BeforeAfterClassMethodsProvider,
                                                                      TestExpectedToFailProvider {

    @Override
    public String getTestExpectedToFail() {
        return SpincastJUnitRunner.SPINCAST_TEST_NAME_BEFORE_CLASS_METHOD_VALIDATION;
    }

    @Override
    public void beforeClass() {
        // no exception!
    }

    @Override
    public void afterClass() {
        fail("Was expecting an instanciation exception!");
    }

    @Override
    public void beforeClassException(Throwable ex) {
        // ok
    }

}
