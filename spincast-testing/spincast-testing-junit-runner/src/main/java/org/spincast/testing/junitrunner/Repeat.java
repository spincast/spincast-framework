package org.spincast.testing.junitrunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify that a test or a test class
 * has to be run multiple time.
 * <p>
 * You should not use this except when you try to debug
 * a test that sometimes fails... This allows you to run
 * it multiple times to trigger the error.
 * </p>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Repeat {

    /**
     * The number of time to repeat the test.
     */
    int value() default 2;

    /**
     * Sleep time in milliseconds between two
     * loops.
     * 
     * By defaults, no sleep.
     */
    int sleep() default 0;
}
