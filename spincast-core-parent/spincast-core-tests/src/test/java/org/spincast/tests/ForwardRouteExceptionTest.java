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
import org.spincast.core.cookies.ICookie;
import org.spincast.core.exceptions.ForwardRouteException;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.IHandler;
import org.spincast.core.server.IServer;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;

public class ForwardRouteExceptionTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Test
    public void defaultReset() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("one");
                throw new ForwardRouteException("/two");
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void reset() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("one");
                throw new ForwardRouteException("/two", true);
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void noReset() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("one");
                throw new ForwardRouteException("/two", false);
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("onetwo", response.getContentAsString());
    }

    @Test
    public void forwardTwoTimesPlusPathParam() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("one");
                throw new ForwardRouteException("/two", true);
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("two");
                throw new ForwardRouteException("/three/four", false);
            }
        });

        getRouter().GET("/three/${param}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String param = context.request().getPathParam("param");
                assertNotNull(param);
                context.response().sendPlainText("three" + param);
            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("twothreefour", response.getContentAsString());
    }

    @Test
    public void forwardTooManyTimes() throws Exception {

        int routeForwardingMaxNumber = getSpincastConfig().getRouteForwardingMaxNumber();
        assertTrue(routeForwardingMaxNumber < 3);

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                throw new ForwardRouteException("/two");
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                throw new ForwardRouteException("/three");
            }
        });

        getRouter().GET("/three").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                throw new ForwardRouteException("/four");
            }
        });

        getRouter().GET("/four").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("four");
            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test
    public void forwardMustChangeSomeRequestInfo() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                throw new ForwardRouteException("/two?q=two", true);
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String expected = createTestUrl("/two?q=two");
                String fullUrl = context.request().getFullUrl();
                assertEquals(expected, fullUrl);

                String path = context.request().getRequestPath();
                assertEquals("/two", path);

                String queryString = context.request().getQueryString(false);
                assertEquals("q=two", queryString);
            }
        });

        IHttpResponse response = GET("/one?q=one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

    }

    @Test
    public void forwardMustKeepSomeRequestInfo() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                throw new ForwardRouteException("/two?q=two", true);
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                // Keep the headers
                String header = context.request().getHeaderFirst(HttpHeaders.ACCEPT);
                assertNotNull(header);
                assertEquals(ContentTypeDefaults.JSON.getMainVariation(), header);

                // Keep the cookies
                ICookie cookie = context.cookies().getCookie("name1");
                assertNotNull(cookie);
                assertEquals("toto", cookie.getValue());

                // The original url
                String expected = createTestUrl("/one?q=one");
                String originalFullUrl = context.request().getFullUrlOriginal();
                assertEquals(expected, originalFullUrl);

                // The server getFullUrl() also still returns the original
                // URL.
                IServer server = context.guice().getInstance(IServer.class);
                String originalUrl = server.getFullUrlOriginal(context.exchange());
                assertEquals(expected, originalUrl);
            }
        });

        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put(HttpHeaders.ACCEPT, Lists.newArrayList(ContentTypeDefaults.JSON.getMainVariation()));

        ICookie cookie = getCookieFactory().createCookie("name1", "toto");
        cookie.setDomain(getSpincastConfig().getServerHost());
        cookie.setPath("/");

        IHttpResponse response = GET("/one?q=one").addCookie(cookie).setHeaders(headers).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void forwardMustKeepSomeRequestPostInfo() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                throw new ForwardRouteException("/two?q=two", true);
            }
        });

        getRouter().POST("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                // Keep the body
                IJsonObject jsonObj = context.request().getJsonBodyAsJsonObject();
                assertNotNull(jsonObj);
                assertNotNull(jsonObj.getString("name"));
                assertEquals("toto", jsonObj.getString("name"));

                // Keeo the Content-Type
                assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(),
                             context.request().getHeaderFirst(HttpHeaders.CONTENT_TYPE));

            }
        });

        IHttpResponse response = POST("/one?q=one").setEntityString("{\"name\":\"toto\"}",
                                                                    ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                                                   .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

    }

    @Test
    public void forwardNotFound() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                throw new ForwardRouteException("/two?q=two", true);
            }
        });

        getRouter().notFound(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void forwardMustRespectHttpMethod() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                throw new ForwardRouteException("/two?q=two", true);
            }
        });

        getRouter().POST("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

    }

    @Test
    public void isForwarded() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                assertFalse(context.routing().isForwarded());
                throw new ForwardRouteException("/two?q=two", true);
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                assertTrue(context.routing().isForwarded());
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

    }

}
