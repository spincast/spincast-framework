package org.spincast.tests.appbasedtesting;

import org.spincast.core.guice.TestingMode;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.tests.appbasedtesting.app.AppConfigDefault;

import com.google.inject.Inject;

/**
 * We extends the app's configuration to change or
 * adds things during testing.
 */
public class TestingAppConfig extends AppConfigDefault {

    @Inject
    protected TestingAppConfig(SpincastConfigPluginConfig spincastConfigPluginConfig,
                               @TestingMode boolean testingMode) {
        super(spincastConfigPluginConfig, testingMode);
    }

    @Override
    public String getCustomConfig() {
        return "testing";
    }

}
