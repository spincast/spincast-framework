package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.SpincastCoreDictionaryEntriesDefault;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;

public class RoutingTypesAllByDefaultTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected Dictionary dictionary;

    @Test
    public void notFoundWithOneInvalidOneValidFilter() throws Exception {

        getRouter().GET("/*{param}").pos(-1).exception().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().ALL("/*{param}").pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        HttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("B" +
                     this.dictionary.get(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_ROUTE_NOT_FOUND_DEFAULTMESSAGE),
                     response.getContentAsString());
    }

    @Test
    public void defaultRouteTypesFoundSplat() throws Exception {

        getRouter().ALL("/nope").pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("nope");
            }
        });

        getRouter().ALL("/*{param}").pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

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

        getRouter().ALL("/*{param}").pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/*{param}").pos(-1).notFound().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("nope");
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

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

        getRouter().ALL("/nope").pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("nope");
            }
        });

        getRouter().ALL("/*{param}").pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/two").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("A" +
                     this.dictionary.get(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_ROUTE_NOT_FOUND_DEFAULTMESSAGE),
                     response.getContentAsString());
    }

    @Test
    public void defaultRouteTypesNotFoundNotRouteType() throws Exception {

        getRouter().ALL("/*{param}").pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/*{param}").pos(-1).notFound().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        getRouter().GET("/*{param}").pos(-1).found().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("nope");
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/two").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("AB" +
                     this.dictionary.get(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_ROUTE_NOT_FOUND_DEFAULTMESSAGE),
                     response.getContentAsString());
    }

    @Test
    public void foundSpecifiedTypesBefore() throws Exception {

        getRouter().ALL("/*{param}").pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/*{param}").pos(-1).found().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        getRouter().GET("/*{param}").pos(-1).notFound().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("C");
            }
        });

        getRouter().GET("/*{param}").pos(-1).exception().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("D");
            }
        });

        getRouter().GET("/*{param}").pos(-1).found().notFound().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("E");
            }
        });

        getRouter().GET("/*{param}").pos(-1).found().exception().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("F");
            }
        });

        getRouter().GET("/*{param}").pos(-1).notFound().exception().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("G");
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

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

        getRouter().GET("/*{param}").pos(1).allRoutingTypes().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        getRouter().GET("/*{param}").pos(1).found().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        getRouter().GET("/*{param}").pos(1).notFound().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("C");
            }
        });

        getRouter().GET("/*{param}").pos(1).exception().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("D");
            }
        });

        getRouter().GET("/*{param}").pos(1).found().notFound().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("E");
            }
        });

        getRouter().GET("/*{param}").pos(1).found().exception().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("F");
            }
        });

        getRouter().GET("/*{param}").pos(1).notFound().exception().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("G");
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

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

}
