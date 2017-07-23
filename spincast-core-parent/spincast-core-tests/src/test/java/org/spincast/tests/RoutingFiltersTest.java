package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.RouteHandlerMatch;
import org.spincast.core.routing.Router;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;

public class RoutingFiltersTest extends NoAppStartHttpServerTestingBase {

    //==========================================
    // Remove all routes, even Spincast ones
    //==========================================
    @Override
    protected boolean removeSpincastRoutesToo() {
        return true;
    }

    protected String flag = "";
    protected Handler<DefaultRequestContext> mainHandler = new Handler<DefaultRequestContext>() {

        @Override
        public void handle(DefaultRequestContext context) {
            RoutingFiltersTest.this.flag += "M";
        }
    };
    protected Handler<DefaultRequestContext> beforeFilter = new Handler<DefaultRequestContext>() {

        @Override
        public void handle(DefaultRequestContext context) {
            RoutingFiltersTest.this.flag += "B";
        }
    };
    protected Handler<DefaultRequestContext> beforeFilter2 = new Handler<DefaultRequestContext>() {

        @Override
        public void handle(DefaultRequestContext context) {
            RoutingFiltersTest.this.flag += "B2";
        }
    };
    protected Handler<DefaultRequestContext> afterFilter = new Handler<DefaultRequestContext>() {

        @Override
        public void handle(DefaultRequestContext context) {
            RoutingFiltersTest.this.flag += "A";
        }
    };
    protected Handler<DefaultRequestContext> afterFilter2 = new Handler<DefaultRequestContext>() {

        @Override
        public void handle(DefaultRequestContext context) {
            RoutingFiltersTest.this.flag += "A2";
        }
    };
    protected Handler<DefaultRequestContext> beforeAndAfterFilter = new Handler<DefaultRequestContext>() {

        @Override
        public void handle(DefaultRequestContext context) {
            RoutingFiltersTest.this.flag += "BA";
        }
    };
    protected Handler<DefaultRequestContext> beforeAndAfterFilter2 = new Handler<DefaultRequestContext>() {

        @Override
        public void handle(DefaultRequestContext context) {
            RoutingFiltersTest.this.flag += "BA2";
        }
    };

    @After
    public void after() {
        this.flag = "";
    }

    @Test
    public void noFilter() throws Exception {

        getRouter().GET("/").save(this.mainHandler);

        HttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("M", this.flag);
    }

    @Test
    public void notFound() throws Exception {

        getRouter().GET("/").save(this.mainHandler);

        HttpResponse response = GET("/nope").send();
        assertEquals(404, response.getStatus());
        assertEquals("", this.flag);
    }

