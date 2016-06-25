package org.spincast.testing.utils;

import org.junit.runner.notification.Failure;

/**
 * Provides a method that will be called each time a test fails. This can help
 * debuging the problem by setting a breakpoint in that method.
 * 
 * To use with {@link org.spincast.testing.utils.SpincastJUnitRunner SpincastJUnitRunner}.
 */
public interface ITestFailureListener {

    public void testFailure(Failure failure);
}
