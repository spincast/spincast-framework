package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;

public class RequestScopeTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected Module getExtraOverridingModule() {

        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(ServiceClass.class).in(Scopes.SINGLETON);
                bind(ControllerClass.class).in(Scopes.SINGLETON);

                // Bind a test class in the REQUEST scope
                bind(TestRequestScopeClass.class).in(SpincastGuiceScopes.REQUEST);
            }
        };
    }

    public static class TestRequestScopeClass {
    }

    public static class ServiceClass {

        private final Provider<DefaultRequestContext> requestContextProvider;
        private final Provider<DefaultRequestContext> requestContextProvider2;

        //==========================================
        // We have to use Providers since those objects are Request scoped!
        //==========================================
        @Inject
        public ServiceClass(Provider<DefaultRequestContext> requestContextProvider,
                            Provider<DefaultRequestContext> requestContextProvider2,
                            Provider<TestRequestScopeClass> testRequestScopeClassProvider) {
            assertNotNull(requestContextProvider);
            assertNotNull(requestContextProvider2);
            assertNotNull(testRequestScopeClassProvider);
            this.requestContextProvider = requestContextProvider;
            this.requestContextProvider2 = requestContextProvider2;
        }

        public void setCookie() {
            DefaultRequestContext requestContext1 = this.requestContextProvider.get();
            assertNotNull(requestContext1);

            DefaultRequestContext requestContext2 = this.requestContextProvider2.get();
            assertNotNull(requestContext2);
            assertTrue(requestContext1 == requestContext2);

            requestContext1.response().setCookieSession("testCookie", "testValue");

            requestContext1.response().sendPlainText(requestContext1.getLocaleToUse().toString());
        }
    }

    public static class ControllerClass {

        private final ServiceClass serviceClass;

        @Inject
        public ControllerClass(ServiceClass serviceClass) {
            this.serviceClass = serviceClass;
        }

        public void testHandlerMethod() {
            this.serviceClass.setCookie();
        }
    }

    @Inject
    protected ControllerClass controller;

    @Inject
    protected ControllerClass controllerOk;

    @Test
    public void oneInstanceOnly() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                DefaultRequestContext requestContextAsGeneric = context.guice()
                                                                       .getInstance(DefaultRequestContext.class);
                assertNotNull(requestContextAsGeneric);
                assertTrue(requestContextAsGeneric == context);

                DefaultRequestContext requestContextAsGeneric2 = context.guice()
                                                                        .getInstance(DefaultRequestContext.class);
                assertNotNull(requestContextAsGeneric2);
                assertTrue(requestContextAsGeneric2 == context);

                TestRequestScopeClass testRequestScopeClass1 = context.guice().getInstance(TestRequestScopeClass.class);
                assertNotNull(testRequestScopeClass1);

                TestRequestScopeClass testRequestScopeClass2 = context.guice().getInstance(TestRequestScopeClass.class);
                assertNotNull(testRequestScopeClass2);
                assertTrue(testRequestScopeClass1 == testRequestScopeClass2);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void getRequestScopeObjectsFromElsewhereThanHandler() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                RequestScopeTest.this.controller.testHandlerMethod();

                Cookie cookie = context.response().getCookieAdded("testCookie");
                assertNotNull(cookie);
                assertEquals("testValue", cookie.getValue());
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(getSpincastConfig().getDefaultLocale().toString(), response.getContentAsString());
    }

}
