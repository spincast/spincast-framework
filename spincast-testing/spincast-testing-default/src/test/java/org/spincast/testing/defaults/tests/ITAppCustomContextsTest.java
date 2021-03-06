package org.spincast.testing.defaults.tests;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.routing.Router;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.testing.core.AppBasedTestingBase;
import org.spincast.testing.core.AppTestingConfigs;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.testing.defaults.tests.utils.RequestContextTesting;
import org.spincast.testing.defaults.tests.utils.RequestContextTestingDefault;
import org.spincast.testing.defaults.tests.utils.WebsocketContextTesting;
import org.spincast.testing.defaults.tests.utils.WebsocketContextTestingDefault;

import com.google.inject.Inject;

public class ITAppCustomContextsTest extends AppBasedTestingBase<RequestContextTesting, WebsocketContextTesting> {

    @Inject
    protected App app;

    protected App getApp() {
        return this.app;
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
                return SpincastConfigTestingDefault.class;
            }

            @Override
            public Class<?> getAppConfigInterface() {
                return null;
            }

            @Override
            public Class<?> getAppConfigTestingImplementationClass() {
                return null;
            }
        };
    }

    /**
     * Testing App
     */
    public static class App {

        private Router<RequestContextTesting, WebsocketContextTesting> router;

        public Router<RequestContextTesting, WebsocketContextTesting> getRouter() {
            return this.router;
        }

        public static void main(String[] args) {
            Spincast.configure()
                    .requestContextImplementationClass(RequestContextTestingDefault.class)
                    .websocketContextImplementationClass(WebsocketContextTestingDefault.class)
                    .init(args);
        }

        @Inject
        public void init(Router<RequestContextTesting, WebsocketContextTesting> router) {
            this.router = router;
        }
    }

    @Override
    protected void callAppMainMethod() {
        App.main(null);
    }

    @Test
    public void initProperly() throws Exception {
        assertNotNull(getApp());
        assertNotNull(getApp().getRouter());
    }

}
