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
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.config.SpincastConfigPluginConfigDefault;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class PrefixesTest extends ConfigTestingBase {

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass2() {
        return AppConfigDefault.class;
    }

    @Override
    protected Module getExtraOverridingModule2() {
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

            //==========================================
            // The "." is added if not specified
            //==========================================
            return Lists.newArrayList("my-prefix1.", "my-prefix2");
        }

        @Override
        public List<String> getSystemPropertiesPrefixes() {

            return Lists.newArrayList("my-prefix1", "my-prefix3");
        }

    }

    /**
     * Extra System properties
     */
    @Override
    protected Map<String, String> getExtraSystemProperties() {

        Map<String, String> sysProps = new HashMap<>();
        sysProps.put("app.test", "fromSysPropTest");
        sysProps.put("test2", "fromSysPropTest2");
        sysProps.put("my-prefix1.test", "fromSysProp1");
        sysProps.put("my-prefix2.test", "fromSysProp2");
        sysProps.put("my-prefix3.test", "fromSysProp3");

        return sysProps;
    }

    @Inject
    private AppConfig appConfig;

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    public static interface AppConfig extends SpincastConfig {

        public String getTest1();

        public String getTest2();

        public String getTest3();

        public String getTest4();

        public String getTest5();

    }

    public static class AppConfigDefault extends SpincastConfigDefault implements AppConfig {

        @Inject
        public AppConfigDefault(SpincastConfigPluginConfig spincastConfigPluginConfig) {
            super(spincastConfigPluginConfig);
        }

        /**
         * Mocks environment variables
         */
        @Override
        protected Set<Entry<String, String>> getEnvironmentVariables() {

            Map<String, String> vars = new HashMap<>();
            vars.put("app.test", "fromEnvVar1");
            vars.put("test2", "fromEnvVar2");
            vars.put("my-prefix1.test", "fromEnvVar111");
            vars.put("my-prefix2.test", "fromEnvVar222");
            vars.put("my-prefix3.test", "fromEnvVar333");
            return vars.entrySet();
        }

        @Override
        public String getTest1() {
            return getString("app.test");
        }

        @Override
        public String getTest2() {
            return getString("test2");
        }

        @Override
        public String getTest3() {
            return getString("my-prefix1.test");
        }

        @Override
        public String getTest4() {
            return getString("my-prefix2.test");
        }

        @Override
        public String getTest5() {
            return getString("my-prefix3.test");
        }
    }

    @Test
    public void appNotAPrefixAnymore() throws Exception {
        assertNull(getAppConfig().getTest1());
    }

    @Test
    public void test2NotAPrefix() throws Exception {
        assertNull(getAppConfig().getTest2());
    }

    @Test
    public void validPrefix() throws Exception {
        assertEquals("fromSysProp1", getAppConfig().getTest3());
    }

    @Test
    public void envVarOnly() throws Exception {
        assertEquals("fromEnvVar222", getAppConfig().getTest4());
    }

    @Test
    public void sysEnvOnly() throws Exception {
        assertEquals("fromSysProp3", getAppConfig().getTest5());
    }

}



