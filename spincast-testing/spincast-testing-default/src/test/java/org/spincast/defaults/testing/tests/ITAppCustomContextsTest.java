package org.spincast.defaults.testing.tests;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.routing.Router;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.testing.tests.utils.RequestContextTesting;
import org.spincast.defaults.testing.tests.utils.RequestContextTestingDefault;
import org.spincast.defaults.testing.tests.utils.WebsocketContextTesting;
import org.spincast.defaults.testing.tests.utils.WebsocketContextTestingDefault;
import org.spincast.testing.core.IntegrationTestAppBase;

import com.google.inject.Inject;

public class ITAppCustomContextsTest extends IntegrationTestAppBase<RequestContextTesting, WebsocketContextTesting> {

    @Inject
    protected App app;

    protected App getApp() {
        return this.app;
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
                    .init();
        }

        @Inject
        public void init(Router<RequestContextTesting, WebsocketContextTesting> router) {
            this.router = router;
        }
    }

    @Override
    protected void initApp() {
        App.main(null);
    }

    @Test
    public void initProperly() throws Exception {
        assertNotNull(getApp());
        assertNotNull(getApp().getRouter());
    }

}
