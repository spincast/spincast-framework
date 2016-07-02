package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.exchange.IRequestRequestContextAddon;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IRouter;
import org.spincast.core.routing.IRoutingResult;
import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.testing.core.SpincastTestBase;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class RoutingHttpMethodsTest extends SpincastTestBase {

    @Inject
    IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router;

    @Before
    public void before() {
        getRouter().removeAllRoutes();
    }

    protected IRouter<IDefaultRequestContext, IDefaultWebsocketContext> getRouter() {
        return this.router;
    }

    @Override
    protected Injector createInjector() {
        return Guice.createInjector(new SpincastDefaultTestingModule());
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
    public void oneHttpMethodRoute() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult =
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        try {
            router.SOME("/").save(SpincastTestUtils.dummyRouteHandler);
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void someHttpMethodRoute() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.SOME("/", HttpMethod.GET).save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult =
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.SOME("/", HttpMethod.GET, HttpMethod.CONNECT).save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult =
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.ALL("/").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult =
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
