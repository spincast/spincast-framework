package org.spincast.testing.junitrunner;

import org.junit.runner.notification.Failure;

/**
 * Provides a method that will be called each time a test fails. This can help
 * debuging the problem by setting a breakpoint in that method.
 * 
 * To use with {@link org.spincast.testing.junitrunner.SpincastJUnitRunner SpincastJUnitRunner}.
 */
public interface TestFailureListener {

    public void testFailure(Failure failure);
}
