package org.spincast.core.guice;

import java.lang.reflect.Method;

import com.google.inject.matcher.AbstractMatcher;

/**
 * Guice AOP Method Matcher that will matche on the
 * name of the method.
 */
public class GuiceAopMethodNameMatcher extends AbstractMatcher<Method> {

    private final String methodName;

    public GuiceAopMethodNameMatcher(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public boolean matches(Method method) {
        return method.getName().equals(this.methodName);
    }
}
