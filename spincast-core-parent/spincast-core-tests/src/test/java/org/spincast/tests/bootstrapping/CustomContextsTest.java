package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;
import org.spincast.core.routing.Router;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.websocket.WebsocketContextBase;
import org.spincast.core.websocket.WebsocketContextBaseDeps;
import org.spincast.core.websocket.WebsocketPeerManager;
import org.spincast.defaults.bootstrapping.Spincast;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class CustomContextsTest {

    private static boolean initCalled = false;
    private static CustomContextsTest instance;

    @Test
    public void test() throws Exception {
        CustomContextsTest.main(null);
        assertNotNull(instance);
        assertNotNull(instance.router);
    }

    //==========================================
    // Custom Request Context type
    //==========================================
    public static interface RequestContextTesting extends RequestContext<RequestContextTesting> {

        public String test();
    }

    public static class RequestContextTestingDefault extends RequestContextBase<RequestContextTesting>
                                                     implements RequestContextTesting {

        @AssistedInject
        public RequestContextTestingDefault(@Assisted Object exchange,
                                            RequestContextBaseDeps<RequestContextTesting> requestContextBaseDeps) {
            super(exchange, requestContextBaseDeps);
        }

        @Override
        public String test() {
            return "42";
        }
    }

    //==========================================
    // Custom WebSocket Context type
    //==========================================
    public static interface WebsocketContextTesting extends WebsocketContext<WebsocketContextTesting> {

        public String test2();
    }

    public static class WebsocketContextTestingDefault extends WebsocketContextBase<WebsocketContextTesting>
                                                       implements WebsocketContextTesting {

        @AssistedInject
        public WebsocketContextTestingDefault(@Assisted("endpointId") String endpointId,
                                              @Assisted("peerId") String peerId,
                                              @Assisted WebsocketPeerManager peerManager,
                                              WebsocketContextBaseDeps<WebsocketContextTesting> deps) {
            super(endpointId,
                  peerId,
                  peerManager,
                  deps);
        }

        @Override
        public String test2() {
            return "Stromgol";
        }
    }

    public static void main(String[] args) {

        Injector guice = Spincast.configure()
                                 .requestContextImplementationClass(RequestContextTestingDefault.class)
                                 .websocketContextImplementationClass(WebsocketContextTestingDefault.class)
                                 .init(args);
        assertTrue(initCalled);

        instance = guice.getInstance(CustomContextsTest.class);
    }

    @Inject
    public Router<RequestContextTesting, WebsocketContextTesting> router;

    @Inject
    protected void init() {
        initCalled = true;
    }
}
