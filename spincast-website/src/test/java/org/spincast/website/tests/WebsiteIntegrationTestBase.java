package org.spincast.website.tests;

import java.util.ArrayList;
import java.util.List;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.shaded.org.apache.commons.lang3.tuple.ImmutablePair;
import org.spincast.shaded.org.apache.commons.lang3.tuple.Pair;
import org.spincast.testing.core.AppTestingConfigInfo;
import org.spincast.testing.core.IntegrationTestAppBase;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.website.App;
import org.spincast.website.AppConfig;
import org.spincast.website.AppConfigDefault;
import org.spincast.website.exchange.AppRequestContext;

import com.google.inject.Inject;

/**
 * Integration test base class specifically made for 
 * our application.
 */
public abstract class WebsiteIntegrationTestBase extends IntegrationTestAppBase<AppRequestContext, DefaultWebsocketContext> {

    /**
     * We specify our testing configurations informations.
     */
    @Override
    protected AppTestingConfigInfo getAppTestingConfigInfo() {

        return new AppTestingConfigInfo() {

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
        public WebsiteTestingConfig(SpincastConfigPluginConfig spincastConfigPluginConfig) {
            super(spincastConfigPluginConfig);
        }

        @Override
        public int getHttpServerPort() {

            if (this.port == null) {
                //==========================================
                // Starts the application to test on a
                // free port.
                //==========================================
                this.port = SpincastTestUtils.findFreePort();
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
        public boolean isDebugEnabled() {
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

    @Override
    protected void initApp() {
        App.main(new String[]{});
    }
}
