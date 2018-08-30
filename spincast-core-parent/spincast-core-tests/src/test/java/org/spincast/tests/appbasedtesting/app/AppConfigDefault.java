package org.spincast.tests.appbasedtesting.app;

import org.spincast.core.guice.TestingMode;
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.plugins.config.SpincastConfigPluginConfig;

import com.google.inject.Inject;

public class AppConfigDefault extends SpincastConfigDefault implements AppConfigs {

    @Inject
    protected AppConfigDefault(SpincastConfigPluginConfig spincastConfigPluginConfig,
                               @TestingMode boolean testingMode) {
        super(spincastConfigPluginConfig, testingMode);
    }

    @Override
    public String getCustomConfig() {
        return "production";
    }

}
