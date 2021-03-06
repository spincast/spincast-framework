package org.spincast.plugins.externalconfig.tests;

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
import org.spincast.testing.junitrunner.ExpectingBeforeClassException;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

//==========================================
// We expect an Exception!
//==========================================
@ExpectingBeforeClassException
public class InvalidEnvVarsTest extends ConfigTestingBase {

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
        // defaults
    }

    @Inject
    private AppConfig appConfig;

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    public static interface AppConfig extends SpincastConfig {

        public String getTest();
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
            vars.put("app.test", "fromEnvVar");

            //==========================================
            // Invalid config
            //==========================================
            vars.put("app.test.nope", "fromEnvVarNope");

            return vars.entrySet();
        }

        @Override
        public String getTest() {
            return getString("app.test");
        }
    }

    @Test
    public void test() throws Exception {
        // ok
    }

}



