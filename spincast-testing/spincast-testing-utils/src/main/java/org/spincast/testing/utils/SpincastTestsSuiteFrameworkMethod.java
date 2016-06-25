package org.spincast.testing.utils;

import java.lang.reflect.Method;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

public class SpincastTestsSuiteFrameworkMethod extends FrameworkMethod {

    public SpincastTestsSuiteFrameworkMethod(TestClass testClass) {
        super(getMethodToUse(testClass));
    }

    protected static Method getMethodToUse(TestClass testClass) {
        try {
            return testClass.getJavaClass().getMethod("hashCode");
        } catch(Exception ex) {
            throw(ex instanceof RuntimeException) ? (RuntimeException)ex : new RuntimeException(ex);
        }
    }

    @Override
    public String getName() {
        return "[Spincast Tests]";
    }

}
