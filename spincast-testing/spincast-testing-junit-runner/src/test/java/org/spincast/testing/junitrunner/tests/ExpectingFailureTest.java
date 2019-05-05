package org.spincast.testing.junitrunner.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.ExpectingFailure;
import org.spincast.testing.junitrunner.tests.utils.SpincastJUnitRunnerTester;

@RunWith(SpincastJUnitRunnerTester.class)
@ExpectingFailure
public class ExpectingFailureTest {

    @Test
    public void test1() throws Exception {
        throw new RuntimeException("test error");
    }
}
