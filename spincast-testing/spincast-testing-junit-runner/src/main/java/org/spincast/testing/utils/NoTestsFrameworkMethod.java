package org.spincast.testing.utils;

import java.lang.reflect.Method;

import org.junit.runners.model.FrameworkMethod;

/**
 * Pseudo-test to be able to run some Spincast tests
 * when no real @Test annotated test exist. JUnit required
 * at least one test to run.
 */
public class NoTestsFrameworkMethod extends FrameworkMethod {

    public NoTestsFrameworkMethod() {
        super(getMethodToUse());
    }

    protected static Method getMethodToUse() {

        try {
            return NoTestsFrameworkMethod.class.getMethod("getName");
        } catch(Exception ex) {
            throw(ex instanceof RuntimeException) ? (RuntimeException)ex : new RuntimeException(ex);
        }
    }

    @Override
    public String getName() {
        return "[Spincast] No runnable tests";
    }

}
