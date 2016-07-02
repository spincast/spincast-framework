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
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.exchange.IRequestRequestContextAddon;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRouteHandlerMatch;
import org.spincast.core.routing.IRouter;
import org.spincast.core.routing.IRoutingResult;
import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.testing.core.SpincastTestBase;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class RoutingTest extends SpincastTestBase {

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
    public void addRemove() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        assertEquals(0, router.getGlobalBeforeFiltersRoutes().size());
        assertEquals(0, router.getMainRoutes().size());
        assertEquals(0, router.getGlobalAfterFiltersRoutes().size());

        router.GET("/").save(SpincastTestUtils.dummyRouteHandler);
        assertEquals(0, router.getGlobalBeforeFiltersRoutes().size());
        assertEquals(1, router.getMainRoutes().size());
        assertEquals(0, router.getGlobalAfterFiltersRoutes().size());

        router.before("/", SpincastTestUtils.dummyRouteHandler);
        assertEquals(1, router.getGlobalBeforeFiltersRoutes().size());
        assertEquals(1, router.getMainRoutes().size());
        assertEquals(0, router.getGlobalAfterFiltersRoutes().size());

        router.after("/", SpincastTestUtils.dummyRouteHandler);
        assertEquals(1, router.getGlobalBeforeFiltersRoutes().size());
        assertEquals(1, router.getMainRoutes().size());
        assertEquals(1, router.getGlobalAfterFiltersRoutes().size());

        router.beforeAndAfter("/", SpincastTestUtils.dummyRouteHandler);
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        IRoutingResult<IDefaultRequestContext> routingResult =
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult =
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("one").save(SpincastTestUtils.dummyRouteHandler);

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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one/").save(SpincastTestUtils.dummyRouteHandler);

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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("one/").save(SpincastTestUtils.dummyRouteHandler);

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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one/two").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult =
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/${param1}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        IRouteHandlerMatch<IDefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        IHandler<IDefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getParameters();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        String value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("one", value);
    }

    @Test
    public void paramEmptyName() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/${}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        IRouteHandlerMatch<IDefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        IHandler<IDefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getParameters();
        assertNotNull(parameters);
        assertEquals(0, parameters.size());
    }

    @Test
    public void twoParams() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/${param1}/${param2}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        IRouteHandlerMatch<IDefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        IHandler<IDefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getParameters();
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        try {
            router.GET("/${param1}/${param1}").save(SpincastTestUtils.dummyRouteHandler);
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void twoParamsSameNameEmpty() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        // This is ok, we don't collect them!
        router.GET("/${}/${}").save(SpincastTestUtils.dummyRouteHandler);
    }

    @Test
    public void lotsdOfParamNamesEmpty() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        // Yeah, why not?
        router.GET("/${}/${}/*{}/${}").save(SpincastTestUtils.dummyRouteHandler);
    }

    @Test
    public void paramEmptyNameNotCollected() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one/${param1}/${}/${}/${param2}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/one/two/three/four/five?test=1"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        IRouteHandlerMatch<IDefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        IHandler<IDefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getParameters();
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        try {
            router.GET("/${param1}/*{param1}").save(SpincastTestUtils.dummyRouteHandler);
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void filterBeforeAfter() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        final Map<String, String> handlerCalled = new HashMap<String, String>();

        router.GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext exchange) {
                handlerCalled.put("handlerCalled", "route");
            }
        });
        router.before("/", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext exchange) {
                handlerCalled.put("handlerCalled", "before");
            }
        });
        router.after("/", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext exchange) {
                handlerCalled.put("handlerCalled", "after");
            }
        });
        router.beforeAndAfter("/", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext exchange) {
                handlerCalled.put("handlerCalled", "beforeAndAfter");
            }
        });

        IRoutingResult<IDefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));

        List<IRouteHandlerMatch<IDefaultRequestContext>> routeAndFilters = routingResult.getRouteHandlerMatches();
        assertEquals(5, routeAndFilters.size());

        IRouteHandlerMatch<IDefaultRequestContext> handlerMatch = routeAndFilters.get(0);
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        // Two splat params not allowed...
        try {
            router.GET("/one/*{param1}/two/*{param1}").save(SpincastTestUtils.dummyRouteHandler);
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void splatSimpleNoName() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/*{}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/111/222/333/444/555"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        IRouteHandlerMatch<IDefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        IHandler<IDefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        // We don't collect params without a name...
        Map<String, String> parameters = routeMatch.getParameters();
        assertNotNull(parameters);
        assertEquals(0, parameters.size());
    }

    @Test
    public void splat1() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/*{param1}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/111/222/333/444/555"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        IRouteHandlerMatch<IDefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        IHandler<IDefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getParameters();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        String value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("111/222/333/444/555", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getParameters();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getParameters();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getParameters();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("", value);
    }

    @Test
    public void splat2() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("*{param1}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/111/222/333/444/555"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        IRouteHandlerMatch<IDefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        IHandler<IDefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getParameters();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        String value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("111/222/333/444/555", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getParameters();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getParameters();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost:8080"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getParameters();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("param1");
        assertNotNull(value);
        assertEquals("", value);
    }

    @Test
    public void splatAndRegularParams() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/${param1}/*{param2}/${param3}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/111/222/333/444/555"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        IRouteHandlerMatch<IDefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        IHandler<IDefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getParameters();
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/*{param1}/${param2}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/111/222/333/444/555"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        IRouteHandlerMatch<IDefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        IHandler<IDefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getParameters();
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/${param1}/*{param2}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/111/222/333/444/555"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        IRouteHandlerMatch<IDefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        IHandler<IDefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getParameters();
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/*{param1}/333/444/${param2}/666/${}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/111/222/333/444/555/666/777/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        IRouteHandlerMatch<IDefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        IHandler<IDefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getParameters();
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/${param1}/222/*{param2}/${param3}/777").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/111/222/333/444/555/666/777/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        IRouteHandlerMatch<IDefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        IHandler<IDefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getParameters();
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("111/${param1}/${param2}/*{param3}/").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/111/222/333/444/555/666/777/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        IRouteHandlerMatch<IDefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        IHandler<IDefaultRequestContext> route = routeMatch.getHandler();
        assertNotNull(route);
        assertEquals(0, routeMatch.getPosition());

        Map<String, String> parameters = routeMatch.getParameters();
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one/*{splat}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        IRouteHandlerMatch<IDefaultRequestContext> routeMatch = routingResult.getRouteHandlerMatches().get(0);

        Map<String, String> parameters = routeMatch.getParameters();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        String value = parameters.get("splat");
        assertNotNull(value);
        assertEquals("two", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getParameters();
        assertNotNull(parameters);
        assertEquals(1, parameters.size());

        value = parameters.get("splat");
        assertNotNull(value);
        assertEquals("", value);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
        routeMatch = routingResult.getRouteHandlerMatches().get(0);

        parameters = routeMatch.getParameters();
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one/two/three").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult =
                router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two"));
        assertNull(routingResult);

    }

    @Test
    public void tooManyTokens() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/one/two/three"));
        assertNull(routingResult);

    }

    @Test
    public void tooManyTokensButSplat() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one/*{any}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/one/two/three"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

    }

    @Test
    public void missingToken() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one/${param1}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/one"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

    }

    @Test
    public void emptyDynamicParam() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/${param1}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());
    }

    @Test
    public void emptyDynamicParam2() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one/${param1}").save(SpincastTestUtils.dummyRouteHandler);

        IRoutingResult<IDefaultRequestContext> routingResult = router.route(getRequestContextMock(HttpMethod.GET,
                                                                                                  "http://localhost/one/"));
        assertNull(routingResult);

        routingResult = router.route(getRequestContextMock(HttpMethod.GET, "http://localhost/one/two"));
        assertEquals(1, routingResult.getRouteHandlerMatches().size());

    }

}
