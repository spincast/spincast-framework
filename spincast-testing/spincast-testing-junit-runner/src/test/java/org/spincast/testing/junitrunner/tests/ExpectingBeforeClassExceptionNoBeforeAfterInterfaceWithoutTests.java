package org.spincast.testing.junitrunner.tests;

import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.ExpectingBeforeClassException;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;
import org.spincast.testing.junitrunner.tests.utils.SpincastJUnitRunnerTester;
import org.spincast.testing.junitrunner.tests.utils.TestExpectedToFailProvider;

@RunWith(SpincastJUnitRunnerTester.class)
@ExpectingBeforeClassException
public class ExpectingBeforeClassExceptionNoBeforeAfterInterfaceWithoutTests implements TestExpectedToFailProvider {

    @Override
    public String getTestExpectedToFail() {
        return SpincastJUnitRunner.SPINCAST_TEST_NAME_BEFORE_CLASS_METHOD_VALIDATION;
    }

}
