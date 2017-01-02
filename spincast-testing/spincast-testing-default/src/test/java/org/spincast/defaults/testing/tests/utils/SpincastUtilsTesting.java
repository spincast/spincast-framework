package org.spincast.defaults.testing.tests.utils;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.utils.SpincastUtilsDefault;

import com.google.inject.Inject;

public class SpincastUtilsTesting extends SpincastUtilsDefault {

    @Inject
    public SpincastUtilsTesting(SpincastConfig spincastConfig) {
        super(spincastConfig);
    }

    @Override
    public String getCacheBusterCode() {
        return "42";
    }
}
