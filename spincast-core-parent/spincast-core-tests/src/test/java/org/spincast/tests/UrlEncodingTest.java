package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URLEncoder;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

public class UrlEncodingTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void pathParamDecoding() throws Exception {

        getRouter().GET("/${param}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String param = context.request().getPathParam("param");
                assertNotNull(param);
                assertEquals("one two" + SpincastTestingUtils.TEST_STRING, param);
            }
        });

        HttpResponse response = GET("/" +
                                    URLEncoder.encode("one two" + SpincastTestingUtils.TEST_STRING, "UTF-8")).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void queryStringDecoding() throws Exception {

        getRouter().GET("/${param}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String queryString = context.request().getQueryString(false);
                assertNotNull(queryString);
                assertEquals("test=one two" + SpincastTestingUtils.TEST_STRING, queryString);
            }
        });

        HttpResponse response = GET("/one?test=" +
                                    URLEncoder.encode("one two" + SpincastTestingUtils.TEST_STRING, "UTF-8")).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void queryStringParamDecoding() throws Exception {

        getRouter().GET("/${param}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String param = context.request().getQueryStringParamFirst("test");
                assertNotNull(param);
                assertEquals("one two" + SpincastTestingUtils.TEST_STRING, param);
            }
        });

        HttpResponse response = GET("/one?test=" +
                                    URLEncoder.encode("one two" + SpincastTestingUtils.TEST_STRING, "UTF-8")).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void fullUrlDecoding() throws Exception {

        getRouter().GET("/${param}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String url = context.request().getFullUrl();
                assertNotNull(url);

                String expected = "https://" + getSpincastConfig().getServerHost() + ":" +
                                  getSpincastConfig().getHttpsServerPort();
                expected += "/one two" + SpincastTestingUtils.TEST_STRING + "?test=" + "one two" +
                            SpincastTestingUtils.TEST_STRING;
                assertEquals(expected, url);
            }
        });

        HttpResponse response = GET("/" +
                                    URLEncoder.encode("one two" + SpincastTestingUtils.TEST_STRING, "UTF-8") +
                                    "?test=" +
                                    URLEncoder.encode("one two" + SpincastTestingUtils.TEST_STRING, "UTF-8")).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
