package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.mockito.Mockito;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.exchange.IRequestRequestContextAddon;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRouter;
import org.spincast.core.routing.IRoutingResult;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.SpincastTestConfig;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Module;

public class RoutingCaseSensitiveTest extends SpincastDefaultNoAppIntegrationTestBase {

    //==========================================
    // Remove all routes, even Spincast ones
    //==========================================
    @Override
    protected boolean removeSpincastRoutesToo() {
        return true;
    }

    public static class TestingSpincastConfigCaseSensitive extends SpincastTestConfig {

        @Override
        public boolean isRoutesCaseSensitive() {
            return true;
        }
    }

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected Class<? extends ISpincastConfig> getSpincastConfigClass() {
                return TestingSpincastConfigCaseSensitive.class;
            }
        };
    }

    @Test
    public void predefinedPatternAlphaCaseSensitive() throws Exception {

        getRouter().GET("/${param1:<A>}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(context.request().getPathParam("param1"));
            }
        });

        IHttpResponse response = GET("/a").send();
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

    protected IDefaultRequestContext getRequestContextMock(HttpMethod httpMethod, String url) {

        @SuppressWarnings("unchecked")
        IRequestRequestContextAddon<IDefaultRequestContext> requestAddon = Mockito.mock(IRequestRequestContextAddon.class);
        Mockito.when(requestAddon.getHttpMethod()).thenReturn(httpMethod);
        Mockito.when(requestAddon.getFullUrl()).thenReturn(url);

        IDefaultRequestContext requestContext = Mockito.mock(IDefaultRequestContext.class);
        Mockito.when(requestContext.request()).thenReturn(requestAddon);

        return requestContext;
    }

    @Test
    public void oneTokenCaseSensitive() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult =
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
