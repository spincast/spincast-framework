package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.exchange.RequestRequestContextAddon;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.RouteHandlerMatch;
import org.spincast.core.routing.Router;
import org.spincast.core.routing.RoutingResult;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.testing.NoAppTestingBase;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;

public class RoutingTest extends NoAppTestingBase {

    @Inject
    Router<DefaultRequestContext, DefaultWebsocketContext> router;

    @Before
    public void before() {
        getRouter().removeAllRoutes(true);
    }

    protected Router<DefaultRequestContext, DefaultWebsocketContext> getRouter() {
        return this.router;
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
    public void addRemove() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        assertEquals(0, router.getGlobalBeforeFiltersRoutes().size());
        assertEquals(0, router.getMainRoutes().size());
        assertEquals(0, router.getGlobalAfterFiltersRoutes().size());

        router.GET("/").save(SpincastTestUtils.dummyRouteHandler);
        assertEquals(0, router.getGlobalBeforeFiltersRoutes().size());
        assertEquals(1, router.getMainRoutes().size());
        assertEquals(0, router.getGlobalAfterFiltersRoutes().size());

        router.ALL("/").pos(-10).save(SpincastTestUtils.dummyRouteHandler);
        assertEquals(1, router.getGlobalBeforeFiltersRoutes().size());
        assertEquals(1, router.getMainRoutes().size());
        assertEquals(0, router.getGlobalAfterFiltersRoutes().size());

        router.ALL("/").pos(10).save(SpincastTestUtils.dummyRouteHandler);
        assertEquals(1, router.getGlobalBeforeFiltersRoutes().size());
        assertEquals(1, router.getMainRoutes().size());
        assertEquals(1, router.getGlobalAfterFiltersRoutes().size());

        router.ALL("/").pos(-10).save(SpincastTestUtils.dummyRouteHandler);
        router.ALL("/").pos(10).save(SpincastTestUtils.dummyRouteHandler);
        assertEquals(2, router.getGlobalBeforeFiltersRoutes().size());
        assertEquals(1, router.getMainRoutes().size());
        assertEquals(2, router.getGlobalAfterFiltersRoutes().size());

        router.removeAllRoutes();
        assertEquals(0, router.getGlobalBeforeFiltersRoutes().size());
        assertEquals(0, router.getMainRoutes().size());
        assertEquals(0, router.getGlobalAfterFiltersRoutes().size());

    }

    @Test
    public void root() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        RoutingResult<DefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope?test=1#anchor"));

        //==========================================
        // We add the root route
        //==========================================
        router.GET("/").save(SpincastTestUtils.dummyRouteHandler);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost?test=1#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/?test=1#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080?test=1#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/?test=1#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        // Still nope for those!
        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope?test=1#anchor"));
        assertNull(routingResult);

