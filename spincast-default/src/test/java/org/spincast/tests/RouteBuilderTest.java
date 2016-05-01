package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRoute;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.common.collect.Lists;
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

        IHttpResponse response = GET("/").send();
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

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
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

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("12main345", response.getContentAsString());
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

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("121M121", response.getContentAsString());
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

        IHttpResponse response =
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
                   .save(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           context.response().sendPlainText("ok");
                       }
                   });

        IHttpResponse response =
                GET("/").setHeaderValues(HttpHeaders.ACCEPT, Lists.newArrayList(ContentTypeDefaults.JSON.getMainVariation()))
                        .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());

        response = GET("/").addHeaderValue(HttpHeaders.ACCEPT, ContentTypeDefaults.XML.getMainVariation()).send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

}
