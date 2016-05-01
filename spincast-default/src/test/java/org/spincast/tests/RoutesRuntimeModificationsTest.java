package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.routing.IRoute;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class RoutesRuntimeModificationsTest extends DefaultIntegrationTestingBase {

    @Test
    public void idsAreUnique() throws Exception {

        getRouter().GET("/a").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("one");
            }
        });

        getRouter().GET("/b").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("one");
            }
        });

        getRouter().GET("/c").id("test").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("one");
            }
        });

        try {
            getRouter().GET("/d").id("test").save(new IHandler<IDefaultRequestContext>() {

                @Override
                public void handle(IDefaultRequestContext context) {
                    context.response().sendPlainText("one");
                }
            });

            fail();
        } catch(Exception ex) {
        }

    }

    @Test
    public void getRouteById() throws Exception {

        getRouter().GET("/one").id("test").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("one");
            }
        });

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IRoute<IDefaultRequestContext> route = getRouter().getRoute("test");
                assertNotNull(route);

                route = getRouter().getRoute("nope");
                assertNull(route);
            }
        });

        IHttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

    }

    @Test
    public void addRouteDynamically() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

                    @Override
                    public void handle(IDefaultRequestContext context) {
                        context.response().sendPlainText("two");
                    }
                });
                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = GET("/two").send();
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

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                getRouter().removeRoute("routeTwo");
                context.response().sendPlainText("ok");
            }
        });

        getRouter().GET("/two").id("routeTwo").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("two");
            }
        });

        IHttpResponse response = GET("/two").send();
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
