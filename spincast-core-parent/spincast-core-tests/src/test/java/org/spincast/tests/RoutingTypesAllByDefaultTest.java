package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;

public class RoutingTypesAllByDefaultTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Inject
    protected SpincastDictionary spincastDictionary;

    @Test
    public void notFoundWithOneInvalidOneValidFilter() throws Exception {

        getRouter().GET("/*{param}").pos(-1).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().before("/*{param}").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        HttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("B" + this.spincastDictionary.route_notFound_default_message(), response.getContentAsString());
    }

    @Test
    public void defaultRouteTypesFoundSplat() throws Exception {

        getRouter().before("/nope").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("nope");
            }
        });

        getRouter().before("/*{param}").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Aok", response.getContentAsString());
    }

    @Test
    public void defaultRouteTypesFoundNotRouteType() throws Exception {

        getRouter().before("/*{param}").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/*{param}").pos(-1).notFound().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("nope");
            }
        });

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Aok", response.getContentAsString());
    }

    @Test
    public void defaultRouteTypesNotFoundSplat() throws Exception {

        getRouter().before("/nope").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("nope");
            }
        });

        getRouter().before("/*{param}").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/two").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("A" + this.spincastDictionary.route_notFound_default_message(), response.getContentAsString());
    }

    @Test
    public void defaultRouteTypesNotFoundNotRouteType() throws Exception {

        getRouter().before("/*{param}").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/*{param}").pos(-1).notFound().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        getRouter().GET("/*{param}").pos(-1).found().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("nope");
            }
        });

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/two").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("AB" + this.spincastDictionary.route_notFound_default_message(), response.getContentAsString());
    }

    @Test
    public void foundSpecifiedTypesBefore() throws Exception {

        getRouter().before("/*{param}").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/*{param}").pos(-1).found().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        getRouter().GET("/*{param}").pos(-1).notFound().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("C");
            }
        });

        getRouter().GET("/*{param}").pos(-1).exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("D");
            }
        });

        getRouter().GET("/*{param}").pos(-1).found().notFound().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("E");
            }
        });

        getRouter().GET("/*{param}").pos(-1).found().exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("F");
            }
        });

        getRouter().GET("/*{param}").pos(-1).notFound().exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("G");
            }
        });

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ABEFok", response.getContentAsString());
    }

    @Test
    public void foundSpecifiedTypesAfter() throws Exception {

        getRouter().GET("/*{param}").pos(1).allRoutingTypes().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/*{param}").pos(1).found().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        getRouter().GET("/*{param}").pos(1).notFound().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("C");
            }
        });

        getRouter().GET("/*{param}").pos(1).exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("D");
            }
        });

        getRouter().GET("/*{param}").pos(1).found().notFound().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("E");
            }
        });

        getRouter().GET("/*{param}").pos(1).found().exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("F");
            }
        });

        getRouter().GET("/*{param}").pos(1).notFound().exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("G");
            }
        });

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("okABEF", response.getContentAsString());
    }

    @Test
    public void specifiedTypesBeforeAndAfter() throws Exception {

        getRouter().beforeAndAfter("/*{param}").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/*{param}").pos(-10).pos(10).found().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        getRouter().GET("/*{param}").pos(-10).pos(10).notFound().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("C");
            }
        });

        getRouter().GET("/*{param}").pos(-10).pos(10).exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("D");
            }
        });

        getRouter().GET("/*{param}").pos(-10).pos(10).found().notFound().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("E");
            }
        });

        getRouter().GET("/*{param}").pos(-10).pos(10).found().exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("F");
            }
        });

        getRouter().GET("/*{param}").pos(-10).pos(10).notFound().exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("G");
            }
        });

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ABEFokABEF", response.getContentAsString());
    }

}