        //==========================================
        // We remove the root route
        //==========================================
        router.removeAllRoutes();

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope/#anchor"));
        assertNull(routingResult);
    }

    @Test
    public void rootNoSlash() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost?test=1#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/?test=1#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080?test=1#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/?test=1#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        // Still nope for those!
        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080/nope?test=1#anchor"));
        assertNull(routingResult);
    }

    @Test
    public void oneToken() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(SpincastTestUtils.dummyRouteHandler);

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
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE/?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE/#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE?test=1#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/ONE/?test=1#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one?test=1#anchor"));
        assertNull(routingResult);
    }

    @Test
    public void oneTokenNoSlashPrefix() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("one").save(SpincastTestUtils.dummyRouteHandler);

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

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one?test=1#anchor"));
        assertNull(routingResult);

    }

    @Test
    public void oneTokenSlashSuffix() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one/").save(SpincastTestUtils.dummyRouteHandler);

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

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one?test=1#anchor"));
        assertNull(routingResult);

    }

    @Test
    public void oneTokenSlashSuffixNoSlashPrefix() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("one/").save(SpincastTestUtils.dummyRouteHandler);

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

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one?test=1#anchor"));
        assertNull(routingResult);
    }

    @Test
    public void twoTokens() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one/two").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two/?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two/#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two?test=1#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two/?test=1#anchor"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two/nope?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two/nope/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two/nope?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two/nope/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/nope/?test=1#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one?test=1"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one/#anchor"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/nope/one?test=1#anchor"));
        assertNull(routingResult);

    }

    @Test
    public void paramRoot() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/${param1}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        RouteHandlerMatch<DefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Handler<DefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        String value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("one", value);
    }

    @Test
    public void paramEmptyName() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/${}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        RouteHandlerMatch<DefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Handler<DefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(0, parameters.size());
    }

    @Test
    public void twoParams() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/${param1}/${param2}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        RouteHandlerMatch<DefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Handler<DefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(2, parameters.size());

        String value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("one", value);

        value = parameters.get("param2");
        assertNotNull(value);
        assertEquals("two", value);
    }

    @Test
    public void twoParamsSameName() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        try {
            router.GET("/${param1}/${param1}").save(SpincastTestUtils.dummyRouteHandler);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void twoParamsSameNameEmpty() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        // This is ok, we don't collect them!
        router.GET("/${}/${}").save(SpincastTestUtils.dummyRouteHandler);
    }

    @Test
    public void lotsdOfParamNamesEmpty() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        // Yeah, why not?
        router.GET("/${}/${}/*{}/${}").save(SpincastTestUtils.dummyRouteHandler);
    }

    @Test
    public void paramEmptyNameNotCollected() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one/${param1}/${}/${}/${param2}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/one/two/three/four/five?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        RouteHandlerMatch<DefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Handler<DefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(2, parameters.size());

        String value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("two", value);

        value = parameters.get("param2");
        assertNotNull(value);
        assertEquals("five", value);
    }

    @Test
    public void twoParamsSameNameOneSplat() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        try {
            router.GET("/${param1}/*{param1}").save(SpincastTestUtils.dummyRouteHandler);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void filterBeforeAfter() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        final Map<String, String> handlerCalled = new HashMap<String, String>();

        router.GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext exchange) {
                handlerCalled.put("handlerCalled", "route");
            }
        });
        router.ALL("/").pos(-10).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext exchange) {
                handlerCalled.put("handlerCalled", "before");
            }
        });
        router.ALL("/").pos(10).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext exchange) {
                handlerCalled.put("handlerCalled", "after");
            }
        });
        router.ALL("/").pos(-10).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext exchange) {
                handlerCalled.put("handlerCalled", "beforeAndAfter");
            }
        });
        router.ALL("/").pos(10).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext exchange) {
                handlerCalled.put("handlerCalled", "beforeAndAfter");
            }
        });

        RoutingResult<DefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));

        List<RouteHandlerMatch<DefaultRequestContext>> routeAndFilters = routingResult.getRouteHandlerMatches();
        assertEquals(5, routeAndFilters.size());

        RouteHandlerMatch<DefaultRequestContext> handlerMatch = routeAndFilters.get(0);
        assertTrue(handlerMatch.getPosition() < 0);
        handlerMatch.getHandler().handle(null);
        assertEquals("before", handlerCalled.get("handlerCalled"));

        handlerMatch = routeAndFilters.get(1);
        assertTrue(routeAndFilters.get(1).getPosition() < 0);
        handlerMatch.getHandler().handle(null);
        assertEquals("beforeAndAfter", handlerCalled.get("handlerCalled"));

        handlerMatch = routeAndFilters.get(2);
        assertEquals(0, routeAndFilters.get(2).getPosition());
        handlerMatch.getHandler().handle(null);
        assertEquals("route", handlerCalled.get("handlerCalled"));

        handlerMatch = routeAndFilters.get(3);
        assertTrue(handlerMatch.getPosition() > 0);
        handlerMatch.getHandler().handle(null);
        assertEquals("after", handlerCalled.get("handlerCalled"));

        handlerMatch = routeAndFilters.get(4);
        assertTrue(handlerMatch.getPosition() > 0);
        handlerMatch.getHandler().handle(null);
        assertEquals("beforeAndAfter", handlerCalled.get("handlerCalled"));

    }

    @Test
    public void twoSplats() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        // Two splat params not allowed...
        try {
            router.GET("/one/*{param1}/two/*{param1}").save(SpincastTestUtils.dummyRouteHandler);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void splatSimpleNoName() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/*{}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/111/222/333/444/555"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        RouteHandlerMatch<DefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Handler<DefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        // We don't collect params without a name...
        Map<String, String> parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(0, parameters.size());
    }

    @Test
    public void splat1() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/*{param1}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/111/222/333/444/555"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        RouteHandlerMatch<DefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Handler<DefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        String value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("111/222/333/444/555", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("", value);
    }

    @Test
    public void splat2() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("*{param1}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/111/222/333/444/555"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        RouteHandlerMatch<DefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Handler<DefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        String value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("111/222/333/444/555", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("", value);
    }

    @Test
    public void splatAndRegularParams() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/${param1}/*{param2}/${param3}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/111/222/333/444/555"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        RouteHandlerMatch<DefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Handler<DefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(3, parameters.size());

        String value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("111", value);

        value = parameters.get("param2");
        assertNotNull(value);
        assertEquals("222/333/444", value);

        value = parameters.get("param3");
        assertNotNull(value);
        assertEquals("555", value);
    }

    @Test
    public void splatAndRegularParams2() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/*{param1}/${param2}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/111/222/333/444/555"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        RouteHandlerMatch<DefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Handler<DefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(2, parameters.size());

        String value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("111/222/333/444", value);

        value = parameters.get("param2");
        assertNotNull(value);
        assertEquals("555", value);

    }

    @Test
    public void splatAndRegularParams3() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/${param1}/*{param2}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/111/222/333/444/555"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        RouteHandlerMatch<DefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Handler<DefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(2, parameters.size());

        String value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("111", value);

        value = parameters.get("param2");
        assertNotNull(value);
        assertEquals("222/333/444/555", value);
    }

    @Test
    public void splatAndRegularParamsAndHardcodedTokens() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/*{param1}/333/444/${param2}/666/${}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/111/222/333/444/555/666/777/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        RouteHandlerMatch<DefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Handler<DefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(2, parameters.size());

        String value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("111/222", value);

        value = parameters.get("param2");
        assertNotNull(value);
        assertEquals("555", value);
    }

    @Test
    public void splatAndRegularParamsAndHardcodedTokens2() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/${param1}/222/*{param2}/${param3}/777").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/111/222/333/444/555/666/777/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        RouteHandlerMatch<DefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Handler<DefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(3, parameters.size());

        String value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("111", value);

        value = parameters.get("param2");
        assertNotNull(value);
        assertEquals("333/444/555", value);

        value = parameters.get("param3");
        assertNotNull(value);
        assertEquals("666", value);
    }

    @Test
    public void splatAndRegularParamsAndHardcodedTokens3() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("111/${param1}/${param2}/*{param3}/").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/111/222/333/444/555/666/777/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        RouteHandlerMatch<DefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Handler<DefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(3, parameters.size());

        String value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("222", value);

        value = parameters.get("param2");
        assertNotNull(value);
        assertEquals("333", value);

        value = parameters.get("param3");
        assertNotNull(value);
        assertEquals("444/555/666/777", value);
    }

    @Test
    public void splatSlashOrNot() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one/*{splat}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        RouteHandlerMatch<DefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Map<String, String> parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        String value = parameters.get("splat");
        assertNotNull(value);
        assertEquals("two", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("splat");
        assertNotNull(value);
        assertEquals("", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getPathParams();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("splat");
        assertNotNull(value);
        assertEquals("", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/onenope"));
        assertNull(routingResult);

    }

    @Test
    public void notEnoughTokens() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one/two/three").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two"));
        assertNull(routingResult);

    }

    @Test
    public void tooManyTokens() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/one/two/three"));
        assertNull(routingResult);

    }

    @Test
    public void tooManyTokensButSplat() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one/*{any}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/one/two/three"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

    }

    @Test
    public void missingToken() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one/${param1}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/one"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

    }

    @Test
    public void emptyDynamicParam() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/${param1}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
    }

    @Test
    public void emptyDynamicParam2() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one/${param1}").save(SpincastTestUtils.dummyRouteHandler);

        RoutingResult<DefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                "http://localhost/one/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

    }

}
