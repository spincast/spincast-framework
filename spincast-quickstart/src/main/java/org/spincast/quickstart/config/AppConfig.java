package org.spincast.quickstart.config;

import org.spincast.core.config.SpincastConfig;

/**
 * Application's custom configurations.
 * 
 * Extends <code>SpincastConfig</code> so both the
 * default and the custom configurations will be available through
 * the <code>AppConfig</code> interface.
 */
public interface AppConfig extends SpincastConfig {

    /**
     * A custom configuration : returns the
     * application name.
     */
    public String getAppName();

}
