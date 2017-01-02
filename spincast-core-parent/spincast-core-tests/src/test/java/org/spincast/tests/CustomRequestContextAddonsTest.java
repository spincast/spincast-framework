package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastGuiceScopes;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.IntegrationTestNoAppBase;
import org.spincast.tests.CustomRequestContextAddonsTest.TestRequestContext;
import org.spincast.tests.varia.RequestContextAddon;
import org.spincast.tests.varia.RequestContextAddonDefault;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class CustomRequestContextAddonsTest extends IntegrationTestNoAppBase<TestRequestContext, DefaultWebsocketContext> {

    @Override
    protected Injector createInjector() {
        return Spincast.configure()
                       .requestContextImplementationClass(TestRequestContextDefault.class)
                       .module(new SpincastGuiceModuleBase() {

                           @Override
                           protected void configure() {

                               //==========================================
                               // Bind the request context add-on in Request scope!
                               //==========================================
                               bind(RequestContextAddon.class).to(RequestContextAddonDefault.class)
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
                       })
                       .init();
    }

    public static class Singleton {
    }

    public static class TestNotSingletonNorRequestScoped {
    }

    public static interface TestRequestContext extends RequestContext<TestRequestContext> {

        public Map<Key<?>, Object> getInstanceFromGuiceCachePublic();
    }

    public static class TestRequestContextDefault extends RequestContextBase<TestRequestContext>
                                                  implements TestRequestContext {

        @AssistedInject
        public TestRequestContextDefault(@Assisted Object exchange,
                                         RequestContextBaseDeps<TestRequestContext> requestContextBaseDeps) {
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

    @Test
    public void cacheForRequestScopedObjects() throws Exception {

        getRouter().GET("/one").save(new Handler<TestRequestContext>() {

            @Override
            public void handle(TestRequestContext context) {

                RequestContextAddon requestContextAddon1 = context.get(RequestContextAddon.class);
                assertNotNull(requestContextAddon1);
                assertTrue(context.getInstanceFromGuiceCachePublic().containsKey(Key.get(RequestContextAddon.class)));

                RequestContextAddon requestContextAddon2 = context.get(RequestContextAddon.class);
                assertTrue(requestContextAddon1 == requestContextAddon2);
                assertTrue(context.getInstanceFromGuiceCachePublic().containsKey(Key.get(RequestContextAddon.class)));

            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void cacheForSingletons() throws Exception {

        getRouter().GET("/one").save(new Handler<TestRequestContext>() {

            @Override
            public void handle(TestRequestContext context) {

                Singleton singleton = context.get(Singleton.class);
                assertNotNull(singleton);
                assertTrue(context.getInstanceFromGuiceCachePublic().containsKey(Key.get(Singleton.class)));

                Singleton singleton2 = context.get(Singleton.class);
                assertTrue(singleton == singleton2);
                assertTrue(context.getInstanceFromGuiceCachePublic().containsKey(Key.get(Singleton.class)));
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void noCacheForOtherScopedObjects() throws Exception {

        getRouter().GET("/one").save(new Handler<TestRequestContext>() {

            @Override
            public void handle(TestRequestContext context) {

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

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void useAddon() throws Exception {

        getRouter().GET("/one").save(new Handler<TestRequestContext>() {

            @Override
            public void handle(TestRequestContext context) {
                context.get(RequestContextAddonDefault.class).addonMethod1();
                String str = context.get(RequestContextAddonDefault.class).addonMethod2();
                context.response().sendPlainText(str);
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("addonMethod1addonMethod2", response.getContentAsString());
    }

}
