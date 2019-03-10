package org.spincast.testing.junitrunner.tests;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.BeforeAfterClassMethodsProvider;
import org.spincast.testing.junitrunner.ExpectingBeforeClassException;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;

@RunWith(SpincastJUnitRunner.class)
@ExpectingBeforeClassException
public class ExpectingBeforeClassExceptionWithTestsTest implements BeforeAfterClassMethodsProvider {

    @Override
    public void beforeClass() {
        fail("init error");
    }

    @Override
    public void afterClass() {
        fail();
    }

    @Override
    public void beforeClassException(Throwable ex) {
        // ok
    }

    @Test
    public void test1() throws Exception {
        fail();
    }
}
