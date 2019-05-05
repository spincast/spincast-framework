package org.spincast.testing.junitrunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify that a test or a test class
 * will be ran until it fails or the max number of tries is
 * reached.
 * <p>
 * You should not use this except when you try to debug
 * a test that sometimes fails... This allows you to run
 * it multiple times to trigger the error.
 * <p>
 * Also, this may be obvious, but notice that
 * the code annotated with this will be
 * ran more than once! Make sure this code does support it
 * and doesn't have unwanted side effects.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatUntilFailure {

    /**
     * The maximum number of loops.
     */
    int value();

    /**
     * Sleep time in milliseconds between two loops.
     * <p>
     * Defaults: no sleep.
     */
    int sleep() default 0;
}
