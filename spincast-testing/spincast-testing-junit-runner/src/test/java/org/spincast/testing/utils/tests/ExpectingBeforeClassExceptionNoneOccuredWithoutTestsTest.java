package org.spincast.testing.utils.tests;

import static org.junit.Assert.fail;

import org.junit.runner.RunWith;
import org.spincast.testing.utils.ExpectingBeforeClassException;
import org.spincast.testing.utils.IBeforeAfterClassMethodsProvider;
import org.spincast.testing.utils.SpincastJUnitRunner;
import org.spincast.testing.utils.tests.utils.ITestExpectedToFailProvider;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;

@RunWith(SpincastJUnitRunnerTester.class)
@ExpectingBeforeClassException
public class ExpectingBeforeClassExceptionNoneOccuredWithoutTestsTest implements IBeforeAfterClassMethodsProvider,
                                                                      ITestExpectedToFailProvider {

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

}
