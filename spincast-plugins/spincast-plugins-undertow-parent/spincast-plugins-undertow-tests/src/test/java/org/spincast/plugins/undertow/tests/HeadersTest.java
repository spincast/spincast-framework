package org.spincast.plugins.undertow.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.server.Server;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;

public class HeadersTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected Server server;

    @Test
    public void headersAreCaseInsensitive() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Map<String, List<String>> requestHeaders = HeadersTest.this.server.getRequestHeaders(context.exchange());

                List<String> headers = requestHeaders.get("MyKey");
                assertNotNull(headers);
                assertEquals(1, headers.size());
                assertEquals("MyValue", headers.get(0));

                headers = requestHeaders.get("mykey");
                assertNotNull(headers);
                assertEquals(1, headers.size());
                assertEquals("MyValue", headers.get(0));

                headers = requestHeaders.get("MYKEy");
                assertNotNull(headers);
                assertEquals(1, headers.size());
                assertEquals("MyValue", headers.get(0));

                headers = requestHeaders.get("Nope");
                assertNull(headers);

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").addHeaderValue("MyKey", "MyValue").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
