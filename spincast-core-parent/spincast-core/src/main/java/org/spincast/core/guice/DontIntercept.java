package org.spincast.core.guice;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * When you use {@link GuiceModuleUtils#createInterceptorModule(Class, Class, boolean)},
 * you can annotate some methods with this to prevent them from being
 * intercepted.
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface DontIntercept {
}
