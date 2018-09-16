package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.mockito.Mockito;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.exchange.RequestRequestContextAddon;
import org.spincast.core.guice.TestingMode;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.Router;
import org.spincast.core.routing.RoutingResult;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.testing.core.utils.SpincastTestingUtils;

import com.google.inject.Inject;

public class RoutingCaseSensitiveTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass2() {
        return TestingSpincastConfigCaseSensitive.class;
    }

    //==========================================
    // Remove all routes, even Spincast ones
    //==========================================
    @Override
    protected boolean removeSpincastRoutesToo() {
        return true;
    }

    public static class TestingSpincastConfigCaseSensitive extends SpincastConfigTestingDefault {

        /**
         * Constructor
         */
        @Inject
        protected TestingSpincastConfigCaseSensitive(SpincastConfigPluginConfig spincastConfigPluginConfig,
                                                     @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public boolean isRoutesCaseSensitive() {
            return true;
        }
    }

    @Test
    public void predefinedPatternAlphaCaseSensitive() throws Exception {

        getRouter().GET("/${param1:<A>}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(context.request().getPathParam("param1"));
            }
        });

        HttpResponse response = GET("/a").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a", response.getContentAsString());

        response = GET("/aaaaa").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("aaaaa", response.getContentAsString());

        response = GET("/A").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/aA").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

    }

    protected DefaultRequestContext getRequestContextMock(HttpMethod httpMethod, String url) {

        @SuppressWarnings("unchecked")
        RequestRequestContextAddon<DefaultRequestContext> requestAddon = Mockito.mock(RequestRequestContextAddon.class);
        Mockito.when(requestAddon.getHttpMethod()).thenReturn(httpMethod);
        Mockito.when(requestAddon.getFullUrl()).thenReturn(url);

        DefaultRequestContext requestContext = Mockito.mock(DefaultRequestContext.class);
        Mockito.when(requestContext.request()).thenReturn(requestAddon);

        return requestContext;
    }

    @Test
    public void oneTokenCaseSensitive() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one").handle(SpincastTestingUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one?test=1#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/?test=1#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE/?test=1#anchor"));
        assertNull(routingResult);

    }

}