    @Test
    public void filterBeforeByRoute() throws Exception {

        getRouter().GET("/").before(this.beforeFilter).save(this.mainHandler);

        HttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterAfterByRoute() throws Exception {

        getRouter().GET("/").after(this.afterFilter).save(this.mainHandler);

        HttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterBeforeAfterByRoute() throws Exception {

        getRouter().GET("/").before(this.beforeFilter).after(this.afterFilter).save(this.mainHandler);

        HttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BMA", this.flag);
    }

    @Test
    public void nullHandler() throws Exception {

        // Not valid!

        try {
            getRouter().GET("/").save(null);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void nullBeforeFilter() throws Exception {
        try {
            getRouter().GET("/").before(null).save(this.mainHandler);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void nullAfterFilter() throws Exception {

        try {
            getRouter().GET("/").before(this.beforeFilter).after(null).save(this.mainHandler);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void filterBeforeGlobal1() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/").pos(-10).save(this.beforeFilter);
        router.GET("/").save(this.mainHandler);

        HttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobal1b() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/").save(this.mainHandler);
        router.ALL("/").pos(-10).save(this.beforeFilter);

        HttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobal1c() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/one").pos(-10).save(this.beforeFilter);
        router.GET("/one").save(this.mainHandler);
        router.GET("/two").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);

        this.flag = "";
        response = GET("/two").send();
        assertEquals(200, response.getStatus());
        assertEquals("M", this.flag);
    }

    @Test
    public void filterBeforeGlobal2() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(-10).save(this.beforeFilter);
        router.GET("/").save(this.mainHandler);

        HttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobal3() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(-10).save(this.beforeFilter);
        router.GET("/one").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobal4() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(-10).save(this.beforeFilter);
        router.GET("/one").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobal5() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(-10).save(this.beforeFilter);
        router.GET("/one/two/three").save(this.mainHandler);

        HttpResponse response = GET("/one/two/three").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobal6() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(-10).save(this.beforeFilter);
        router.GET("/one/*{param1}").save(this.mainHandler);

        HttpResponse response = GET("/one/two/three").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobal7() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/one/*{path}").pos(-10).save(this.beforeFilter);
        router.GET("/one").save(this.mainHandler);
        router.GET("/one/*{path}").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);

        this.flag = "";
        response = GET("/one/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);

        this.flag = "";
        response = GET("/one/two").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobalNotApplied1() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/").pos(-10).save(this.beforeFilter);
        router.GET("/one").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("M", this.flag);
    }

    @Test
    public void filterAfterGlobal1() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/").pos(10).save(this.afterFilter);
        router.GET("/").save(this.mainHandler);

        HttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobal1b() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/").save(this.mainHandler);
        router.ALL("/").pos(10).save(this.afterFilter);

        HttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobal1c() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/one").pos(10).save(this.afterFilter);
        router.GET("/one").save(this.mainHandler);
        router.GET("/two").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);

        this.flag = "";
        response = GET("/two").send();
        assertEquals(200, response.getStatus());
        assertEquals("M", this.flag);
    }

    @Test
    public void filterAfterGlobal2() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(10).save(this.afterFilter);
        router.GET("/").save(this.mainHandler);

        HttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobal3() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(10).save(this.afterFilter);
        router.GET("/one").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobal4() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(10).save(this.afterFilter);
        router.GET("/one").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobal5() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(10).save(this.afterFilter);
        router.GET("/one/two/three").save(this.mainHandler);

        HttpResponse response = GET("/one/two/three").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobal6() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(10).save(this.afterFilter);
        router.GET("/one/*{param1}").save(this.mainHandler);

        HttpResponse response = GET("/one/two/three").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobal7() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/one/*{path}").pos(10).save(this.afterFilter);
        router.GET("/one").save(this.mainHandler);
        router.GET("/one/*{path}").save(this.mainHandler);
        router.GET("/one/*{param}").save(this.mainHandler);
        router.GET("/one/${param}").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);

        this.flag = "";
        response = GET("/one/").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);

        this.flag = "";
        response = GET("/one/two").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobalNotApplied1() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/").pos(10).save(this.afterFilter);
        router.GET("/one").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("M", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal1() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/").pos(-10).save(this.beforeAndAfterFilter);
        router.ALL("/").pos(10).save(this.beforeAndAfterFilter);
        router.GET("/").save(this.mainHandler);

        HttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag); // Balabalabala la bamba!
    }

    @Test
    public void filterBeforeAfterGlobal1b() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/").save(this.mainHandler);
        router.ALL("/").pos(-10).save(this.beforeAndAfterFilter);
        router.ALL("/").pos(10).save(this.beforeAndAfterFilter);

        HttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal1c() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/one").pos(-10).save(this.beforeAndAfterFilter);
        router.ALL("/one").pos(10).save(this.beforeAndAfterFilter);
        router.GET("/one").save(this.mainHandler);
        router.GET("/two").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);

        this.flag = "";
        response = GET("/two").send();
        assertEquals(200, response.getStatus());
        assertEquals("M", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal2() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(-10).save(this.beforeAndAfterFilter);
        router.ALL("/*{path}").pos(10).save(this.beforeAndAfterFilter);
        router.GET("/").save(this.mainHandler);

        HttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal3() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(-10).save(this.beforeAndAfterFilter);
        router.ALL("/*{path}").pos(10).save(this.beforeAndAfterFilter);
        router.GET("/one").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal4() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(-10).save(this.beforeAndAfterFilter);
        router.ALL("/*{path}").pos(10).save(this.beforeAndAfterFilter);
        router.GET("/one").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal5() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(-10).save(this.beforeAndAfterFilter);
        router.ALL("/*{path}").pos(10).save(this.beforeAndAfterFilter);
        router.GET("/one/two/three").save(this.mainHandler);

        HttpResponse response = GET("/one/two/three").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal6() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/*{path}").pos(-10).save(this.beforeAndAfterFilter);
        router.ALL("/*{path}").pos(10).save(this.beforeAndAfterFilter);
        router.GET("/one/*{param1}").save(this.mainHandler);

        HttpResponse response = GET("/one/two/three").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal7() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/one/*{path}").pos(-10).save(this.beforeAndAfterFilter);
        router.ALL("/one/*{path}").pos(10).save(this.beforeAndAfterFilter);
        router.GET("/one").save(this.mainHandler);
        router.GET("/one/*{path}").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);

        this.flag = "";
        response = GET("/one/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);

        this.flag = "";
        response = GET("/one/two").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobalNotApplied1() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.ALL("/").pos(-10).save(this.beforeAndAfterFilter);
        router.ALL("/").pos(10).save(this.beforeAndAfterFilter);
        router.GET("/one").save(this.mainHandler);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("M", this.flag);
    }

    @Test
    public void filterOrder() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(this.mainHandler);

        router.ALL("/one").pos(-10).save(this.beforeFilter);
        router.ALL("/one").pos(-10).save(this.beforeFilter2);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BB2M", this.flag);
    }

    @Test
    public void filterOrder2() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(this.mainHandler);

        router.ALL("/one").pos(-10).save(this.beforeFilter2);
        router.ALL("/one").pos(-10).save(this.beforeFilter);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("B2BM", this.flag);
    }

    @Test
    public void filterOrder3() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(this.mainHandler);

        router.ALL("/one").pos(10).save(this.afterFilter);
        router.ALL("/one").pos(10).save(this.afterFilter2);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("MAA2", this.flag);
    }

    @Test
    public void filterOrder4() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(this.mainHandler);

        router.ALL("/one").pos(10).save(this.afterFilter2);
        router.ALL("/one").pos(10).save(this.afterFilter);

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA2A", this.flag);
    }

    @Test
    public void filterWithParam() throws Exception {

        // The filter will be called with its own DefaultRequestContext,
        // containing its parameters.
        getRouter().ALL("/${paramFilter1}/*{anything}").pos(-10).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String paramFilter1 = context.request().getPathParam("paramFilter1");
                assertNotNull(paramFilter1);
                assertEquals("one", paramFilter1);

                String paramSplat = context.request().getPathParam("anything");
                assertNotNull(paramSplat);
                assertEquals("val1/three/four", paramSplat);

                String param1 = context.request().getPathParam("param1");
                assertNull(param1);

                context.response().sendPlainText(param1);
            }
        });

        getRouter().GET("/one/${param1}/three/four")
                   .before(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {

                           String param1 = context.request().getPathParam("param1");
                           assertNotNull(param1);
                           assertEquals("val1", param1);

                           String paramSplat = context.request().getPathParam("anything");
                           assertNull(paramSplat);

                           String paramFilter1 = context.request().getPathParam("paramFilter1");
                           assertNull(paramFilter1);

                           context.response().sendPlainText(param1);
                       }
                   })
                   .after(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {

                           String param1 = context.request().getPathParam("param1");
                           assertNotNull(param1);
                           assertEquals("val1", param1);

                           String paramSplat = context.request().getPathParam("anything");
                           assertNull(paramSplat);

                           String paramFilter1 = context.request().getPathParam("paramFilter1");
                           assertNull(paramFilter1);

                           context.response().sendPlainText(param1);
                       }
                   })
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {

                           String param1 = context.request().getPathParam("param1");
                           assertNotNull(param1);
                           assertEquals("val1", param1);

                           String paramSplat = context.request().getPathParam("anything");
                           assertNull(paramSplat);

                           String paramFilter1 = context.request().getPathParam("paramFilter1");
                           assertNull(paramFilter1);

                           context.response().sendPlainText(param1);
                       }
                   });

        HttpResponse response = GET("/one/val1/three/four").send();
        assertEquals(200, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("val1val1val1", response.getContentAsString());
    }

    @Test
    public void validateRouteHandlers() throws Exception {

        getRouter().ALL("/*{before}").pos(-10).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertTrue(context.routing().getPosition() < 0);

                List<RouteHandlerMatch<DefaultRequestContext>> routeHandlerMatches =
                        context.routing().getRoutingResult().getRouteHandlerMatches();
                assertNotNull(routeHandlerMatches);
                assertEquals(7, routeHandlerMatches.size());

                RouteHandlerMatch<DefaultRequestContext> currentRouteHandlerMatch =
                        context.routing().getCurrentRouteHandlerMatch();
                assertEquals("/*{before}", currentRouteHandlerMatch.getSourceRoute().getPath());

                assertEquals("one", context.request().getPathParam("before"));

                context.response().sendPlainText("BF");
            }
        });

        getRouter().ALL("/*{after}").pos(10).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertTrue(context.routing().getPosition() > 0);

                List<RouteHandlerMatch<DefaultRequestContext>> routeHandlerMatches =
                        context.routing().getRoutingResult().getRouteHandlerMatches();
                assertNotNull(routeHandlerMatches);
                assertEquals(7, routeHandlerMatches.size());

                RouteHandlerMatch<DefaultRequestContext> currentRouteHandlerMatch =
                        context.routing().getCurrentRouteHandlerMatch();
                assertEquals("/*{after}", currentRouteHandlerMatch.getSourceRoute().getPath());

                assertEquals("one", context.request().getPathParam("after"));

                context.response().sendPlainText("AF");
            }
        });

        Handler<DefaultRequestContext> handler = new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertTrue(context.routing().getPosition() != 0);

                List<RouteHandlerMatch<DefaultRequestContext>> routeHandlerMatches =
                        context.routing().getRoutingResult().getRouteHandlerMatches();
                assertNotNull(routeHandlerMatches);
                assertEquals(7, routeHandlerMatches.size());

                RouteHandlerMatch<DefaultRequestContext> currentRouteHandlerMatch =
                        context.routing().getCurrentRouteHandlerMatch();
                assertEquals("/*{beforeAfter}", currentRouteHandlerMatch.getSourceRoute().getPath());

                assertEquals("one", context.request().getPathParam("beforeAfter"));

                if (context.routing().getPosition() < 0) {
                    context.response().sendPlainText("BF2");
                } else if (context.routing().getPosition() > 0) {
                    context.response().sendPlainText("AF2");
                }
            }
        };

        getRouter().ALL("/*{beforeAfter}").pos(-10).save(handler);
        getRouter().ALL("/*{beforeAfter}").pos(10).save(handler);

        getRouter().GET("/${param1}")
                   .before(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {

                           assertTrue(context.routing().getPosition() < 0);

                           List<RouteHandlerMatch<DefaultRequestContext>> routeHandlerMatches =
                                   context.routing().getRoutingResult().getRouteHandlerMatches();
                           assertNotNull(routeHandlerMatches);
                           assertEquals(7, routeHandlerMatches.size());

                           RouteHandlerMatch<DefaultRequestContext> currentRouteHandlerMatch =
                                   context.routing().getCurrentRouteHandlerMatch();
                           assertEquals("/${param1}", currentRouteHandlerMatch.getSourceRoute().getPath());

                           assertEquals("one", context.request().getPathParam("param1"));

                           context.response().sendPlainText("RBF");
                       }
                   })
                   .after(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {

                           assertTrue(context.routing().getPosition() > 0);

                           List<RouteHandlerMatch<DefaultRequestContext>> routeHandlerMatches =
                                   context.routing().getRoutingResult().getRouteHandlerMatches();
                           assertNotNull(routeHandlerMatches);
                           assertEquals(7, routeHandlerMatches.size());

                           RouteHandlerMatch<DefaultRequestContext> currentRouteHandlerMatch =
                                   context.routing().getCurrentRouteHandlerMatch();
                           assertEquals("/${param1}", currentRouteHandlerMatch.getSourceRoute().getPath());

                           assertEquals("one", context.request().getPathParam("param1"));

                           context.response().sendPlainText("RAF");
                       }
                   })
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {

                           assertTrue(context.routing().getPosition() == 0);

                           List<RouteHandlerMatch<DefaultRequestContext>> routeHandlerMatches =
                                   context.routing().getRoutingResult().getRouteHandlerMatches();
                           assertNotNull(routeHandlerMatches);
                           assertEquals(7, routeHandlerMatches.size());

                           RouteHandlerMatch<DefaultRequestContext> currentRouteHandlerMatch =
                                   context.routing().getCurrentRouteHandlerMatch();
                           assertEquals("/${param1}", currentRouteHandlerMatch.getSourceRoute().getPath());

                           assertEquals("one", context.request().getPathParam("param1"));

                           context.response().sendPlainText("M");
                       }
                   });

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("BFBF2RBFMRAFAFAF2", response.getContentAsString());
    }

    @Test
    public void filtersWithInlineFilters() throws Exception {

        Router<DefaultRequestContext, DefaultWebsocketContext> router = getRouter();

        router.GET("/one").pos(-1)
              .before(new Handler<DefaultRequestContext>() {

                  @Override
                  public void handle(DefaultRequestContext context) {
                      context.response().sendPlainText("A");
                  }
              })
              .after(new Handler<DefaultRequestContext>() {

                  @Override
                  public void handle(DefaultRequestContext context) {
                      context.response().sendPlainText("C");
                  }
              })
              .save(new Handler<DefaultRequestContext>() {

                  @Override
                  public void handle(DefaultRequestContext context) {
                      context.response().sendPlainText("B");
                  }
              });

        router.GET("/one").pos(1)
              .before(new Handler<DefaultRequestContext>() {

                  @Override
                  public void handle(DefaultRequestContext context) {
                      context.response().sendPlainText("D");
                  }
              })
              .after(new Handler<DefaultRequestContext>() {

                  @Override
                  public void handle(DefaultRequestContext context) {
                      context.response().sendPlainText("F");
                  }
              })
              .save(new Handler<DefaultRequestContext>() {

                  @Override
                  public void handle(DefaultRequestContext context) {
                      context.response().sendPlainText("E");
                  }
              });

        router.GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("main");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("ABCmainDEF", response.getContentAsString());
    }

    @Test
    public void deleteFilter() throws Exception {

        getRouter().ALL("/*{path}").id("myFilter").pos(-10).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("before");
            }
        });

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("main");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("beforemain", response.getContentAsString());

        getRouter().removeRoute("myFilter");

        response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("main", response.getContentAsString());
    }

    @Test
    public void skipFilter() throws Exception {

        getRouter().ALL("/*{path}").id("myBeforeFilter").pos(-10).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("before");
            }
        });

        getRouter().ALL("/*{path}").id("myAfterFilter").pos(10).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("after");
            }
        });

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("one");
            }
        });

        getRouter().GET("/two").skip("myBeforeFilter").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().GET("/three").skip("myAfterFilter").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("three");
            }
        });

        getRouter().GET("/four").skip("myBeforeFilter").skip("myAfterFilter").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("four");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("beforeoneafter", response.getContentAsString());

        response = GET("/two").send();
        assertEquals(200, response.getStatus());
        assertEquals("twoafter", response.getContentAsString());

        response = GET("/three").send();
        assertEquals(200, response.getStatus());
        assertEquals("beforethree", response.getContentAsString());

        response = GET("/four").send();
        assertEquals(200, response.getStatus());
        assertEquals("four", response.getContentAsString());
    }

    @Test
    public void skipFiltersKeywords() throws Exception {

        getRouter().ALL().pos(-10).id("myBeforeFilter").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("before");
            }
        });

        getRouter().ALL().pos(10).id("myAfterFilter").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("after");
            }
        });

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("one");
            }
        });

        getRouter().GET("/two").skip("myBeforeFilter").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        getRouter().GET("/three").skip("myAfterFilter").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("three");
            }
        });

        getRouter().GET("/four").skip("myBeforeFilter").skip("myAfterFilter").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("four");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("beforeoneafter", response.getContentAsString());

        response = GET("/two").send();
        assertEquals(200, response.getStatus());
        assertEquals("twoafter", response.getContentAsString());

        response = GET("/three").send();
        assertEquals(200, response.getStatus());
        assertEquals("beforethree", response.getContentAsString());

        response = GET("/four").send();
        assertEquals(200, response.getStatus());
        assertEquals("four", response.getContentAsString());
    }

}
