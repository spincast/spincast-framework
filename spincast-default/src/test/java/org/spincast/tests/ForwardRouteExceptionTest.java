package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.cookies.ICookie;
import org.spincast.core.exceptions.ForwardRouteException;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.IHandler;
import org.spincast.core.server.IServer;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.client.CookieStore;
import org.spincast.shaded.org.apache.http.impl.cookie.BasicClientCookie;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;

import com.google.common.net.HttpHeaders;

public class ForwardRouteExceptionTest extends DefaultIntegrationTestingBase {

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

        SpincastTestHttpResponse response = get("/one");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContent());
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

        SpincastTestHttpResponse response = get("/one");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContent());
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

        SpincastTestHttpResponse response = get("/one");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("onetwo", response.getContent());
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

        SpincastTestHttpResponse response = get("/one");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("twothreefour", response.getContent());
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

        SpincastTestHttpResponse response = get("/one");

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

                String queryString = context.request().getQueryString();
                assertEquals("q=two", queryString);
            }
        });

        SpincastTestHttpResponse response = get("/one?q=one");
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
                String originalFullUrl = context.request().getOriginalFullUrl();
                assertEquals(expected, originalFullUrl);

                // The server getFullUrl() also still returns the original
                // URL.
                IServer server = context.guice().getInstance(IServer.class);
                String originalUrl = server.getFullUrl(context.exchange());
                assertEquals(expected, originalUrl);
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ACCEPT, ContentTypeDefaults.JSON.getMainVariation());

        CookieStore cookieStore = getCookieStore();

        BasicClientCookie basicClientCookie = new BasicClientCookie("name1", "toto");
        basicClientCookie.setDomain(getSpincastConfig().getServerHost());
        basicClientCookie.setPath("/");
        cookieStore.addCookie(basicClientCookie);

        SpincastTestHttpResponse response = get("/one?q=one", headers);
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

        SpincastTestHttpResponse response = postJson("/one?q=one", "{\"name\":\"toto\"}");
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

        SpincastTestHttpResponse response = get("/one");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContent());
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

        SpincastTestHttpResponse response = get("/one");
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

        SpincastTestHttpResponse response = get("/one");
        assertEquals(HttpStatus.SC_OK, response.getStatus());

    }

}
