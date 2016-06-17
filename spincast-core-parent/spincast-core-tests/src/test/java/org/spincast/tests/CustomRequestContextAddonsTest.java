package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.SpincastGuiceModuleBasedIntegrationTestBase;
import org.spincast.tests.CustomRequestContextAddonsTest.ITestRequestContext;
import org.spincast.tests.varia.IRequestContextAddon;
import org.spincast.tests.varia.RequestContextAddon;

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class CustomRequestContextAddonsTest extends
                                            SpincastGuiceModuleBasedIntegrationTestBase<ITestRequestContext, IDefaultWebsocketContext> {

    public static class Singleton {
    }

    public static class TestNotSingletonNorRequestScoped {
    }

    public static interface ITestRequestContext extends IRequestContext<ITestRequestContext> {

        public Map<Key<?>, Object> getInstanceFromGuiceCachePublic();
    }

    public static class TestRequestContext extends RequestContextBase<ITestRequestContext>
                                           implements ITestRequestContext {

        @AssistedInject
        public TestRequestContext(@Assisted Object exchange,
                                  RequestContextBaseDeps<ITestRequestContext> requestContextBaseDeps) {
            super(exchange, requestContextBaseDeps);
        }

        //==========================================
        // Make getInstanceFromGuiceCache() public.
        //==========================================
        @Override
        public Map<Key<?>, Object> getInstanceFromGuiceCachePublic() {
            return getInstanceFromGuiceCache();
        }
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();

                //==========================================
                // Bind the request context add-on in Request scope!
                //==========================================
                bind(IRequestContextAddon.class).to(RequestContextAddon.class)
                                                .in(SpincastGuiceScopes.REQUEST);

                //==========================================
                // Bind an object as singleton
                //==========================================
                bind(Singleton.class).in(Scopes.SINGLETON);

                //==========================================
                // Bind an object not as singleton or request scoped
                //==========================================
                bind(TestNotSingletonNorRequestScoped.class);
            }

            @Override
            protected Class<? extends IRequestContext<?>> getRequestContextImplementationClass() {
                return TestRequestContext.class;
            }

        };
    }

    @Test
    public void cacheForRequestScopedObjects() throws Exception {

        getRouter().GET("/one").save(new IHandler<ITestRequestContext>() {

            @Override
            public void handle(ITestRequestContext context) {

                IRequestContextAddon requestContextAddon1 = context.get(IRequestContextAddon.class);
                assertNotNull(requestContextAddon1);
                assertTrue(context.getInstanceFromGuiceCachePublic().containsKey(Key.get(IRequestContextAddon.class)));

                IRequestContextAddon requestContextAddon2 = context.get(IRequestContextAddon.class);
                assertTrue(requestContextAddon1 == requestContextAddon2);
                assertTrue(context.getInstanceFromGuiceCachePublic().containsKey(Key.get(IRequestContextAddon.class)));

            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void cacheForSingletons() throws Exception {

        getRouter().GET("/one").save(new IHandler<ITestRequestContext>() {

            @Override
            public void handle(ITestRequestContext context) {

                Singleton singleton = context.get(Singleton.class);
                assertNotNull(singleton);
                assertTrue(context.getInstanceFromGuiceCachePublic().containsKey(Key.get(Singleton.class)));

                Singleton singleton2 = context.get(Singleton.class);
                assertTrue(singleton == singleton2);
                assertTrue(context.getInstanceFromGuiceCachePublic().containsKey(Key.get(Singleton.class)));
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void noCacheForOtherScopedObjects() throws Exception {

        getRouter().GET("/one").save(new IHandler<ITestRequestContext>() {

            @Override
            public void handle(ITestRequestContext context) {

                TestNotSingletonNorRequestScoped testNotSingleton = context.get(TestNotSingletonNorRequestScoped.class);
                assertNotNull(testNotSingleton);
                assertFalse(context.getInstanceFromGuiceCachePublic()
                                   .containsKey(Key.get(TestNotSingletonNorRequestScoped.class)));

                TestNotSingletonNorRequestScoped testNotSingleton2 = context.get(TestNotSingletonNorRequestScoped.class);
                assertFalse(testNotSingleton == testNotSingleton2);
                assertFalse(context.getInstanceFromGuiceCachePublic()
                                   .containsKey(Key.get(TestNotSingletonNorRequestScoped.class)));
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void useAddon() throws Exception {

        getRouter().GET("/one").save(new IHandler<ITestRequestContext>() {

            @Override
            public void handle(ITestRequestContext context) {
                context.get(IRequestContextAddon.class).addonMethod1();
                String str = context.get(IRequestContextAddon.class).addonMethod2();
                context.response().sendPlainText(str);
            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("addonMethod1addonMethod2", response.getContentAsString());
    }

}
