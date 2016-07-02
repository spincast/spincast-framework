package org.spincast.testing.utils.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.spincast.testing.utils.IBeforeAfterClassMethodsProvider;
import org.spincast.testing.utils.ITestFailureListener;
import org.spincast.testing.utils.SpincastJUnitRunner;

/**
 * We didn't find a way to test ITestFailureListener
 * properly. Since a test must fail to validate that feature, we always
 * end up with a failed result. So we ignore this test but it can be
 * run manually: it will fail, but we can validate that 
 * the 'testFailure(...)' method is called.
 */
@Ignore
@RunWith(SpincastJUnitRunner.class)
public class TestFailureListenerTest implements ITestFailureListener,
                                     IBeforeAfterClassMethodsProvider {

    private boolean testFailureCalled = false;

    @Test
    public void testThatFails() throws Exception {
        fail("test1 failed");
    }

    @Override
    public void testFailure(Failure failure) {
        this.testFailureCalled = true;
        assertNotNull(failure);
    }

    @Override
    public void beforeClass() {
        // nothing
    }

    @Override
    public void afterClass() {
        assertTrue(this.testFailureCalled);
    }
}
