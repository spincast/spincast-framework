package org.spincast.defaults.testing.tests;

import org.spincast.core.config.SpincastConfig;
import org.spincast.defaults.testing.IntegrationTestAppDefaultContextsBase;
import org.spincast.testing.core.AppTestingConfigInfo;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;

public abstract class NoAppConfigTestClassBase extends IntegrationTestAppDefaultContextsBase {

    @Override
    protected AppTestingConfigInfo getAppTestingConfigInfo() {
        return new AppTestingConfigInfo() {

            @Override
            public Class<? extends SpincastConfig> getSpincastConfigTestingImplementationClass() {
                return SpincastConfigTestingDefault.class;
            }

            @Override
            public Class<?> getAppConfigTestingImplementationClass() {
                return null;
            }

            @Override
            public Class<?> getAppConfigInterface() {
                return null;
            }
        };
    }

}
