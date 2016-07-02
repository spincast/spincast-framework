package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRouteHandlerMatch;
import org.spincast.core.routing.IRouter;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;

public class RoutingFiltersTest extends SpincastDefaultNoAppIntegrationTestBase {

    protected String flag = "";
    protected IHandler<IDefaultRequestContext> mainHandler = new IHandler<IDefaultRequestContext>() {

        @Override
        public void handle(IDefaultRequestContext context) {
            RoutingFiltersTest.this.flag += "M";
        }
    };
    protected IHandler<IDefaultRequestContext> beforeFilter = new IHandler<IDefaultRequestContext>() {

        @Override
        public void handle(IDefaultRequestContext context) {
            RoutingFiltersTest.this.flag += "B";
        }
    };
    protected IHandler<IDefaultRequestContext> beforeFilter2 = new IHandler<IDefaultRequestContext>() {

        @Override
        public void handle(IDefaultRequestContext context) {
            RoutingFiltersTest.this.flag += "B2";
        }
    };
    protected IHandler<IDefaultRequestContext> afterFilter = new IHandler<IDefaultRequestContext>() {

        @Override
        public void handle(IDefaultRequestContext context) {
            RoutingFiltersTest.this.flag += "A";
        }
    };
    protected IHandler<IDefaultRequestContext> afterFilter2 = new IHandler<IDefaultRequestContext>() {

        @Override
        public void handle(IDefaultRequestContext context) {
            RoutingFiltersTest.this.flag += "A2";
        }
    };
    protected IHandler<IDefaultRequestContext> beforeAndAfterFilter = new IHandler<IDefaultRequestContext>() {

        @Override
        public void handle(IDefaultRequestContext context) {
            RoutingFiltersTest.this.flag += "BA";
        }
    };
    protected IHandler<IDefaultRequestContext> beforeAndAfterFilter2 = new IHandler<IDefaultRequestContext>() {

        @Override
        public void handle(IDefaultRequestContext context) {
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

        IHttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("M", this.flag);
    }

    @Test
    public void notFound() throws Exception {

        getRouter().GET("/").save(this.mainHandler);

        IHttpResponse response = GET("/nope").send();
        assertEquals(404, response.getStatus());
        assertEquals("", this.flag);
    }

    @Test
    public void filterBeforeByRoute() throws Exception {

        getRouter().GET("/").before(this.beforeFilter).save(this.mainHandler);

        IHttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterAfterByRoute() throws Exception {

        getRouter().GET("/").after(this.afterFilter).save(this.mainHandler);

        IHttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterBeforeAfterByRoute() throws Exception {

        getRouter().GET("/").before(this.beforeFilter).after(this.afterFilter).save(this.mainHandler);

        IHttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BMA", this.flag);
    }

    @Test
    public void nullHandler() throws Exception {

        // Not valid!

        try {
            getRouter().GET("/").save(null);
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void nullBeforeFilter() throws Exception {
        try {
            getRouter().GET("/").before(null).save(this.mainHandler);
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void nullAfterFilter() throws Exception {

        try {
            getRouter().GET("/").before(this.beforeFilter).after(null).save(this.mainHandler);
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void filterBeforeGlobal1() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.before("/", this.beforeFilter);
        router.GET("/").save(this.mainHandler);

        IHttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobal1b() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/").save(this.mainHandler);
        router.before("/", this.beforeFilter);

        IHttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobal1c() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.before("/one", this.beforeFilter);
        router.GET("/one").save(this.mainHandler);
        router.GET("/two").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);

        this.flag = "";
        response = GET("/two").send();
        assertEquals(200, response.getStatus());
        assertEquals("M", this.flag);
    }

    @Test
    public void filterBeforeGlobal2() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.before("/*{path}", this.beforeFilter);
        router.GET("/").save(this.mainHandler);

        IHttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobal3() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.before("/*{path}", this.beforeFilter);
        router.GET("/one").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobal4() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.before("/*{path}", this.beforeFilter);
        router.GET("/one").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobal5() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.before("/*{path}", this.beforeFilter);
        router.GET("/one/two/three").save(this.mainHandler);

        IHttpResponse response = GET("/one/two/three").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobal6() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.before("/*{path}", this.beforeFilter);
        router.GET("/one/*{param1}").save(this.mainHandler);

        IHttpResponse response = GET("/one/two/three").send();
        assertEquals(200, response.getStatus());
        assertEquals("BM", this.flag);
    }

    @Test
    public void filterBeforeGlobal7() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.before("/one/*{path}", this.beforeFilter);
        router.GET("/one").save(this.mainHandler);
        router.GET("/one/*{path}").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.before("/", this.beforeFilter);
        router.GET("/one").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("M", this.flag);
    }

    @Test
    public void filterAfterGlobal1() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.after("/", this.afterFilter);
        router.GET("/").save(this.mainHandler);

        IHttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobal1b() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/").save(this.mainHandler);
        router.after("/", this.afterFilter);

        IHttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobal1c() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.after("/one", this.afterFilter);
        router.GET("/one").save(this.mainHandler);
        router.GET("/two").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);

        this.flag = "";
        response = GET("/two").send();
        assertEquals(200, response.getStatus());
        assertEquals("M", this.flag);
    }

    @Test
    public void filterAfterGlobal2() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.after("/*{path}", this.afterFilter);
        router.GET("/").save(this.mainHandler);

        IHttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobal3() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.after("/*{path}", this.afterFilter);
        router.GET("/one").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobal4() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.after("/*{path}", this.afterFilter);
        router.GET("/one").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobal5() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.after("/*{path}", this.afterFilter);
        router.GET("/one/two/three").save(this.mainHandler);

        IHttpResponse response = GET("/one/two/three").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobal6() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.after("/*{path}", this.afterFilter);
        router.GET("/one/*{param1}").save(this.mainHandler);

        IHttpResponse response = GET("/one/two/three").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA", this.flag);
    }

    @Test
    public void filterAfterGlobal7() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.after("/one/*{path}", this.afterFilter);
        router.GET("/one").save(this.mainHandler);
        router.GET("/one/*{path}").save(this.mainHandler);
        router.GET("/one/*{param}").save(this.mainHandler);
        router.GET("/one/${param}").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.after("/", this.afterFilter);
        router.GET("/one").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("M", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal1() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.beforeAndAfter("/", this.beforeAndAfterFilter);
        router.GET("/").save(this.mainHandler);

        IHttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag); // Balabalabala la bamba!
    }

    @Test
    public void filterBeforeAfterGlobal1b() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/").save(this.mainHandler);
        router.beforeAndAfter("/", this.beforeAndAfterFilter);

        IHttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal1c() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.beforeAndAfter("/one", this.beforeAndAfterFilter);
        router.GET("/one").save(this.mainHandler);
        router.GET("/two").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);

