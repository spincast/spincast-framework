package org.spincast.testing.core;

import org.spincast.core.config.SpincastConfig;

/**
 * Informations about the testing configurations to use.
 */
public interface AppTestingConfigInfo {

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
