package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRoute;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.common.net.HttpHeaders;

public class RouteBuilderTest extends DefaultIntegrationTestingBase {

    @Test
    public void createRoute() throws Exception {

        assertEquals(0, getRouter().getGlobalBeforeFiltersRoutes().size());
        assertEquals(0, getRouter().getMainRoutes().size());
        assertEquals(0, getRouter().getGlobalAfterFiltersRoutes().size());

        IRoute<IDefaultRequestContext> route =
                getRouter().GET("/")
                           .create(new IHandler<IDefaultRequestContext>() {

                               @Override
                               public void handle(IDefaultRequestContext context) {
                                   context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
                               }
                           });

        assertEquals(0, getRouter().getGlobalBeforeFiltersRoutes().size());
        assertEquals(0, getRouter().getMainRoutes().size());
        assertEquals(0, getRouter().getGlobalAfterFiltersRoutes().size());

        SpincastTestHttpResponse response = get("/");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        getRouter().addRoute(route);

        assertEquals(0, getRouter().getGlobalBeforeFiltersRoutes().size());

        // A default NotFound route will have been added too!!
        assertEquals(2, getRouter().getMainRoutes().size());
        assertEquals(0, getRouter().getGlobalAfterFiltersRoutes().size());

        response = get("/");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void minimumConfigs() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        SpincastTestHttpResponse response = get("/");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void filterPosition() throws Exception {

        getRouter().GET("/").pos(100).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("4");
            }
        });

        getRouter().GET("/").pos(-1).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("2");
            }
        });

        getRouter().GET("/").pos(1).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("3");
            }
        });

        getRouter().GET("/").pos(1000).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("5");
            }
        });

        getRouter().GET("/").pos(-2).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("1");
            }
        });

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("main");
            }
        });

        SpincastTestHttpResponse response = get("/");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("12main345", response.getContent());
    }

    @Test
    public void multipleInlineFilters() throws Exception {

        IHandler<IDefaultRequestContext> handler = new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("1");
            }
        };

        getRouter().GET("/")
                   .before(handler)
                   .before(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           context.response().sendPlainText("2");
                       }
                   })
                   .before(handler)
                   .after(handler)
                   .after(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           context.response().sendPlainText("2");
                       }
                   })
                   .after(handler)
                   .save(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           context.response().sendPlainText("M");
                       }
                   });

        SpincastTestHttpResponse response = get("/");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("121M121", response.getContent());
    }

    @Test
    public void acceptContentType() throws Exception {

        getRouter().GET("/")
                   .accept(ContentTypeDefaults.JSON)
                   .save(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           context.response().sendPlainText("ok");
                       }
                   });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ACCEPT, ContentTypeDefaults.JSON.getMainVariation());
        SpincastTestHttpResponse response = get("/", headers);
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContent());

        headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ACCEPT, ContentTypeDefaults.XML.getMainVariation());
        response = get("/", headers);
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void acceptContentTypeAsString() throws Exception {

        getRouter().GET("/")
                   .acceptAsString("application/json")
                   .save(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           context.response().sendPlainText("ok");
                       }
                   });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ACCEPT, ContentTypeDefaults.JSON.getMainVariation());
        SpincastTestHttpResponse response = get("/", headers);
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContent());

        headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ACCEPT, ContentTypeDefaults.XML.getMainVariation());
        response = get("/", headers);
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

}
