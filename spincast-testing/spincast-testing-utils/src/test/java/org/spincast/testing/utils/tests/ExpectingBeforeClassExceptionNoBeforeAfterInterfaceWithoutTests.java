package org.spincast.testing.utils.tests;

import org.junit.runner.RunWith;
import org.spincast.testing.utils.ExpectingBeforeClassException;
import org.spincast.testing.utils.tests.utils.ITestingBeforeClassShouldHaveThrowAnException;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;

@RunWith(SpincastJUnitRunnerTester.class)
@ExpectingBeforeClassException
public class ExpectingBeforeClassExceptionNoBeforeAfterInterfaceWithoutTests implements ITestingBeforeClassShouldHaveThrowAnException {
    // nothing
}
