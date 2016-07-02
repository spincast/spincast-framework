package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;

public class RoutingTypesAllByDefaultTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Inject
    protected ISpincastDictionary spincastDictionary;

    @Test
    public void notFoundWithOneInvalidOneValidFilter() throws Exception {

        getRouter().GET("/*{param}").pos(-1).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().before("/*{param}", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        IHttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("B" + this.spincastDictionary.route_notFound_default_message(), response.getContentAsString());
    }

    @Test
    public void defaultRouteTypesFoundSplat() throws Exception {

        getRouter().before("/nope", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("nope");
            }
        });

        getRouter().before("/*{param}", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Aok", response.getContentAsString());
    }

    @Test
    public void defaultRouteTypesFoundNotRouteType() throws Exception {

        getRouter().before("/*{param}", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/*{param}").pos(-1).notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("nope");
            }
        });

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Aok", response.getContentAsString());
    }

    @Test
    public void defaultRouteTypesNotFoundSplat() throws Exception {

        getRouter().before("/nope", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("nope");
            }
        });

        getRouter().before("/*{param}", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = GET("/two").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("A" + this.spincastDictionary.route_notFound_default_message(), response.getContentAsString());
    }

    @Test
    public void defaultRouteTypesNotFoundNotRouteType() throws Exception {

        getRouter().before("/*{param}", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/*{param}").pos(-1).notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        getRouter().GET("/*{param}").pos(-1).found().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("nope");
            }
        });

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = GET("/two").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("AB" + this.spincastDictionary.route_notFound_default_message(), response.getContentAsString());
    }

    @Test
    public void foundSpecifiedTypesBefore() throws Exception {

        getRouter().before("/*{param}", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/*{param}").pos(-1).found().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        getRouter().GET("/*{param}").pos(-1).notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("C");
            }
        });

        getRouter().GET("/*{param}").pos(-1).exception().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("D");
            }
        });

        getRouter().GET("/*{param}").pos(-1).found().notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("E");
            }
        });

        getRouter().GET("/*{param}").pos(-1).found().exception().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("F");
            }
        });

        getRouter().GET("/*{param}").pos(-1).notFound().exception().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("G");
            }
        });

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ABEFok", response.getContentAsString());
    }

    @Test
    public void foundSpecifiedTypesAfter() throws Exception {

        getRouter().GET("/*{param}").pos(1).allRoutingTypes().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/*{param}").pos(1).found().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        getRouter().GET("/*{param}").pos(1).notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("C");
            }
        });

        getRouter().GET("/*{param}").pos(1).exception().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("D");
            }
        });

        getRouter().GET("/*{param}").pos(1).found().notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("E");
            }
        });

        getRouter().GET("/*{param}").pos(1).found().exception().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("F");
            }
        });

        getRouter().GET("/*{param}").pos(1).notFound().exception().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("G");
            }
        });

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("okABEF", response.getContentAsString());
    }

    @Test
    public void specifiedTypesBeforeAndAfter() throws Exception {

        getRouter().beforeAndAfter("/*{param}", new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/*{param}").pos(-1).pos(1).found().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        getRouter().GET("/*{param}").pos(-1).pos(1).notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("C");
            }
        });

        getRouter().GET("/*{param}").pos(-1).pos(1).exception().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("D");
            }
        });

        getRouter().GET("/*{param}").pos(-1).pos(1).found().notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("E");
            }
        });

        getRouter().GET("/*{param}").pos(-1).pos(1).found().exception().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("F");
            }
        });

        getRouter().GET("/*{param}").pos(-1).pos(1).notFound().exception().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("G");
            }
        });

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ABEFokABEF", response.getContentAsString());
    }

}
