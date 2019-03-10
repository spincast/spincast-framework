package org.spincast.plugins.httpclient.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.exceptions.RedirectException;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpException;
import org.spincast.shaded.org.apache.http.HttpResponseInterceptor;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.client.CookieStore;
import org.spincast.shaded.org.apache.http.client.config.RequestConfig;
import org.spincast.shaded.org.apache.http.impl.client.BasicCookieStore;
import org.spincast.shaded.org.apache.http.impl.client.HttpClientBuilder;
import org.spincast.shaded.org.apache.http.protocol.HttpContext;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.common.collect.Lists;

public class HttpClientTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void get() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());
                assertNull(context.request().getHeaderFirst("test-header"));

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void getWithSetHeaders() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());

                List<String> values = context.request().getHeader("test-header");
                assertEquals(2, context.request().getHeader("test-header").size());
                Set<String> valuesSet = new HashSet<>(values);
                assertTrue(valuesSet.contains("test"));
                assertTrue(valuesSet.contains("testb"));

                assertEquals("test", context.request().getHeaderFirst("test-header"));

                assertEquals(1, context.request().getHeader("test-header2").size());
                assertEquals("test2", context.request().getHeaderFirst("test-header2"));

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put("test-header", Lists.newArrayList("test", "testb"));
        headers.put("test-header2", Lists.newArrayList("test2"));
        HttpResponse response = GET("/").setHeaders(headers)
                                        .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void getWithSetHeaderValues() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());

                Set<String> values = new HashSet<>(context.request().getHeader("test-header"));
                assertEquals(2, values.size());
                assertTrue(values.contains("test"));
                assertTrue(values.contains("test2"));

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/").setHeaderValues("test-header", Lists.newArrayList("nope"))
                                        .setHeaderValues("test-header", Lists.newArrayList("test", "test2"))
                                        .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void getWithAddHeaderValues() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Set<String> values = new HashSet<>(context.request().getHeader("test-header"));
                assertEquals(3, values.size());
                assertTrue(values.contains("test1"));
                assertTrue(values.contains("test2"));
                assertTrue(values.contains("test3"));

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/").addHeaderValue("test-header", "test1")
                                        .addHeaderValues("test-header", Lists.newArrayList("test2", "test3"))
                                        .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void getWithAddHeaderValueAndRedirect() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());
                assertEquals("test", context.request().getHeaderFirst("test-header"));
                assertEquals("test2", context.request().getHeaderFirst("test-header2"));

                context.response().redirect("/test", true);
            }
        });

        getRouter().GET("/test").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/").addHeaderValue("test-header", "test")
                                        .addHeaderValue("test-header2", "test2")
                                        .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void getWithAddHeaderValueAndRedirectException() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());
                assertEquals("test", context.request().getHeaderFirst("test-header"));
                assertEquals("test2", context.request().getHeaderFirst("test-header2"));

                throw new RedirectException("/test", true);
            }
        });

        getRouter().GET("/test").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().setStatusCode(HttpStatus.SC_EXPECTATION_FAILED).sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/").addHeaderValue("test-header", "test")
                                        .addHeaderValue("test-header2", "test2")
                                        .send();

        assertEquals(HttpStatus.SC_EXPECTATION_FAILED, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void getWithRequestConfigNoRedirect() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());
                context.response().redirect("/test", true);
            }
        });

        getRouter().GET("/test").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        RequestConfig noRedirectConfig = RequestConfig.custom().setRedirectsEnabled(false).build();

        HttpResponse response = GET("/").setRequestConfig(noRedirectConfig)
                                        .send();

        assertEquals(HttpStatus.SC_MOVED_PERMANENTLY, response.getStatus());
    }

    @Test
    public void getValidateGzip() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        getRouter().file("/image").classpath("/image.jpg").handle();

        HttpResponse response = GET("/").send();
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertTrue(response.isGzipped());

        response = GET("/image").send();
        assertNotNull(response);
        assertFalse(response.isGzipped());
    }

    @Test
    public void getResponseHeaders() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());
                context.response().addHeaderValues("test-header", Lists.newArrayList("111", "222"));
                context.response().addHeaderValue("test-header2", "333");
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/").send();
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        Map<String, List<String>> headers = response.getHeaders();
        assertNotNull(headers);

        List<String> values = headers.get("test-header");
        assertNotNull(values);
        assertEquals(2, values.size());
        Set<String> valuesSet = new HashSet<>(values);
        assertTrue(valuesSet.contains("111"));
        assertTrue(valuesSet.contains("222"));

        values = headers.get("test-header2");
        assertNotNull(values);
        assertEquals(1, values.size());
        assertEquals("333", values.get(0));

        values = response.getHeader("test-header");
        assertNotNull(values);
        assertEquals(2, values.size());
        valuesSet = new HashSet<>(values);
        assertTrue(valuesSet.contains("111"));
        assertTrue(valuesSet.contains("222"));

        String value = response.getHeaderFirst("test-header");
        assertNotNull(values);
        assertEquals("111", value);
    }

    @Test
    public void getWithCookies() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());

                String cookie = context.request().getCookieValue("sendCookie1");
                assertNotNull(cookie);
                assertEquals("sendCookieVal1", cookie);

                cookie = context.request().getCookieValue("sendCookie2");
                assertNotNull(cookie);
                assertEquals("sendCookieVal2", cookie);

                cookie = context.request().getCookieValue("sendCookie13");
                assertNotNull(cookie);
                assertEquals("sendCookieVal3", cookie);

                context.response().setCookieSession("cookie1", "cookieVal1");
                context.response().setCookieSession("cookie2", "cookieVal2");

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        Cookie cookie = getCookieFactory().createCookie("sendCookie13", "sendCookieVal3");
        cookie.setSecure(false);

        HttpResponse response = GET("/").setCookie("sendCookie1", "sendCookieVal1", false)
                                        .setCookie("sendCookie2", "sendCookieVal2", false)
                                        .setCookie(cookie)
                                        .send();
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        cookie = response.getCookie("cookie1");
        assertNotNull(cookie);
        assertEquals("cookieVal1", cookie.getValue());

        cookie = response.getCookie("cookie2");
        assertNotNull(cookie);
        assertEquals("cookieVal2", cookie.getValue());

    }

    @Test
    public void customHttpClientBuilder() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        final boolean[] flag = new boolean[]{false};

        HttpClientBuilder customHttpClientBuilder = HttpClientBuilder.create();
        customHttpClientBuilder.addInterceptorLast(new HttpResponseInterceptor() {

            @Override
            public void process(org.spincast.shaded.org.apache.http.HttpResponse response,
                                HttpContext context) throws HttpException, IOException {
                flag[0] = true;
            }
        });

        HttpResponse response = GET("/").setHttpClientBuilder(customHttpClientBuilder).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());

        assertTrue(flag[0]);
    }

    @Test
    public void customCookieStore() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());

                String cookie = context.request().getCookieValue("sentCookie1");
                assertNotNull(cookie);
                assertEquals("sent1", cookie);

                context.response().setCookieSession("testCookie", "testValue");
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpClientBuilder customHttpClientBuilder = HttpClientBuilder.create();

        CookieStore cookieStore = new BasicCookieStore();
        customHttpClientBuilder.setDefaultCookieStore(cookieStore);

        HttpResponse response = GET("/").setHttpClientBuilder(customHttpClientBuilder)
                                        .setCookie("sentCookie1", "sent1", false)
                                        .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());

        List<org.spincast.shaded.org.apache.http.cookie.Cookie> cookies = cookieStore.getCookies();
        assertNotNull(cookies);
        assertEquals(2, cookies.size());
        org.spincast.shaded.org.apache.http.cookie.Cookie cookie = cookies.get(0);
        assertNotNull(cookie);
        assertEquals("sentCookie1", cookie.getName());
        assertEquals("sent1", cookie.getValue());

        cookie = cookies.get(1);
        assertNotNull(cookie);
        assertEquals("testCookie", cookie.getName());
        assertEquals("testValue", cookie.getValue());
    }

    @Test
    public void trace() throws Exception {

        getRouter().TRACE("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = TRACE("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void options() throws Exception {

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = OPTIONS("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void head() throws Exception {

        getRouter().HEAD("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = HEAD("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());

        // HEAD shouldn't return a body.
        assertTrue(StringUtils.isBlank(response.getContentAsString()));
    }

    @Test
    public void setHttpAuthCredentials() throws Exception {

        getServer().addHttpAuthentication("testRealm", "user1", "pass1");

        getRouter().httpAuth("/one", "testRealm");
        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("one");
            }
        });

        HttpResponse response = GET("/one").setHttpAuthCredentials("user1", "pass1").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("one", response.getContentAsString());

        response = GET("/one").setHttpAuthCredentials("user2", "pass2").send();
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatus());
    }

}
