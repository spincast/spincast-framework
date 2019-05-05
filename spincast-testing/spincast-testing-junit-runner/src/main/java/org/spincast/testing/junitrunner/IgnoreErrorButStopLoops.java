package org.spincast.testing.junitrunner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is an annotation only useful in some very
 * corner cases tests. You will probably never
 * need it.
 * <p>
 * It allows a test method to fail without triggering
 * an error (exception expected), but still stopping any
 * loops configured by {@link RepeatUntilSuccess} or {@link RepeatUntilFailure}.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreErrorButStopLoops {
    // nothing required
}
