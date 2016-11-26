package org.spincast.testing.utils.tests;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spincast.testing.utils.ExpectingBeforeClassException;
import org.spincast.testing.utils.BeforeAfterClassMethodsProvider;
import org.spincast.testing.utils.SpincastJUnitRunner;

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

    @Test
    public void test1() throws Exception {
        fail();
    }
}
