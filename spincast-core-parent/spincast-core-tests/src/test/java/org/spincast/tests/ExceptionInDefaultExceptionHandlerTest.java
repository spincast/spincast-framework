package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.controllers.SpincastFrontController;
import org.spincast.core.exceptions.PublicExceptionDefault;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.exchange.RequestContextFactory;
import org.spincast.core.exchange.RequestContextType;
import org.spincast.core.guice.SpincastRequestScope;
import org.spincast.core.json.JsonManager;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.Router;
import org.spincast.core.server.Server;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.core.xml.XmlManager;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Module;

public class ExceptionInDefaultExceptionHandlerTest extends SpincastDefaultNoAppIntegrationTestBase {

    public static class CustomFrontController extends SpincastFrontController<DefaultRequestContext, DefaultWebsocketContext> {

        @Inject
        public CustomFrontController(Router<DefaultRequestContext, DefaultWebsocketContext> router,
                                     SpincastConfig spincastConfig,
                                     SpincastDictionary spincastDictionary,
                                     Server server,
                                     RequestContextFactory<DefaultRequestContext> requestCreationFactory,
                                     SpincastRequestScope spincastRequestScope,
                                     @RequestContextType Type requestContextType,
                                     JsonManager jsonManager,
                                     XmlManager xmlManager) {
            super(router,
                  spincastConfig,
                  spincastDictionary,
                  server,
                  requestCreationFactory,
                  spincastRequestScope,
                  requestContextType,
                  jsonManager,
                  xmlManager);
        }

        @Override
        protected void defaultExceptionHandling(Object exchange, Throwable ex) throws Throwable {
            throw new RuntimeException("default handler exception");
        }
    }

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();
            }

            @Override
            protected Key<?> getFrontControllerKey() {
                return Key.get(CustomFrontController.class);
            }
        };
    }

    @Test
    public void lastResortExceptionHandler() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new PublicExceptionDefault("original message");
            }
        });

        getRouter().ALL("/*{path}").exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("custom handler exception");
            }
        });

        HttpResponse response = GET("/one").send();

        // The original message should have been kept!
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertTrue(response.getContentAsString() == null || "".equals(response.getContentAsString()));
    }

}
