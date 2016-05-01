package org.spincast.quickstart.tests;

import org.spincast.core.config.ISpincastConfig;

import com.google.inject.Inject;

public class TestInject {

    private final ISpincastConfig spincastConfig;

    @Inject
    public TestInject(ISpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    public String test() {
        return this.spincastConfig.getEnvironmentName();
    }
}
