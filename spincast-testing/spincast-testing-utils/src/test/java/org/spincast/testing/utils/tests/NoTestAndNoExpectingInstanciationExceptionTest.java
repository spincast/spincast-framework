package org.spincast.testing.utils.tests;

import static org.junit.Assert.fail;

import org.junit.runner.RunWith;
import org.spincast.testing.utils.IBeforeAfterClassMethodsProvider;
import org.spincast.testing.utils.tests.utils.ITestingNoTestAndNoExpectingBeforeClassException;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;

@RunWith(SpincastJUnitRunnerTester.class)
public class NoTestAndNoExpectingInstanciationExceptionTest implements
                                                            IBeforeAfterClassMethodsProvider,
                                                            ITestingNoTestAndNoExpectingBeforeClassException {

    @Override
    public void beforeClass() {
        // ok
    }

    @Override
    public void afterClass() {
        fail("Test initiation was expected to fail.");
    }

}