        this.flag = "";
        response = GET("/two").send();
        assertEquals(200, response.getStatus());
        assertEquals("M", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal2() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.beforeAndAfter("/*{path}", this.beforeAndAfterFilter);
        router.GET("/").save(this.mainHandler);

        IHttpResponse response = GET("/").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal3() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.beforeAndAfter("/*{path}", this.beforeAndAfterFilter);
        router.GET("/one").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal4() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.beforeAndAfter("/*{path}", this.beforeAndAfterFilter);
        router.GET("/one").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal5() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.beforeAndAfter("/*{path}", this.beforeAndAfterFilter);
        router.GET("/one/two/three").save(this.mainHandler);

        IHttpResponse response = GET("/one/two/three").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal6() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.beforeAndAfter("/*{path}", this.beforeAndAfterFilter);
        router.GET("/one/*{param1}").save(this.mainHandler);

        IHttpResponse response = GET("/one/two/three").send();
        assertEquals(200, response.getStatus());
        assertEquals("BAMBA", this.flag);
    }

    @Test
    public void filterBeforeAfterGlobal7() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.beforeAndAfter("/one/*{path}", this.beforeAndAfterFilter);
        router.GET("/one").save(this.mainHandler);
        router.GET("/one/*{path}").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
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

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.beforeAndAfter("/", this.beforeAndAfterFilter);
        router.GET("/one").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("M", this.flag);
    }

    @Test
    public void filterOrder() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(this.mainHandler);

        router.before("/one", this.beforeFilter);
        router.before("/one", this.beforeFilter2);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BB2M", this.flag);
    }

    @Test
    public void filterOrder2() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(this.mainHandler);

        router.before("/one", this.beforeFilter2);
        router.before("/one", this.beforeFilter);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("B2BM", this.flag);
    }

    @Test
    public void filterOrder3() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(this.mainHandler);

        router.after("/one", this.afterFilter);
        router.after("/one", this.afterFilter2);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("MAA2", this.flag);
    }

    @Test
    public void filterOrder4() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(this.mainHandler);

        router.after("/one", this.afterFilter2);
        router.after("/one", this.afterFilter);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("MA2A", this.flag);
    }

    @Test
    public void filterOrder5() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(this.mainHandler);

        router.beforeAndAfter("/one", this.beforeAndAfterFilter);
        router.beforeAndAfter("/one", this.beforeAndAfterFilter2);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BABA2MBABA2", this.flag);
    }

    @Test
    public void filterOrder6() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(this.mainHandler);

        router.beforeAndAfter("/one", this.beforeAndAfterFilter2);
        router.beforeAndAfter("/one", this.beforeAndAfterFilter);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BA2BAMBA2BA", this.flag);
    }

    @Test
    public void multipleFilter1() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one").save(this.mainHandler);

        router.before("/one", this.beforeFilter);
        router.before("/one", this.beforeFilter2);
        router.after("/one", this.afterFilter);
        router.after("/one", this.afterFilter2);
        router.beforeAndAfter("/one", this.beforeAndAfterFilter);
        router.beforeAndAfter("/one", this.beforeAndAfterFilter2);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BB2BABA2MAA2BABA2", this.flag);

    }

    @Test
    public void multipleFilter2() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.after("/one", this.afterFilter2);
        router.before("/one", this.beforeFilter);
        router.beforeAndAfter("/one", this.beforeAndAfterFilter2);
        router.after("/one", this.afterFilter);
        router.before("/one", this.beforeFilter2);
        router.beforeAndAfter("/one", this.beforeAndAfterFilter);

        router.GET("/one").save(this.mainHandler);

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("BBA2B2BAMA2BA2ABA", this.flag);
    }

    @Test
    public void filterWithParam() throws Exception {

        // The filter will be called with its own IDefaultRequestContext,
        // containing its parameters.
        getRouter().before("/${paramFilter1}/*{anything}", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

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
                   .before(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {

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
                   .after(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {

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
                   .save(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {

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

        IHttpResponse response = GET("/one/val1/three/four").send();
        assertEquals(200, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("val1val1val1", response.getContentAsString());
    }

    @Test
    public void validateRouteHandlers() throws Exception {

        getRouter().before("/*{before}", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertTrue(context.routing().getPosition() < 0);

                List<IRouteHandlerMatch<IDefaultRequestContext>> routeHandlerMatches =
                        context.routing().getRoutingResult().getRouteHandlerMatches();
                assertNotNull(routeHandlerMatches);
                assertEquals(7, routeHandlerMatches.size());

                IRouteHandlerMatch<IDefaultRequestContext> currentRouteHandlerMatch =
                        context.routing().getCurrentRouteHandlerMatch();
                assertEquals("/*{before}", currentRouteHandlerMatch.getSourceRoute().getPath());

                assertEquals("one", context.request().getPathParam("before"));

                context.response().sendPlainText("BF");
            }
        });

        getRouter().after("/*{after}", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertTrue(context.routing().getPosition() > 0);

                List<IRouteHandlerMatch<IDefaultRequestContext>> routeHandlerMatches =
                        context.routing().getRoutingResult().getRouteHandlerMatches();
                assertNotNull(routeHandlerMatches);
                assertEquals(7, routeHandlerMatches.size());

                IRouteHandlerMatch<IDefaultRequestContext> currentRouteHandlerMatch =
                        context.routing().getCurrentRouteHandlerMatch();
                assertEquals("/*{after}", currentRouteHandlerMatch.getSourceRoute().getPath());

                assertEquals("one", context.request().getPathParam("after"));

                context.response().sendPlainText("AF");
            }
        });

        getRouter().beforeAndAfter("/*{beforeAfter}", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertTrue(context.routing().getPosition() != 0);

                List<IRouteHandlerMatch<IDefaultRequestContext>> routeHandlerMatches =
                        context.routing().getRoutingResult().getRouteHandlerMatches();
                assertNotNull(routeHandlerMatches);
                assertEquals(7, routeHandlerMatches.size());

                IRouteHandlerMatch<IDefaultRequestContext> currentRouteHandlerMatch =
                        context.routing().getCurrentRouteHandlerMatch();
                assertEquals("/*{beforeAfter}", currentRouteHandlerMatch.getSourceRoute().getPath());

                assertEquals("one", context.request().getPathParam("beforeAfter"));

                if(context.routing().getPosition() < 0) {
                    context.response().sendPlainText("BF2");
                } else if(context.routing().getPosition() > 0) {
                    context.response().sendPlainText("AF2");
                }
            }
        });

        getRouter().GET("/${param1}")
                   .before(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {

                           assertTrue(context.routing().getPosition() < 0);

                           List<IRouteHandlerMatch<IDefaultRequestContext>> routeHandlerMatches =
                                   context.routing().getRoutingResult().getRouteHandlerMatches();
                           assertNotNull(routeHandlerMatches);
                           assertEquals(7, routeHandlerMatches.size());

                           IRouteHandlerMatch<IDefaultRequestContext> currentRouteHandlerMatch =
                                   context.routing().getCurrentRouteHandlerMatch();
                           assertEquals("/${param1}", currentRouteHandlerMatch.getSourceRoute().getPath());

                           assertEquals("one", context.request().getPathParam("param1"));

                           context.response().sendPlainText("RBF");
                       }
                   })
                   .after(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {

                           assertTrue(context.routing().getPosition() > 0);

                           List<IRouteHandlerMatch<IDefaultRequestContext>> routeHandlerMatches =
                                   context.routing().getRoutingResult().getRouteHandlerMatches();
                           assertNotNull(routeHandlerMatches);
                           assertEquals(7, routeHandlerMatches.size());

                           IRouteHandlerMatch<IDefaultRequestContext> currentRouteHandlerMatch =
                                   context.routing().getCurrentRouteHandlerMatch();
                           assertEquals("/${param1}", currentRouteHandlerMatch.getSourceRoute().getPath());

                           assertEquals("one", context.request().getPathParam("param1"));

                           context.response().sendPlainText("RAF");
                       }
                   })
                   .save(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {

                           assertTrue(context.routing().getPosition() == 0);

                           List<IRouteHandlerMatch<IDefaultRequestContext>> routeHandlerMatches =
                                   context.routing().getRoutingResult().getRouteHandlerMatches();
                           assertNotNull(routeHandlerMatches);
                           assertEquals(7, routeHandlerMatches.size());

                           IRouteHandlerMatch<IDefaultRequestContext> currentRouteHandlerMatch =
                                   context.routing().getCurrentRouteHandlerMatch();
                           assertEquals("/${param1}", currentRouteHandlerMatch.getSourceRoute().getPath());

                           assertEquals("one", context.request().getPathParam("param1"));

                           context.response().sendPlainText("M");
                       }
                   });

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("BFBF2RBFMRAFAFAF2", response.getContentAsString());
    }

    @Test
    public void filtersWithInlineFilters() throws Exception {

        IRouter<IDefaultRequestContext, IDefaultWebsocketContext> router = getRouter();

        router.GET("/one").pos(-1)
              .before(new IHandler<IDefaultRequestContext>() {

                  @Override
                  public void handle(IDefaultRequestContext context) {
                      context.response().sendPlainText("A");
                  }
              })
              .after(new IHandler<IDefaultRequestContext>() {

                  @Override
                  public void handle(IDefaultRequestContext context) {
                      context.response().sendPlainText("C");
                  }
              })
              .save(new IHandler<IDefaultRequestContext>() {

                  @Override
                  public void handle(IDefaultRequestContext context) {
                      context.response().sendPlainText("B");
                  }
              });

        router.GET("/one").pos(1)
              .before(new IHandler<IDefaultRequestContext>() {

                  @Override
                  public void handle(IDefaultRequestContext context) {
                      context.response().sendPlainText("D");
                  }
              })
              .after(new IHandler<IDefaultRequestContext>() {

                  @Override
                  public void handle(IDefaultRequestContext context) {
                      context.response().sendPlainText("F");
                  }
              })
              .save(new IHandler<IDefaultRequestContext>() {

                  @Override
                  public void handle(IDefaultRequestContext context) {
                      context.response().sendPlainText("E");
                  }
              });

        router.GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("main");
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(200, response.getStatus());
        assertEquals("ABCmainDEF", response.getContentAsString());
    }

}
