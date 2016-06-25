package org.spincast.testing.utils.tests;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.utils.ExpectingBeforeClassException;
import org.spincast.testing.utils.tests.utils.ITestingBeforeClassShouldHaveThrowAnException;
import org.spincast.testing.utils.tests.utils.SpincastJUnitRunnerTester;

@RunWith(SpincastJUnitRunnerTester.class)
@ExpectingBeforeClassException
public class ExpectingBeforeClassExceptionNoBeforeAfterInterfaceWithTests implements
                                                                          ITestingBeforeClassShouldHaveThrowAnException {

    @Test
    public void test1() throws Exception {
        fail("Was expecting a beforeClass() exception!");
    }

}
