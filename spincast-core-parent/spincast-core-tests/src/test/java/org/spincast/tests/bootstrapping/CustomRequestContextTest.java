package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;
import org.spincast.core.routing.Router;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.bootstrapping.Spincast;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class CustomRequestContextTest {

    private static boolean initCalled = false;
    private static CustomRequestContextTest instance;

    @Test
    public void test() throws Exception {
        CustomRequestContextTest.main(null);
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

    public static void main(String[] args) {

        Injector guice = Spincast.configure()
                                 .requestContextImplementationClass(RequestContextTestingDefault.class)
                                 .init();
        assertTrue(initCalled);

        instance = guice.getInstance(CustomRequestContextTest.class);
    }

    @Inject
    public Router<RequestContextTesting, DefaultWebsocketContext> router;

    @Inject
    protected void init() {
        initCalled = true;
    }
}
