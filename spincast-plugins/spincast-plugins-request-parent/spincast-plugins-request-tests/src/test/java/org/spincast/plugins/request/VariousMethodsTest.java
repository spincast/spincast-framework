package org.spincast.plugins.request;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

public class VariousMethodsTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void getIp() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String ip = context.request().getIp();
                assertEquals("127.0.0.1", ip);
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
