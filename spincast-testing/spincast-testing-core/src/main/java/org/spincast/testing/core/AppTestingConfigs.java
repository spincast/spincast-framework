package org.spincast.testing.core;

import org.spincast.core.config.SpincastConfig;

/**
 * Informations about the configurations to use
 * when testing the application.
 */
public interface AppTestingConfigs {

    /**
     * Should the App class itself (the clas in which
     * <code>Spincast.init()</code> or 
     * <code>Spincast.configure()</code> is called) be
     * bound?
     * <p>
     * Returning <code>true</code> is common if your
     * test class runs <em>integration</em> tests, since
     * it is probably the calling class that starts the
     * HTTP server. Return <code>false</code> if you
     * don't need any server to be started, but still
     * want the full application Guice context to be 
     * created (used in general for <em>unit</em> tests). 
     */
    public boolean isBindAppClass();

    /**
     * The implementation class to use for the <code>SpincastConfig</code>
     * binding. Returns <code>null</code> to leave it untouched.
     */
    public Class<? extends SpincastConfig> getSpincastConfigTestingImplementationClass();

    /**
     * Your application configuration interface. Returns 
     * <code>null</code> if you don't have one or
     * to leave it untouched.
     */
    public Class<?> getAppConfigInterface();

    /**
     * The implementation class to use for your application 
     * configuration binding. Returns 
     * <code>null</code> if you don't have one or
     * to leave it untouched.
     */
    public Class<?> getAppConfigTestingImplementationClass();
}
