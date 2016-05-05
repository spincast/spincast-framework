package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.cookies.ICookie;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;

public class RequestScopeTest extends DefaultIntegrationTestingBase {

    public static class TestRequestScopeClass {
    }

    public static class ServiceClass {

        private final Provider<IDefaultRequestContext> requestContextProvider;
        private final Provider<IDefaultRequestContext> requestContextProvider2;

        //==========================================
        // We have to use Providers since those objects are Request scoped!
        //==========================================
        @Inject
        public ServiceClass(Provider<IDefaultRequestContext> requestContextProvider,
                            Provider<IDefaultRequestContext> requestContextProvider2,
                            Provider<TestRequestScopeClass> testRequestScopeClassProvider) {
            assertNotNull(requestContextProvider);
            assertNotNull(requestContextProvider2);
            assertNotNull(testRequestScopeClassProvider);
            this.requestContextProvider = requestContextProvider;
            this.requestContextProvider2 = requestContextProvider2;
        }

        public void setCookie() {
            IDefaultRequestContext requestContext1 = this.requestContextProvider.get();
            assertNotNull(requestContext1);

            IDefaultRequestContext requestContext2 = this.requestContextProvider2.get();
            assertNotNull(requestContext2);
            assertTrue(requestContext1 == requestContext2);

            requestContext1.cookies().addCookie("testCookie", "testValue");

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

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();

                bind(ServiceClass.class).in(Scopes.SINGLETON);
                bind(ControllerClass.class).in(Scopes.SINGLETON);

                // Bind a test class in the REQUEST scope
                bind(TestRequestScopeClass.class).in(SpincastGuiceScopes.REQUEST);
            }
        };
    }

    @Test
    public void oneInstanceOnly() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IDefaultRequestContext requestContextAsGeneric = context.guice()
                                                                        .getInstance(IDefaultRequestContext.class);
                assertNotNull(requestContextAsGeneric);
                assertTrue(requestContextAsGeneric == context);

                IDefaultRequestContext requestContextAsGeneric2 = context.guice()
                                                                         .getInstance(IDefaultRequestContext.class);
                assertNotNull(requestContextAsGeneric2);
                assertTrue(requestContextAsGeneric2 == context);

                TestRequestScopeClass testRequestScopeClass1 = context.guice().getInstance(TestRequestScopeClass.class);
                assertNotNull(testRequestScopeClass1);

                TestRequestScopeClass testRequestScopeClass2 = context.guice().getInstance(TestRequestScopeClass.class);
                assertNotNull(testRequestScopeClass2);
                assertTrue(testRequestScopeClass1 == testRequestScopeClass2);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void getRequestScopeObjectsFromElsewhereThanIHandler() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                RequestScopeTest.this.controller.testHandlerMethod();

                ICookie cookie = context.cookies().getCookie("testCookie");
                assertNotNull(cookie);
                assertEquals("testValue", cookie.getValue());
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(getSpincastConfig().getDefaultLocale().toString(), response.getContentAsString());
    }

}
