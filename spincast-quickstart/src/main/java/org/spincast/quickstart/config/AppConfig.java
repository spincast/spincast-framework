package org.spincast.quickstart.config;

import org.spincast.plugins.config.SpincastConfig;

/**
 * Implementation of the application's custom configurations class.
 */
public class AppConfig extends SpincastConfig implements IAppConfig {

    @Override
    public String getAppName() {
        return "Spincast Quick Start";
    }
}
