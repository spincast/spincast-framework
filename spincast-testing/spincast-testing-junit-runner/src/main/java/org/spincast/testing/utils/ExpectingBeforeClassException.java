package org.spincast.testing.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When this annotation is added on a Test Class, 
 * the 'beforeClass()' method is expected to fail.
 * 
 * To use with {@link org.spincast.testing.utils.SpincastJUnitRunner SpincastJUnitRunner}
 * and {@link org.spincast.testing.utils.BeforeAfterClassMethodsProvider BeforeAfterClassMethodsProvider}
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpectingBeforeClassException {
}
