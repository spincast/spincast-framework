package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.exceptions.ForwardRouteException;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Handler;
import org.spincast.core.server.Server;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;

public class ForwardRouteExceptionTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void defaultReset() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("one");
                throw new ForwardRouteException("/two");
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void reset() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("one");
                throw new ForwardRouteException("/two", true);
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void noReset() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("one");
                throw new ForwardRouteException("/two", false);
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("onetwo", response.getContentAsString());
    }

    @Test
    public void forwardTwoTimesPlusPathParam() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("one");
                throw new ForwardRouteException("/two", true);
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
                throw new ForwardRouteException("/three/four", false);
            }
        });

        getRouter().GET("/three/${param}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String param = context.request().getPathParam("param");
                assertNotNull(param);
                context.response().sendPlainText("three" + param);
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("twothreefour", response.getContentAsString());
    }

    @Test
    public void forwardTooManyTimes() throws Exception {

        int routeForwardingMaxNumber = getSpincastConfig().getRouteForwardingMaxNumber();
        assertTrue(routeForwardingMaxNumber < 3);

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two");
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/three");
            }
        });

        getRouter().GET("/three").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/four");
            }
        });

        getRouter().GET("/four").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("four");
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test
    public void forwardMustChangeSomeRequestInfo() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two?q=two", true);
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String expected = createTestUrl("/two?q=two");
                String fullUrl = context.request().getFullUrl();
                assertEquals(expected, fullUrl);

                String path = context.request().getRequestPath();
                assertEquals("/two", path);

                String queryString = context.request().getQueryString(false);
                assertEquals("q=two", queryString);
            }
        });

        HttpResponse response = GET("/one?q=one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

    }

    @Test
    public void forwardMustKeepSomeRequestInfo() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two?q=two", true);
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                // Keep the headers
                String header = context.request().getHeaderFirst(HttpHeaders.ACCEPT);
                assertNotNull(header);
                assertEquals(ContentTypeDefaults.JSON.getMainVariation(), header);

                // Keep the cookies
                String cookie = context.request().getCookieValue("name1");
                assertNotNull(cookie);
                assertEquals("toto", cookie);

                // The original url
                String expected = createTestUrl("/one?q=one");
                String originalFullUrl = context.request().getFullUrlOriginal();
                assertEquals(expected, originalFullUrl);

                // The server getFullUrl() also still returns the original
                // URL.
                Server server = context.guice().getInstance(Server.class);
                String originalUrl = server.getFullUrlOriginal(context.exchange());
                assertEquals(expected, originalUrl);
            }
        });

        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put(HttpHeaders.ACCEPT, Lists.newArrayList(ContentTypeDefaults.JSON.getMainVariation()));

        Cookie cookie = getCookieFactory().createCookie("name1", "toto");
        cookie.setDomain(getSpincastConfig().getServerHost());
        cookie.setPath("/");
        cookie.setSecure(false);

        HttpResponse response = GET("/one?q=one").setCookie(cookie).setHeaders(headers).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void forwardMustKeepSomeRequestPostInfo() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two?q=two", true);
            }
        });

        getRouter().POST("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                // Keep the body
                JsonObject jsonObj = context.request().getJsonBody();
                assertNotNull(jsonObj);
                assertNotNull(jsonObj.getString("name"));
                assertEquals("toto", jsonObj.getString("name"));

                // Keeo the Content-Type
                assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(),
                             context.request().getHeaderFirst(HttpHeaders.CONTENT_TYPE));

            }
        });

        HttpResponse response = POST("/one?q=one").setStringBody("{\"name\":\"toto\"}",
                                                                   ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                                                  .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

    }

    @Test
    public void forwardNotFound() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two?q=two", true);
            }
        });

        getRouter().notFound(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void forwardMustRespectHttpMethod() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two?q=two", true);
            }
        });

        getRouter().POST("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

    }

    @Test
    public void isForwarded() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                assertFalse(context.routing().isForwarded());
                throw new ForwardRouteException("/two?q=two", true);
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                assertTrue(context.routing().isForwarded());
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

    }

}
