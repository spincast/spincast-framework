package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.HttpMethod;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class RoutingHttpMethods2Test extends NoAppStartHttpServerTestingBase {

    @Test
    public void get() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                assertEquals(HttpMethod.GET, context.request().getHttpMethod());
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void post() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                assertEquals(HttpMethod.POST, context.request().getHttpMethod());
            }
        });

        HttpResponse response = POST("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
