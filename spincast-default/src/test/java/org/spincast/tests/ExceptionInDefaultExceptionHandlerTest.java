package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;

import org.junit.Test;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.controllers.SpincastFrontController;
import org.spincast.core.exceptions.PublicException;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.exchange.IRequestContextFactory;
import org.spincast.core.exchange.RequestContextType;
import org.spincast.core.guice.SpincastRequestScope;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRouter;
import org.spincast.core.server.IServer;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Module;

public class ExceptionInDefaultExceptionHandlerTest extends DefaultIntegrationTestingBase {

    public static class CustomFrontController extends SpincastFrontController<IDefaultRequestContext> {

        @Inject
        public CustomFrontController(IRouter<IDefaultRequestContext> router,
                                     ISpincastConfig spincastConfig,
                                     ISpincastDictionary spincastDictionary,
                                     IServer server,
                                     IRequestContextFactory<IDefaultRequestContext> requestCreationFactory,
                                     SpincastRequestScope spincastRequestScope,
                                     @RequestContextType Type requestContextType) {
            super(router,
                  spincastConfig,
                  spincastDictionary,
                  server,
                  requestCreationFactory,
                  spincastRequestScope,
                  requestContextType);
        }

        @Override
        protected void defaultExceptionHandling(Object exchange, Throwable ex) throws Throwable {
            throw new RuntimeException("default handler exception");
        }
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

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

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                throw new PublicException("original message");
            }
        });

        getRouter().ALL("/*{path}").exception().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                throw new RuntimeException("custom handler exception");
            }
        });

        SpincastTestHttpResponse response = get("/one");

        // The original message should have been kept!
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertTrue(response.getContent() == null || "".equals(response.getContent()));
    }

}
