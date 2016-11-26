package org.spincast.testing.utils.tests;

import org.junit.runner.RunWith;
import org.spincast.testing.utils.ExpectingBeforeClassException;
import org.spincast.testing.utils.SpincastJUnitRunner;
import org.spincast.testing.utils.tests.utils.TestExpectedToFailProvider;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;

@RunWith(SpincastJUnitRunnerTester.class)
@ExpectingBeforeClassException
public class ExpectingBeforeClassExceptionNoBeforeAfterInterfaceWithoutTests implements TestExpectedToFailProvider {

    @Override
    public String getTestExpectedToFail() {
        return SpincastJUnitRunner.SPINCAST_TEST_NAME_BEFORE_CLASS_METHOD_VALIDATION;
    }

}
