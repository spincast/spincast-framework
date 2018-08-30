package org.spincast.plugins.externalconfig.tests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

public class ExternalFileOverridesEnvVarsConfigTest extends ConfigTestingBase {

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
        // External file has priority!
        //==========================================
        @Override
        public boolean isExternalFileConfigsOverrideEnvironmentVariables() {
            return true;
        }
    }

    @Inject
    private AppConfig appConfig;

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    public static interface AppConfig extends SpincastConfig {

        public String getTest10();
    }

    public static class AppConfigDefault extends SpincastConfigDefault implements AppConfig {

        @Inject
        public AppConfigDefault(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        /**
         * Mock environment variables
         */
        @Override
        protected Set<Entry<String, String>> getEnvironmentVariables() {

            Map<String, String> vars = new HashMap<>();
            vars.put("app.test10", "fromEnvVar1");
            return vars.entrySet();
        }

        @Override
        public String getTest10() {
            return getString("app.test10");
        }
    }

    @Test
    public void externalFileOverridesEnvVar() throws Exception {
        assertEquals("fromExternalConfigFile10", getAppConfig().getTest10());
    }



}



