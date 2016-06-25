package org.spincast.testing.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
