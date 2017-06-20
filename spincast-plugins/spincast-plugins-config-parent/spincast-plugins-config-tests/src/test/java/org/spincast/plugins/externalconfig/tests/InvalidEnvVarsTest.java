package org.spincast.plugins.externalconfig.tests;

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
import org.spincast.testing.utils.ExpectingBeforeClassException;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Scopes;

//==========================================
// We expect an Exception!
//==========================================
@ExpectingBeforeClassException
public class InvalidEnvVarsTest extends ConfigTestingBase {

    /**
     * We manage the configurations by ourself
     */
    @Override
    protected boolean isGuiceTweakerAutoTestingConfigBindings() {
        return false;
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

        public String getTest();
    }

    public static class AppConfigDefault extends SpincastConfigDefault implements AppConfig {

        @Inject
        public AppConfigDefault(SpincastConfigPluginConfig spincastConfigPluginConfig) {
            super(spincastConfigPluginConfig);
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



