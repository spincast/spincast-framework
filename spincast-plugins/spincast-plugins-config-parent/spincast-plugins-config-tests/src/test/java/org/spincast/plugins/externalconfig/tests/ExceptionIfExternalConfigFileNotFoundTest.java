package org.spincast.plugins.externalconfig.tests;

import static org.junit.Assert.fail;

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

@ExpectingBeforeClassException
public class ExceptionIfExternalConfigFileNotFoundTest extends ConfigTestingBase {

    /**
     * We manage the configurations by ourself
     */
    @Override
    protected boolean isGuiceTweakerAutoTestingConfigBindings() {
        return false;
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
        // Throws exception if not found!
        //==========================================
        @Override
        public boolean isThrowExceptionIfSpecifiedExternalConfigFileIsNotFound() {
            return true;
        }
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

        public String getTest1();
    }

    public static class AppConfigDefault extends SpincastConfigDefault implements AppConfig {

        @Inject
        public AppConfigDefault(SpincastConfigPluginConfig spincastConfigPluginConfig) {
            super(spincastConfigPluginConfig);
        }

        @Override
        public String getTest1() {
            return getString("app.test1");
        }
    }

    @Test
    public void emptyTest() throws Exception {
        fail();
    }

}



