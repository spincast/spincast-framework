package org.spincast.defaults.testing.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.IntegrationTestAppDefaultContextsBase;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.testing.core.AppTestingConfigInfo;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;

import com.google.inject.Inject;

public class ITAppCustomConfigTest extends IntegrationTestAppDefaultContextsBase {

    /**
     * Custom Spincast Config class
     */
    public static class SpincastConfigCustom extends SpincastConfigTestingDefault {

        /**
         * Constructor
         */
        @Inject
        protected SpincastConfigCustom(SpincastConfigPluginConfig spincastConfigPluginConfig) {
            super(spincastConfigPluginConfig);
        }

        @Override
        public int getHttpServerPort() {
            return 42;
        }
    }


    @Override
    protected AppTestingConfigInfo getAppTestingConfigInfo() {
        return new AppTestingConfigInfo() {

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
    protected void initApp() {
        App.main(null);
    }

    @Test
    public void test() throws Exception {
        assertEquals(42, getSpincastConfig().getHttpServerPort());
    }

}
