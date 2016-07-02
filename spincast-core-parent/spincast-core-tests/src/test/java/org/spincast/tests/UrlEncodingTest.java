package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URLEncoder;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class UrlEncodingTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Test
    public void pathParamDecoding() throws Exception {

        getRouter().GET("/${param}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String param = context.request().getPathParam("param");
                assertNotNull(param);
                assertEquals("one two" + SpincastTestUtils.TEST_STRING, param);
            }
        });

        IHttpResponse response = GET("/" +
                                     URLEncoder.encode("one two" + SpincastTestUtils.TEST_STRING, "UTF-8")).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void queryStringDecoding() throws Exception {

        getRouter().GET("/${param}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String queryString = context.request().getQueryString();
                assertNotNull(queryString);
                assertEquals("test=one two" + SpincastTestUtils.TEST_STRING, queryString);
            }
        });

        IHttpResponse response = GET("/one?test=" +
                                     URLEncoder.encode("one two" + SpincastTestUtils.TEST_STRING, "UTF-8")).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void queryStringParamDecoding() throws Exception {

        getRouter().GET("/${param}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String param = context.request().getQueryStringParamFirst("test");
                assertNotNull(param);
                assertEquals("one two" + SpincastTestUtils.TEST_STRING, param);
            }
        });

        IHttpResponse response = GET("/one?test=" +
                                     URLEncoder.encode("one two" + SpincastTestUtils.TEST_STRING, "UTF-8")).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void fullUrlDecoding() throws Exception {

        getRouter().GET("/${param}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String url = context.request().getFullUrl();
                assertNotNull(url);

                String expected = "http://" + getSpincastConfig().getServerHost() + ":" +
                                  getSpincastConfig().getHttpServerPort();
                expected += "/one two" + SpincastTestUtils.TEST_STRING + "?test=" + "one two" +
                            SpincastTestUtils.TEST_STRING;
                assertEquals(expected, url);
            }
        });

        IHttpResponse response = GET("/" +
                                     URLEncoder.encode("one two" + SpincastTestUtils.TEST_STRING, "UTF-8") +
                                     "?test=" +
                                     URLEncoder.encode("one two" + SpincastTestUtils.TEST_STRING, "UTF-8")).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
