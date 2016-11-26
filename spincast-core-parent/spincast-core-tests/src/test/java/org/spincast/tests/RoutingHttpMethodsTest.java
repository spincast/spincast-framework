package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.exchange.RequestRequestContextAddon;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.Router;
import org.spincast.core.routing.RoutingResult;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.testing.core.SpincastTestBase;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class RoutingHttpMethodsTest extends SpincastTestBase {

    @Inject
    Router<DefaultRequestContext, DefaultWebsocketContext> router;

    @Before
    public void before() {
        getRouter().removeAllRoutes(true);
    }

    protected Router<DefaultRequestContext, DefaultWebsocketContext> getRouter() {
        return this.router;
    }

    @Override
    protected Injector createInjector() {
        return Guice.createInjector(new SpincastDefaultTestingModule());
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
    public void oneHttpMethodRoute() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.CONNECT, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.DELETE, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.HEAD, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.OPTIONS, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.PATCH, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.POST, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.PUT, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.TRACE, "http://localhost/"));
        assertNull(routingResult);

    }

    @Test
    public void someHttpMethodRouteAtLeastOne() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        try {
            router.SOME("/").save(SpincastTestUtils.dummyRouteHandler);
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void someHttpMethodRoute() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.SOME("/", HttpMethod.GET).save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.CONNECT, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.DELETE, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.HEAD, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.OPTIONS, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.PATCH, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.POST, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.PUT, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.TRACE, "http://localhost/"));
        assertNull(routingResult);

    }

    @Test
    public void someHttpMethodRoute2() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.SOME("/", HttpMethod.GET, HttpMethod.CONNECT).save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.CONNECT, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.DELETE, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.HEAD, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.OPTIONS, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.PATCH, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.POST, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.PUT, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.TRACE, "http://localhost/"));
        assertNull(routingResult);
    }

    @Test
    public void allHttpMethodRoute() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.CONNECT, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.DELETE, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.HEAD, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.OPTIONS, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.PATCH, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.POST, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.PUT, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.TRACE, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
    }

}
