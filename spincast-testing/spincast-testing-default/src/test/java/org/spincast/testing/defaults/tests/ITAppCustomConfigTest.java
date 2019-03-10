package org.spincast.testing.defaults.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.TestingMode;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.testing.core.AppTestingConfigs;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;

import com.google.inject.Inject;

public class ITAppCustomConfigTest extends IntegrationConfigTestClassBase {

    /**
     * Custom Spincast Config class
     */
    public static class SpincastConfigCustom extends SpincastConfigTestingDefault {

        /**
         * Constructor
         */
        @Inject
        protected SpincastConfigCustom(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public int getHttpServerPort() {
            return 42;
        }
    }


    @Override
    protected AppTestingConfigs getAppTestingConfigs() {

        return new AppTestingConfigs() {

            @Override
            public boolean isBindAppClass() {
                return true;
            }

            @Override
            public Class<? extends SpincastConfig> getSpincastConfigTestingImplementationClass() {
                return SpincastConfigCustom.class;
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

    @Inject
    protected App app;

    protected App getApp() {
        return this.app;
    }

    /**
     * Testing App
     */
    public static class App {

        private SpincastConfig spincastConfig;

        public SpincastConfig getSpincastConfig() {
            return this.spincastConfig;
        }

        public static void main(String[] args) {
            Spincast.init(args);
        }

        @Inject
        public void init(SpincastConfig spincastConfig) {
            this.spincastConfig = spincastConfig;
        }
    }

    @Override
    protected void callAppMainMethod() {
        App.main(null);
    }

    @Test
    public void test() throws Exception {
        assertEquals(42, getSpincastConfig().getHttpServerPort());
    }

}
