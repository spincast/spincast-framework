package org.spincast.quickstart.tests;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.TestingMode;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.quickstart.App;
import org.spincast.quickstart.config.AppConfig;
import org.spincast.quickstart.config.AppConfigDefault;
import org.spincast.quickstart.exchange.AppRequestContext;
import org.spincast.quickstart.exchange.AppWebsocketContext;
import org.spincast.testing.core.AppBasedTestingBase;
import org.spincast.testing.core.AppTestingConfigs;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;

/**
 * Integration test base class specifically made for 
 * our application.
 */
public abstract class AppTestBase extends AppBasedTestingBase<AppRequestContext, AppWebsocketContext> {

    @Override
    protected void callAppMainMethod() {
        App.main(getMainArgs());
    }

    protected String[] getMainArgs() {
        return null;
    }

    /**
     * We specify our testing configurations informations.
     */
    @Override
    protected AppTestingConfigs getAppTestingConfigs() {

        return new AppTestingConfigs() {

            @Override
            public boolean isBindAppClass() {
                return true;
            }

            @Override
            public Class<? extends SpincastConfig> getSpincastConfigTestingImplementationClass() {
                return AppTestingConfig.class;
            }

            @Override
            public Class<?> getAppConfigInterface() {
                return AppConfig.class;
            }

            @Override
            public Class<?> getAppConfigTestingImplementationClass() {
                return AppTestingConfig.class;
            }
        };
    }

    /**
     * Testing config
     */
    public static class AppTestingConfig extends AppConfigDefault {

        private int serverPort = -1;

        @Inject
        public AppTestingConfig(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        /**
         * We do not run in "development" mode.
         */
        @Override
        public boolean isDevelopmentMode() {
            return false;
        }

        @Override
        public String getServerHost() {
            return "localhost";
        }

        /**
         * We use a free port.
         */
        @Override
        public int getHttpServerPort() {

            if (this.serverPort == -1) {

                //==========================================
                // We reserve 44419 for the default configuration.
                //==========================================
                do {
                    this.serverPort = SpincastTestUtils.findFreePort();
                } while (this.serverPort == 44419);
            }
            return this.serverPort;
        }

        @Override
        public String getPublicUrlBase() {
            return "http://" + getServerHost() + ":" + getHttpServerPort();
        }

        @Override
        public boolean isValidateLocalhostHost() {
            return false;
        }

        @Override
        public boolean isEnableCookiesValidator() {
            return false;
        }
    }
}
