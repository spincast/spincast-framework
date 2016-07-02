package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IHandler;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class RoutingHttpMethods2Test extends SpincastDefaultNoAppIntegrationTestBase {

    @Test
    public void get() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                assertEquals(HttpMethod.GET, context.request().getHttpMethod());
            }
        });

        IHttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void post() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                assertEquals(HttpMethod.POST, context.request().getHttpMethod());
            }
        });

        IHttpResponse response = POST("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
