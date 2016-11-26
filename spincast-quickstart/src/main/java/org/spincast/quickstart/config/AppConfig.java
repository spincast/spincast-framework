package org.spincast.quickstart.config;

import org.spincast.core.config.SpincastConfig;

/**
 * Application's custom configurations.
 * 
 * Extends <code>SpincastConfig</code> so we can use this class 
 * as the implementation for this interface too.
 */
public interface AppConfig extends SpincastConfig {

    /**
     * A custom configuration : return the
     * application name.
     */
    public String getAppName();

}
