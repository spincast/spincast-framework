package org.spincast.plugins.externalconfig.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.config.SpincastConfigPluginConfigDefault;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Scopes;

public class DefaultConfigTest extends ConfigTestingBase {

    /**
     * We manage the configurations by ourself
     */
    @Override
    protected boolean isGuiceTweakerAutoTestingConfigBindings() {
        return false;
    }

    /**
     * Extra System properties
     */
    @Override
    protected Map<String, String> getExtraSystemProperties() {

        Map<String, String> sysProps = new HashMap<>();
        sysProps.put("app.test3", "fromSysProp3");
        sysProps.put("app.test4", "fromSysProp4");
        sysProps.put("app.test6", "fromSysProp6");
        sysProps.put("app.test8", "fromSysProp8");

        return sysProps;
    }

    protected static class AppSpincastConfigPluginConfig extends SpincastConfigPluginConfigDefault {
        // defaults
    }

    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .bindCurrentClass(false)
                       .module(new SpincastGuiceModuleBase() {

                           @Override
                           protected void configure() {
                               bind(AppConfig.class).to(AppConfigDefault.class).in(Scopes.SINGLETON);
                               bind(SpincastConfigPluginConfig.class).to(AppSpincastConfigPluginConfig.class)
                                                                     .in(Scopes.SINGLETON);
                           }
                       })
                       .init(new String[]{});
    }

    @Inject
    private AppConfig appConfig;

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    public static interface AppConfig extends SpincastConfig {

        public String getHardcodedVal();

        public String getNotFound();

        public String getDefaultValueNotFound();

        public String getDefaultValueFound();

        public String getTest1();

        public String getTest2();

        public String getTest3();

        public String getTest4();

        public String getTest5();

        public String getTest6();

        public String getTest7();

        public String getTest8();

        public String getTest9();
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
            vars.put("app.test2", "fromEnvVar2");
            vars.put("app.test4", "fromEnvVar4");
            vars.put("app.test5", "fromEnvVar5");
            vars.put("app.test9", "fromEnvVar9");
            return vars.entrySet();
        }

        @Override
        public String getHttpsKeyStorePath() {
            return "overriden config";
        }

        @Override
        public String getHardcodedVal() {
            return "I am hardcoded";
        }

        @Override
        public String getNotFound() {
            return getString("nope");
        }

        @Override
        public String getDefaultValueNotFound() {
            return getString("nope", "some default value");
        }

        @Override
        public String getDefaultValueFound() {
            return getString("app.test1", "some default value");
        }

        @Override
        public String getTest1() {
            return getString("app.test1");
        }

        @Override
        public String getTest2() {
            return getString("app.test2");
        }

        @Override
        public String getTest3() {
            return getString("app.test3");
        }

        @Override
        public String getTest4() {
            return getString("app.test4");
        }

        @Override
        public String getTest5() {
            return getString("app.test5");
        }

        @Override
        public String getTest6() {
            return getString("app.test6");
        }

        @Override
        public String getTest7() {
            return getString("app.test7");
        }

        @Override
        public String getTest8() {
            return getString("app.test8");
        }

        @Override
        public String getTest9() {
            return getString("app.test9");
        }

    }

    @Test
    public void notFound() throws Exception {
        assertNull(getAppConfig().getNotFound());
    }

    @Test
    public void getDefaultValueNotFound() throws Exception {
        assertEquals("some default value", getAppConfig().getDefaultValueNotFound());
    }

    @Test
    public void getDefaultValueFound() throws Exception {
        assertEquals("fromClasspathConfigFile1", getAppConfig().getDefaultValueFound());
    }

    @Test
    public void defaultConfig() throws Exception {
        assertEquals(44419, getSpincastConfig().getHttpServerPort());
        assertEquals(44419, getAppConfig().getHttpServerPort());
    }

    @Test
    public void hardcodedConfig() throws Exception {
        assertEquals("I am hardcoded", getAppConfig().getHardcodedVal());
    }

    @Test
    public void overridenHardcodedConfig() throws Exception {
        assertEquals("overriden config", getSpincastConfig().getHttpsKeyStorePath());
        assertEquals("overriden config", getAppConfig().getHttpsKeyStorePath());
    }

    @Test
    public void classpathFileConfigOnly() throws Exception {
        assertEquals("fromClasspathConfigFile1", getAppConfig().getTest1());
    }

    @Test
    public void envVarOverridesClasspathFile() throws Exception {
        assertEquals("fromEnvVar2", getAppConfig().getTest2());
    }

    @Test
    public void sysPropOverridesClasspathFile() throws Exception {
        assertEquals("fromSysProp3", getAppConfig().getTest3());
    }

    @Test
    public void sysPropOverridesEnvVar() throws Exception {
        assertEquals("fromSysProp4", getAppConfig().getTest4());
    }

    @Test
    public void envVarOnly() throws Exception {
        assertEquals("fromEnvVar5", getAppConfig().getTest5());
    }

    @Test
    public void sysPropOnly() throws Exception {
        assertEquals("fromSysProp6", getAppConfig().getTest6());
    }

    @Test
    public void externalFileOverridesClasspathFile() throws Exception {
        assertEquals("fromExternalConfigFile7", getAppConfig().getTest7());
    }

    @Test
    public void sysPopOverridesExternalFile() throws Exception {
        assertEquals("fromSysProp8", getAppConfig().getTest8());
    }

    @Test
    public void envVarOverridesExternalFileByDefault() throws Exception {
        assertEquals("fromEnvVar9", getAppConfig().getTest9());
    }



}



