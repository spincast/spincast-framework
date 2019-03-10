package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.Route;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

public class RoutesRuntimeModificationsTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void idsAreUnique() throws Exception {

        getRouter().GET("/a").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("one");
            }
        });

        getRouter().GET("/b").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("one");
            }
        });

        getRouter().GET("/c").id("test").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("one");
            }
        });

        try {
            getRouter().GET("/d").id("test").handle(new Handler<DefaultRequestContext>() {

                @Override
                public void handle(DefaultRequestContext context) {
                    context.response().sendPlainText("one");
                }
            });

            fail();
        } catch (Exception ex) {
        }

    }

    @Test
    public void getRouteById() throws Exception {

        getRouter().GET("/one").id("test").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("one");
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Route<DefaultRequestContext> route = getRouter().getRoute("test");
                assertNotNull(route);

                route = getRouter().getRoute("nope");
                assertNull(route);
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

    }

    @Test
    public void addRouteDynamically() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

                    @Override
                    public void handle(DefaultRequestContext context) {
                        context.response().sendPlainText("two");
                    }
                });
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/two").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());

        response = GET("/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());

    }

    @Test
    public void removeRouteDynamically() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                getRouter().removeRoute("routeTwo");
                context.response().sendPlainText("ok");
            }
        });

        getRouter().GET("/two").id("routeTwo").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        HttpResponse response = GET("/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());

        response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());

        response = GET("/two").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

    }

}
