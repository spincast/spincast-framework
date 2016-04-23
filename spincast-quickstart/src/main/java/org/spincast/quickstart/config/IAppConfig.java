package org.spincast.quickstart.config;

import org.spincast.core.config.ISpincastConfig;

/**
 * Application's custom configurations.
 * 
 * <p>
 * Extends <code>ISpincastConfig</code> so we can use this class 
 * as the implementation for this interface too.
 * </p>
 */
public interface IAppConfig extends ISpincastConfig {

    /**
     * A custom configuration : return the
     * application name.
     */
    public String getAppName();

}
