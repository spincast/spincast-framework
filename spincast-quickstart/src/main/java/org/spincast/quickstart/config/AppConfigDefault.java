package org.spincast.quickstart.config;

import org.spincast.plugins.config.SpincastConfigDefault;

/**
 * Implementation of the application's custom configurations class.
 */
public class AppConfigDefault extends SpincastConfigDefault implements AppConfig {

    @Override
    public String getAppName() {
        return "Spincast Quick Start";
    }

    @Override
    public String getPublicServerSchemeHostPort() {
        return "http://localhost:44419";
    }
}
