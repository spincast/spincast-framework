package org.spincast.plugins.externalconfig.tests;

import java.util.HashMap;
import java.util.List;
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
import org.spincast.testing.utils.ExpectingBeforeClassException;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

@ExpectingBeforeClassException
public class PrefixStrippingEnvVarDuplicateTest extends ConfigTestingBase {

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

        @Override
        public List<String> getEnvironmentVariablesPrefixes() {
            return Lists.newArrayList("app.", "common.");
        }

        @Override
        public boolean isEnvironmentVariablesStripPrefix() {
            return true;
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

        /**
         * Mocks environment variables
         */
        @Override
        protected Set<Entry<String, String>> getEnvironmentVariables() {

            //==========================================
            // Duplicated "test" key when the prefixes
            // are stripped!
            //==========================================
            Map<String, String> vars = new HashMap<>();
            vars.put("app.test", "fromEnvVar1");
            vars.put("common.test", "fromEnvVar111");
            return vars.entrySet();
        }

        @Override
        public String getTest1() {
            return getString("test");
        }
    }

    @Test
    public void test() throws Exception {
        // dummy
    }

}



