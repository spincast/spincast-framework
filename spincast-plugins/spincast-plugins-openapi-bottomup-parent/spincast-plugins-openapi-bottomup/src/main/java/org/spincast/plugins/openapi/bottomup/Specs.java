package org.spincast.plugins.openapi.bottomup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import io.swagger.v3.oas.annotations.Operation;

/**
 * Annotation to wrap Swagger annotations used
 * to define specs on a route.
 */
@Target({ElementType.TYPE_USE, ElementType.TYPE, ElementType.TYPE_PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Specs {

    /**
     * The Operation Swagger annotation to associate
     * to the annotated route.
     */
    Operation value();

    /**
     * The media types the route consumes.
     */
    Consumes consumes() default @Consumes("");

    /**
     * The media types the route produces.
     */
    Produces produces() default @Produces("");
}
