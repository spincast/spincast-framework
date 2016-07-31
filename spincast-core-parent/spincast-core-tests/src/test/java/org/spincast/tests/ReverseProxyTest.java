package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.spincast.core.config.SpincastConstants.HttpHeadersExtra;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.common.net.HttpHeaders;

public class ReverseProxyTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Test
    public void none() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertEquals(context.request().getFullUrl(), context.request().getFullUrlOriginal());
                assertEquals(context.request().getFullUrl(), context.request().getFullUrlProxied());

                context.response().sendPlainText(context.request().getFullUrl());
            }
        });

        IHttpResponse response = GET("/one?titi=toto").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("http://" + getSpincastConfig().getServerHost() + ":" + getSpincastConfig().getHttpServerPort() +
                     "/one?titi=toto",
                     response.getContentAsString());
    }

    @Test
    public void proto() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertEquals(context.request().getFullUrl(), context.request().getFullUrlOriginal());
                assertNotEquals(context.request().getFullUrl(), context.request().getFullUrlProxied());

                context.response().sendPlainText(context.request().getFullUrl());
            }
        });

        IHttpResponse response = GET("/one?titi=toto").addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                                                      .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("https://" + getSpincastConfig().getServerHost() + ":" + getSpincastConfig().getHttpServerPort() +
                     "/one?titi=toto",
                     response.getContentAsString());
    }

    @Test
    public void protoHost() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertEquals(context.request().getFullUrl(), context.request().getFullUrlOriginal());
                assertNotEquals(context.request().getFullUrl(), context.request().getFullUrlProxied());

                context.response().sendPlainText(context.request().getFullUrl());
            }
        });

        IHttpResponse response = GET("/one?titi=toto").addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                                                      .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                                                      .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("https://someHost:" + getSpincastConfig().getHttpServerPort() +
                     "/one?titi=toto",
                     response.getContentAsString());
    }

    @Test
    public void protoHostAndPort() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertEquals(context.request().getFullUrl(), context.request().getFullUrlOriginal());
                assertNotEquals(context.request().getFullUrl(), context.request().getFullUrlProxied());

                context.response().sendPlainText(context.request().getFullUrl());
            }
        });

        IHttpResponse response = GET("/one?titi=toto").addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                                                      .addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                                                      .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "12345")
                                                      .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("https://someHost:12345/one?titi=toto", response.getContentAsString());
    }

    @Test
    public void hostAndPort() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertEquals(context.request().getFullUrl(), context.request().getFullUrlOriginal());
                assertNotEquals(context.request().getFullUrl(), context.request().getFullUrlProxied());

                context.response().sendPlainText(context.request().getFullUrl());
            }
        });

        IHttpResponse response = GET("/one?titi=toto").addHeaderValue(HttpHeadersExtra.X_FORWARDED_HOST, "someHost")
                                                      .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "12345")
                                                      .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("http://someHost:12345/one?titi=toto", response.getContentAsString());
    }

    @Test
    public void protoPort() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertEquals(context.request().getFullUrl(), context.request().getFullUrlOriginal());
                assertNotEquals(context.request().getFullUrl(), context.request().getFullUrlProxied());

                context.response().sendPlainText(context.request().getFullUrl());
            }
        });

        IHttpResponse response = GET("/one?titi=toto").addHeaderValue(HttpHeaders.X_FORWARDED_PROTO, "https")
                                                      .addHeaderValue(HttpHeadersExtra.X_FORWARDED_PORT, "12345")
                                                      .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("https://" + getSpincastConfig().getServerHost() + ":12345/one?titi=toto",
                     response.getContentAsString());
    }

}