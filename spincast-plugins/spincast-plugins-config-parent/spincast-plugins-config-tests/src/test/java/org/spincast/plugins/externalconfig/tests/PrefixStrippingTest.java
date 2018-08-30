package org.spincast.plugins.externalconfig.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class PrefixStrippingTest extends ConfigTestingBase {

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

        @Override
        public List<String> getSystemPropertiesPrefixes() {
            return Lists.newArrayList("app.", "common.");
        }

        @Override
        public boolean isSystemPropertiesStripPrefix() {
            return true;
        }
    }

    /**
     * Extra System properties
     */
    @Override
    protected Map<String, String> getExtraSystemProperties() {

        Map<String, String> sysProps = new HashMap<>();
        sysProps.put("app.testSys", "fromSysPropTest");
        sysProps.put("test", "fromSysPropTest3");
        sysProps.put("common.testSysCom", "fromSysProp1");
        return sysProps;
    }

    @Inject
    private AppConfig appConfig;

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    public static interface AppConfig extends SpincastConfig {

        public String getTest1();

        public String getTest1Stripped();

        public String getTestCommon();

        public String getTestSys1();

        public String getTest1SysStripped();

        public String getTestSysCommon();

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

            Map<String, String> vars = new HashMap<>();
            vars.put("app.testEnv", "fromEnvVar1");
            vars.put("test2", "fromEnvVar2");
            vars.put("common.testEnvCom", "fromEnvVar111");
            return vars.entrySet();
        }

        @Override
        public String getTest1() {
            return getString("app.testEnv");
        }

        @Override
        public String getTest1Stripped() {
            return getString("testEnv");
        }

        @Override
        public String getTestCommon() {
            return getString("testEnvCom");
        }

        @Override
        public String getTestSys1() {
            return getString("app.testEnv");
        }

        @Override
        public String getTest1SysStripped() {
            return getString("testEnv");
        }

        @Override
        public String getTestSysCommon() {
            return getString("testEnvCom");
        }
    }

    @Test
    public void unstrippedEnvVar() throws Exception {
        assertNull(getAppConfig().getTest1());
    }

    @Test
    public void strippedEnvVar() throws Exception {
        assertEquals("fromEnvVar1", getAppConfig().getTest1Stripped());
    }

    @Test
    public void strippedEnvVarCommon() throws Exception {
        assertEquals("fromEnvVar111", getAppConfig().getTestCommon());
    }

    @Test
    public void unstrippedSysProp() throws Exception {
        assertNull(getAppConfig().getTestSys1());
    }

    @Test
    public void strippedSysProp() throws Exception {
        assertEquals("fromEnvVar1", getAppConfig().getTest1SysStripped());
    }

    @Test
    public void strippedSysPropCommon() throws Exception {
        assertEquals("fromEnvVar111", getAppConfig().getTestSysCommon());
    }

}



