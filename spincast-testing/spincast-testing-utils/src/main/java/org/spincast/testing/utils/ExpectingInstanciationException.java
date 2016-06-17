package org.spincast.testing.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When this annoation is added on a Test Class, 
 * the class instanciation is expecting to fail.
 * 
 * To use with {@link org.spincast.testing.utils.SpincastJUnitRunner SpincastJUnitRunner}.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpectingInstanciationException {
}
