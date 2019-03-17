package org.spincast.quickstart.config;

import org.spincast.core.guice.TestingMode;
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.plugins.config.SpincastConfigPluginConfig;

import com.google.inject.Inject;

/**
 * Implementation for the application's custom configurations.
 */
public class AppConfigDefault extends SpincastConfigDefault implements AppConfig {

    private String thisRootPackageName = null;

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
    public String getAppRootPackage() {
        if (this.thisRootPackageName == null) {

            //==========================================
            // By default, we use the first two package
            // parts of this very class as the root package
            // of the app.
            //==========================================
            String rootPackageName = AppConfigDefault.class.getPackage().getName();
            int dotPos = rootPackageName.indexOf(".");
            if (dotPos > 0) {
                int dotPos2 = rootPackageName.indexOf(".", dotPos + 1);
                if (dotPos2 > 0) {
                    rootPackageName = rootPackageName.substring(0, dotPos2);
                }
            }
            this.thisRootPackageName = rootPackageName;
        }
        return this.thisRootPackageName;
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
