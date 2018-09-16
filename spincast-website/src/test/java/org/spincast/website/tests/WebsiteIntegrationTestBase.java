package org.spincast.website.tests;

import java.util.ArrayList;
import java.util.List;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.TestingMode;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.shaded.org.apache.commons.lang3.tuple.ImmutablePair;
import org.spincast.shaded.org.apache.commons.lang3.tuple.Pair;
import org.spincast.testing.core.AppBasedTestingBase;
import org.spincast.testing.core.AppTestingConfigs;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.website.App;
import org.spincast.website.AppConfig;
import org.spincast.website.AppConfigDefault;
import org.spincast.website.exchange.AppRequestContext;

import com.google.inject.Inject;

/**
 * Integration test base class specifically made for 
 * our application.
 */
public abstract class WebsiteIntegrationTestBase extends AppBasedTestingBase<AppRequestContext, DefaultWebsocketContext> {

    @Override
    protected void callAppMainMethod() {
        App.main(new String[]{});
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
                return WebsiteTestingConfig.class;
            }

            @Override
            public Class<?> getAppConfigInterface() {
                return AppConfig.class;
            }

            @Override
            public Class<?> getAppConfigTestingImplementationClass() {
                return WebsiteTestingConfig.class;
            }
        };
    }

    /**
     * Our testing configurations.
     */
    public static class WebsiteTestingConfig extends AppConfigDefault {

        protected Integer port;

        @Inject
        public WebsiteTestingConfig(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public int getHttpServerPort() {

            if (this.port == null) {
                //==========================================
                // Starts the application to test on a
                // free port.
                //==========================================
                this.port = SpincastTestingUtils.findFreePort();
            }

            return this.port;
        }

        @Override
        public String getPublicUrlBase() {
            return "http://localhost:" + getHttpServerPort();
        }

        @Override
        public boolean isValidateLocalhostHost() {
            return false;
        }

        @Override
        public boolean isEnableCookiesValidator() {
            return false;
        }

        @Override
        public boolean isDevelopmentMode() {
            return false;
        }

        @Override
        public List<Pair<String, String>> getAdminUsernamesPasswords() {

            List<Pair<String, String>> ids = new ArrayList<Pair<String, String>>();
            ids.add(new ImmutablePair<String, String>("user1", "pass1"));
            ids.add(new ImmutablePair<String, String>("user2", "pass2"));

            return ids;
        }
    }

}
