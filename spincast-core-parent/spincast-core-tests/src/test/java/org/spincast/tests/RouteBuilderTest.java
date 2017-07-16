package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.Route;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;

public class RouteBuilderTest extends NoAppStartHttpServerTestingBase {

    //==========================================
    // Remove all routes, even Spincast ones
    //==========================================
    @Override
    protected boolean removeSpincastRoutesToo() {
        return true;
    }

    @Test
    public void createRoute() throws Exception {

        assertEquals(0, getRouter().getGlobalBeforeFiltersRoutes().size());
        assertEquals(0, getRouter().getMainRoutes().size());
        assertEquals(0, getRouter().getGlobalAfterFiltersRoutes().size());

        Route<DefaultRequestContext> route =
                getRouter().GET("/")
                           .create(new Handler<DefaultRequestContext>() {

                               @Override
                               public void handle(DefaultRequestContext context) {
                                   context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
                               }
                           });

        assertEquals(0, getRouter().getGlobalBeforeFiltersRoutes().size());
        assertEquals(0, getRouter().getMainRoutes().size());
        assertEquals(0, getRouter().getGlobalAfterFiltersRoutes().size());

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        getRouter().addRoute(route);

        assertEquals(0, getRouter().getGlobalBeforeFiltersRoutes().size());

        // A default NotFound route will have been added too!!
        assertEquals(2, getRouter().getMainRoutes().size());
        assertEquals(0, getRouter().getGlobalAfterFiltersRoutes().size());

        response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void minimumConfigs() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void filterPosition() throws Exception {

        getRouter().GET("/").pos(100).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("4");
            }
        });

        getRouter().GET("/").pos(-1).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("2");
            }
        });

        getRouter().GET("/").pos(1).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("3");
            }
        });

        getRouter().GET("/").pos(1000).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("5");
            }
        });

        getRouter().GET("/").pos(-2).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("1");
            }
        });

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("main");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("12main345", response.getContentAsString());
    }

    @Test
    public void multipleInlineFilters() throws Exception {

        Handler<DefaultRequestContext> handler = new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("1");
            }
        };

        getRouter().GET("/")
                   .before(handler)
                   .before(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("2");
                       }
                   })
                   .before(handler)
                   .after(handler)
                   .after(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("2");
                       }
                   })
                   .after(handler)
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("M");
                       }
                   });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("121M121", response.getContentAsString());
    }

    @Test
    public void acceptContentType() throws Exception {

        getRouter().GET("/")
                   .accept(ContentTypeDefaults.JSON)
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("ok");
                       }
                   });

        HttpResponse response =
                GET("/").addHeaderValue(HttpHeaders.ACCEPT, ContentTypeDefaults.JSON.getMainVariation()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());

        response = GET("/").addHeaderValue(HttpHeaders.ACCEPT, ContentTypeDefaults.XML.getMainVariation()).send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void acceptContentTypeAsString() throws Exception {

        getRouter().GET("/")
                   .acceptAsString("application/json")
                   .save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("ok");
                       }
                   });

        HttpResponse response =
                GET("/").setHeaderValues(HttpHeaders.ACCEPT, Lists.newArrayList(ContentTypeDefaults.JSON.getMainVariation()))
                        .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());

        response = GET("/").addHeaderValue(HttpHeaders.ACCEPT, ContentTypeDefaults.XML.getMainVariation()).send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

}
