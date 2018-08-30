package org.spincast.plugins.externalconfig.tests;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.TestingMode;
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.config.SpincastConfigPluginConfigDefault;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class NoExceptionIfExternalConfigFileNotFoundTest extends ConfigTestingBase {

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass2() {
        return AppConfigDefault.class;
    }

    @Override
    protected Module getExtraOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastConfigPluginConfig.class).to(AppSpincastConfigPluginConfig.class)
                                                      .in(Scopes.SINGLETON);
                bind(AppConfig.class).to(AppConfigDefault.class).in(Scopes.SINGLETON);
            }
        };
    }

    protected static class AppSpincastConfigPluginConfig extends SpincastConfigPluginConfigDefault {

        //==========================================
        // Not existing external config file name
        //==========================================
        @Override
        public String getExternalFilePath() {
            return "nope.yaml";
        };

        //==========================================
        // No exception if not found!
        //==========================================
        @Override
        public boolean isThrowExceptionIfSpecifiedExternalConfigFileIsNotFound() {
            return false;
        }
    }

    @Inject
    private AppConfig appConfig;

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    public static interface AppConfig extends SpincastConfig {

        public String getTest1();
    }

    public static class AppConfigDefault extends SpincastConfigDefault implements AppConfig {

        @Inject
        public AppConfigDefault(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public String getTest1() {
            return getString("app.test1");
        }
    }

    @Test
    public void emptyTest() throws Exception {
        // ok!
    }

}



