package org.spincast.testing.junitrunner.tests;

import static org.junit.Assert.fail;

import org.junit.runner.RunWith;
import org.spincast.testing.junitrunner.BeforeAfterClassMethodsProvider;
import org.spincast.testing.junitrunner.ExpectingBeforeClassException;
import org.spincast.testing.junitrunner.SpincastJUnitRunner;

@RunWith(SpincastJUnitRunner.class)
@ExpectingBeforeClassException
public class ExpectingBeforeClassExceptionWithoutTestsTest implements BeforeAfterClassMethodsProvider {

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
}
