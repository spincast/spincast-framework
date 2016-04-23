package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IHandler;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.NameValuePair;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;

public class RoutingHttpMethods2Test extends DefaultIntegrationTestingBase {

    @Test
    public void get() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                assertEquals(HttpMethod.GET, context.request().getHttpMethod());
            }
        });

        SpincastTestHttpResponse response = get("/");
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

        SpincastTestHttpResponse response = postWithParams("/", new ArrayList<NameValuePair>());
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
