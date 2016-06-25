package org.spincast.testing.utils.tests;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.utils.ExpectingBeforeClassException;
import org.spincast.testing.utils.IBeforeAfterClassMethodsProvider;
import org.spincast.testing.utils.tests.utils.ITestingBeforeClassShouldHaveThrowAnException;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;

@RunWith(SpincastJUnitRunnerTester.class)
@ExpectingBeforeClassException
public class ExpectingBeforeClassExceptionNoneOccuredWithTestsTest implements IBeforeAfterClassMethodsProvider,
                                                                   ITestingBeforeClassShouldHaveThrowAnException {

    @Override
    public void beforeClass() {
        // no exception!
    }

    @Override
    public void afterClass() {
        fail("Test was expected to be ignored");
    }

    @Test
    public void test1() throws Exception {
        fail("Test was expected to be ignored");
    }
}
