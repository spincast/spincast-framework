package org.spincast.quickstart.config;

import org.spincast.core.guice.TestingMode;
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.plugins.config.SpincastConfigPluginConfig;

import com.google.inject.Inject;

/**
 * Implementation for the application's custom configurations.
 */
public class AppConfigDefault extends SpincastConfigDefault implements AppConfig {

    /**
     * Constructor
     */
    @Inject
    public AppConfigDefault(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
        super(spincastConfigPluginConfig, testingMode);
    }

    @Override
    public String getAppName() {
        return getString("app.name");
    }

    @Override
    public String getPublicUrlBase() {
        return getString("spincast.publicAccess.urlBase");
    }

    @Override
    public String getCookiesValidatorCookieName() {
        return getString("spincast.cookies.validator.name", super.getCookiesValidatorCookieName());
    }

    @Override
    public String getCookieNameFlashMessage() {
        return getString("spincast.cookies.flashMessageCookieName", super.getCookieNameFlashMessage());
    }

    @Override
    public String getCookieNameLocale() {
        return getString("spincast.cookies.localeCookieName", super.getCookieNameLocale());
    }


}
