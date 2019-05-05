package org.spincast.testing.junitrunner.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.ExpectingFailure;
import org.spincast.testing.junitrunner.RepeatUntilFailure;
import org.spincast.testing.junitrunner.RepeatUntilSuccess;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;

@RunWith(SpincastJUnitRunner.class)
@ExpectingFailure
public class TwoRepeatAnnotationsAtTestLevelTest {

    @Test
    @RepeatUntilSuccess(value = 5, sleep = 50)
    @RepeatUntilFailure(value = 5, sleep = 50) // invalid!
    public void test1() throws Exception {
        // ok
    }
}
