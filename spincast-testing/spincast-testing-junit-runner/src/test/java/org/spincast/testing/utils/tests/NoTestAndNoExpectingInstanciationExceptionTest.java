package org.spincast.testing.utils.tests;

import static org.junit.Assert.fail;

import org.junit.runner.RunWith;
import org.spincast.testing.utils.BeforeAfterClassMethodsProvider;
import org.spincast.testing.utils.SpincastJUnitRunner;
import org.spincast.testing.utils.tests.utils.TestExpectedToFailProvider;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;

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

}
