package org.spincast.quickstart.config;

import org.spincast.plugins.config.SpincastConfigDefault;

/**
 * Implementation for the application's custom configurations.
 */
public class AppConfigDefault extends SpincastConfigDefault implements AppConfig {

    /**
     * Implementation of a custom configuration.
     */
    @Override
    public String getAppName() {
        return "Spincast Quick Start";
    }

    /**
     * It is recommended to always override
     * this "getPublicServerSchemeHostPort" configuration. 
     * Spincast can't know by itself what *public*
     * scheme/host/port are used to access 
     * the application and sometimes it needs those
     * informations...
     */
    @Override
    public String getPublicServerSchemeHostPort() {
        return "http://localhost:44419";
    }

    /**
     * This is another example of overriding a default
     * configuration. This one changes the name
     * of the cookie used by Spincast to indicate
     * that cookies have been validated and are enabled
     * for a specific user.
     * <p>
     * The default name is "spincast_cookies_enabled".
     */
    @Override
    public String getCookiesValidatorCookieName() {
        return "ck_enabled";
    }
}
