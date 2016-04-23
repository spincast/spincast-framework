package org.spincast.core.guice;

/**
 * Custom Guice scoped defined by Spincast.
 */
public interface SpincastGuiceScopes {

    /**
     * Instance of the Guice scope for a <code>request</code>.
     */
    public static final SpincastRequestScope REQUEST = new SpincastRequestScope();

}
