package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.controllers.FrontController;
import org.spincast.core.controllers.SpincastFrontController;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.exceptions.PublicExceptionDefault;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.exchange.RequestContextFactory;
import org.spincast.core.exchange.RequestContextType;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastRequestScope;
import org.spincast.core.json.JsonManager;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.Router;
import org.spincast.core.server.Server;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.core.xml.XmlManager;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class ExceptionInDefaultExceptionHandlerTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected Module getExtraOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(FrontController.class).to(CustomFrontController.class).in(Scopes.SINGLETON);
            }
        };
    }

    public static class CustomFrontController extends SpincastFrontController<DefaultRequestContext, DefaultWebsocketContext> {

        @Inject
        public CustomFrontController(Router<DefaultRequestContext, DefaultWebsocketContext> router,
                                     SpincastConfig spincastConfig,
                                     Dictionary dictionary,
                                     Server server,
                                     RequestContextFactory<DefaultRequestContext> requestCreationFactory,
                                     SpincastRequestScope spincastRequestScope,
                                     @RequestContextType Type requestContextType,
                                     JsonManager jsonManager,
                                     XmlManager xmlManager) {
            super(router,
                  spincastConfig,
                  dictionary,
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

    @Test
    public void lastResortExceptionHandler() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new PublicExceptionDefault("original message");
            }
        });

        getRouter().ALL("/*{path}").exception().handle(new Handler<DefaultRequestContext>() {

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
