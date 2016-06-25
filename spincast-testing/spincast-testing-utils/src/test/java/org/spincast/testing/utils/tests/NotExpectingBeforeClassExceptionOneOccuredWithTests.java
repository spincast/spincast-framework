package org.spincast.testing.utils.tests;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.utils.IBeforeAfterClassMethodsProvider;
import org.spincast.testing.utils.tests.utils.ITestingBeforeClassShouldNotHaveThrowAnException;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;

@RunWith(SpincastJUnitRunnerTester.class)
public class NotExpectingBeforeClassExceptionOneOccuredWithTests implements
                                                                 IBeforeAfterClassMethodsProvider,
                                                                 ITestingBeforeClassShouldNotHaveThrowAnException {

    @Override
    public void beforeClass() {
        fail("init error!");
    }

    @Override
    public void afterClass() {
        fail("Was expecting an instanciation exception!");
    }

    @Test
    public void test1() throws Exception {
        fail("Was expecting an instanciation exception!");
    }

}
