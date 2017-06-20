package org.spincast.quickstart.tests;

import org.spincast.core.config.SpincastConfig;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.quickstart.App;
import org.spincast.quickstart.config.AppConfig;
import org.spincast.quickstart.config.AppConfigDefault;
import org.spincast.quickstart.exchange.AppRequestContext;
import org.spincast.quickstart.exchange.AppWebsocketContext;
import org.spincast.testing.core.AppTestingConfigInfo;
import org.spincast.testing.core.IntegrationTestAppBase;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;

/**
 * Integration test base class specifically made for 
 * our application.
 */
public abstract class AppTestBase extends IntegrationTestAppBase<AppRequestContext, AppWebsocketContext> {

    @Override
    protected void initApp() {
        App.main(getMainArgs());
    }

    /**
     * Testing config
     */
    public static class AppTestingConfig extends AppConfigDefault {

        private int serverPort = -1;

        @Inject
        public AppTestingConfig(SpincastConfigPluginConfig spincastConfigPluginConfig) {
            super(spincastConfigPluginConfig);
        }

        /**
         * We do not run in "debug" mode.
         */
        @Override
        public boolean isDebugEnabled() {
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

    /**
     * We specify our testing configurations informations.
     */
    @Override
    protected AppTestingConfigInfo getAppTestingConfigInfo() {

        return new AppTestingConfigInfo() {

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

    protected String[] getMainArgs() {
        return null;
    }
}
