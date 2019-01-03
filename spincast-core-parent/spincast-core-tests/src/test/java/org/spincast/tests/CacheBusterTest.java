package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.config.SpincastConstants.HttpHeadersExtra;
import org.spincast.core.exceptions.ForwardRouteException;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;

public class CacheBusterTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected SpincastUtils spincastUtils;

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    @Test
    public void removeCacheBusterRegularRoute() throws Exception {

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String requestPath = context.request().getRequestPath();
                assertEquals("/one.css", requestPath);

                String val = context.request().getQueryStringParamFirst("aaa");
                assertNotNull(val);
                assertEquals("bbb", val);

                String fullUrl = context.request().getFullUrl();
                assertNotNull(fullUrl);
                assertEquals("https://" + getSpincastConfig().getServerHost() + ":" + getSpincastConfig().getHttpsServerPort() +
                             "/one.css?aaa=bbb",
                             fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void removeCacheBusterStaticResource() throws Exception {

        getRouter().file("/someFile.txt").classpath("/someFile.txt").handle();

        String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        HttpResponse response = GET("/someFile" + cacheBusterCode + ".txt").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/plain", response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());

    }

    @Test
    public void getFullUrlDefault() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://someHost:81/one.css?aaa=bbb";

                String fullUrl = context.request().getFullUrl();
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlNoCacheBusters() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://someHost:81/one.css?aaa=bbb";

                String fullUrl = context.request().getFullUrl(false);
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlWithCacheBusters() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://someHost:81/one" + cacheBusterCode + ".css?aaa=bbb";

                String fullUrl = context.request().getFullUrl(true);
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlProxied() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://" + getSpincastConfig().getServerHost() + ":" + getSpincastConfig().getHttpsServerPort() +
                                         "/one.css?aaa=bbb";

                String fullUrl = context.request().getFullUrlProxied();
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlProxiedNoCacheBusters() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://" + getSpincastConfig().getServerHost() + ":" + getSpincastConfig().getHttpsServerPort() +
                                         "/one.css?aaa=bbb";

                String fullUrl = context.request().getFullUrlProxied(false);
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlProxiedWithCacheBusters() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://" + getSpincastConfig().getServerHost() + ":" + getSpincastConfig().getHttpsServerPort() +
                                         "/one" + cacheBusterCode + ".css?aaa=bbb";

                String fullUrl = context.request().getFullUrlProxied(true);
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlOriginalDefault() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://someHost:81/one.css?aaa=bbb";

                String fullUrl = context.request().getFullUrlOriginal();
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlOriginalNoCacheBusters() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://someHost:81/one.css?aaa=bbb";

                String fullUrl = context.request().getFullUrlOriginal(false);
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlOriginalWithCacheBusters() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://someHost:81/one" + cacheBusterCode + ".css?aaa=bbb";

                String fullUrl = context.request().getFullUrlOriginal(true);
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlDefaultWhenForwarded() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two.css" + context.request().getQueryString(true));
            }
        });

        getRouter().GET("/two.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://someHost:81/two.css?aaa=bbb";

                String fullUrl = context.request().getFullUrl();
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlNoCacheBustersWhenForwarded() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two.css" + context.request().getQueryString(true));
            }
        });

        getRouter().GET("/two.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://someHost:81/two.css?aaa=bbb";

                String fullUrl = context.request().getFullUrl(false);
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlWithCacheBustersWhenForwarded() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two.css" + context.request().getQueryString(true));
            }
        });

        getRouter().GET("/two.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://someHost:81/two.css?aaa=bbb";

                String fullUrl = context.request().getFullUrl(true);
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlProxiedWhenForwarded() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two.css" + context.request().getQueryString(true));
            }
        });

        getRouter().GET("/two.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://" + getSpincastConfig().getServerHost() + ":" + getSpincastConfig().getHttpsServerPort() +
                                         "/one.css?aaa=bbb";

                String fullUrl = context.request().getFullUrlProxied();
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlProxiedNoCacheBustersWhenForwarded() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two.css" + context.request().getQueryString(true));
            }
        });

        getRouter().GET("/two.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://" + getSpincastConfig().getServerHost() + ":" + getSpincastConfig().getHttpsServerPort() +
                                         "/one.css?aaa=bbb";

                String fullUrl = context.request().getFullUrlProxied(false);
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlProxiedWithCacheBustersWhenForwarded() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two.css" + context.request().getQueryString(true));
            }
        });

        getRouter().GET("/two.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://" + getSpincastConfig().getServerHost() + ":" + getSpincastConfig().getHttpsServerPort() +
                                         "/one" + cacheBusterCode + ".css?aaa=bbb";

                String fullUrl = context.request().getFullUrlProxied(true);
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlOriginalDefaultWhenForwarded() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two.css" + context.request().getQueryString(true));
            }
        });

        getRouter().GET("/two.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://someHost:81/one.css?aaa=bbb";

                String fullUrl = context.request().getFullUrlOriginal();
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlOriginalNoCacheBustersWhenForwarded() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two.css" + context.request().getQueryString(true));
            }
        });

        getRouter().GET("/two.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://someHost:81/one.css?aaa=bbb";

                String fullUrl = context.request().getFullUrlOriginal(false);
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void getFullUrlOriginalWithCacheBustersWhenForwarded() throws Exception {

        final String cacheBusterCode = getSpincastUtils().getCacheBusterCode();

        getRouter().GET("/one.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new ForwardRouteException("/two.css" + context.request().getQueryString(true));
            }
        });

        getRouter().GET("/two.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String fullUrlExpected =
                        "https://someHost:81/one" + cacheBusterCode + ".css?aaa=bbb";

                String fullUrl = context.request().getFullUrlOriginal(true);
                assertNotNull(fullUrl);
                assertEquals(fullUrlExpected, fullUrl);

                context.response().sendPlainText("ok");
            }
        });

        // @formatter:off
        HttpResponse response = GET("/one" + cacheBusterCode + ".css?aaa=bbb")
                .addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "81")
                .send(); 
        // @formatter:on

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

}
