package org.spincast.testing.defaults.tests;

import org.spincast.core.config.SpincastConfig;
import org.spincast.testing.core.AppTestingConfigs;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.testing.defaults.AppBasedDefaultContextTypesTestingBase;

public abstract class IntegrationConfigTestClassBase extends AppBasedDefaultContextTypesTestingBase {


    @Override
    protected AppTestingConfigs getAppTestingConfigs() {

        return new AppTestingConfigs() {

            @Override
            public boolean isBindAppClass() {
                return true;
            }

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
