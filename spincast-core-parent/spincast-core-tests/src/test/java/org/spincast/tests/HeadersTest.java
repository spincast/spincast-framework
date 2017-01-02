package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpHeaders;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class HeadersTest extends IntegrationTestNoAppDefaultContextsBase {

    @Test
    public void requestHeaders() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    Map<String, List<String>> requestHeaders = context.request().getHeaders();
                    assertNotNull(requestHeaders);

                    List<String> vals = requestHeaders.get("header1");
                    assertEquals(1, vals.size());
                    assertEquals("val1 val2", vals.get(0));

                    vals = requestHeaders.get("header2");
                    assertEquals(1, vals.size());
                    assertEquals(SpincastTestUtils.TEST_STRING, URLDecoder.decode(vals.get(0), "UTF-8"));

                    vals = context.request().getHeader("header1");
                    assertEquals(1, vals.size());
                    assertEquals("val1 val2", vals.get(0));

                    String headerFirstValue = context.request().getHeaderFirst("header2");
                    assertEquals(SpincastTestUtils.TEST_STRING, URLDecoder.decode(headerFirstValue, "UTF-8"));

                    String insensitive = context.request().getHeaderFirst("HEADer1");
                    assertEquals("val1 val2", insensitive);

                    // Added by Apache HttpClient
                    headerFirstValue = context.request().getHeaderFirst(HttpHeaders.ACCEPT_ENCODING);
                    assertNotNull(headerFirstValue);

                    // immutable
                    try {
                        requestHeaders.remove("HEADer1");
                        fail();
                    } catch(UnsupportedOperationException ex) {
                    }

                    try {
                        requestHeaders.put("HEADer3", Arrays.asList("val3"));
                        fail();
                    } catch(UnsupportedOperationException ex) {
                    }

                    headerFirstValue = context.request().getHeaderFirst("HEADer1");
                    assertNotNull(headerFirstValue);

                    headerFirstValue = context.request().getHeaderFirst("HEADer3");
                    assertNull(headerFirstValue);

                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").addHeaderValue("header1", "val1 val2")
                                           .addHeaderValue("header2",
                                                           URLEncoder.encode(SpincastTestUtils.TEST_STRING, "UTF-8"))
                                           .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void responseHeadersMutable() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    Map<String, List<String>> responseHeaders = context.response().getHeaders();
                    assertNotNull(responseHeaders);
                    assertEquals(0, responseHeaders.size());

                    // mutable
                    responseHeaders.put("header1", Arrays.asList("val1", "val2"));
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        List<String> vals = response.getHeader("header1");
        assertNotNull(vals);
        assertEquals(2, vals.size());
        assertEquals("val1", vals.get(0));
        assertEquals("val2", vals.get(1));

    }

    @Test
    public void responseHeadersResetEverything() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    context.response().addHeaderValue("header1", "val1");
                    context.response().addHeaderValue("header1", "val2");

                    List<String> vals = context.response().getHeader("header1");
                    assertNotNull(vals);
                    assertEquals(2, vals.size());

                    context.response().resetEverything();

                    vals = context.response().getHeader("header1");
                    assertNotNull(vals);
                    assertEquals(0, vals.size());

                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        List<String> vals = response.getHeader("header1");
        assertNotNull(vals);
        assertEquals(0, vals.size());

    }

    @Test
    public void responseHeadersAdd() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    List<String> vals = context.response().getHeader("header1");
                    assertNotNull(vals);
                    assertEquals(0, vals.size());

                    context.response().addHeaderValue("header1", "val1");
                    context.response().addHeaderValue("header1", "val2");

                    vals = context.response().getHeader("header1");
                    assertNotNull(vals);
                    assertEquals(2, vals.size());
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        List<String> vals = response.getHeader("header1");
        assertNotNull(vals);
        assertEquals(2, vals.size());
        assertEquals("val1", vals.get(0));
        assertEquals("val2", vals.get(1));
    }

    @Test
    public void responseHeadersAddValues() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    List<String> vals = context.response().getHeader("header1");
                    assertNotNull(vals);
                    assertEquals(0, vals.size());

                    context.response().addHeaderValues("header1", Arrays.asList("val1", "val2"));

                    vals = context.response().getHeader("header1");
                    assertNotNull(vals);
                    assertEquals(2, vals.size());
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        List<String> vals = response.getHeader("header1");
        assertNotNull(vals);
        assertEquals(2, vals.size());
        assertEquals("val1", vals.get(0));
        assertEquals("val2", vals.get(1));
    }

    @Test
    public void responseHeadersSet() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    context.response().addHeaderValues("header1", Arrays.asList("val1", "val2"));

                    context.response().setHeader("HEAder1", "val3");

                    List<String> vals = context.response().getHeader("header1");
                    assertNotNull(vals);
                    assertEquals(1, vals.size());
                    assertEquals("val3", vals.get(0));
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        List<String> vals = response.getHeader("header1");
        assertNotNull(vals);
        assertEquals(1, vals.size());
        assertEquals("val3", vals.get(0));
    }

    @Test
    public void responseHeadersRemove() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    context.response().addHeaderValues("header1", Arrays.asList("val1", "val2"));

                    context.response().removeHeader("HEAder1");

                    List<String> vals = context.response().getHeader("header1");
                    assertNotNull(vals);
                    assertEquals(0, vals.size());
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        List<String> vals = response.getHeader("header1");
        assertNotNull(vals);
        assertEquals(0, vals.size());
    }

    @Test
    public void responseHeadersRemoveWithSetNull() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    context.response().addHeaderValues("header1", Arrays.asList("val1", "val2"));

                    context.response().setHeader("HEAder1", (String)null);

                    List<String> vals = context.response().getHeader("header1");
                    assertNotNull(vals);
                    assertEquals(0, vals.size());
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        List<String> vals = response.getHeader("header1");
        assertNotNull(vals);
        assertEquals(0, vals.size());
    }

    @Test
    public void responseHeadersGetFirstValue() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    context.response().addHeaderValues("header1", Arrays.asList("val1", "val2"));

                    String headerFirstValue = context.response().getHeaderFirst("HEAder1");
                    assertNotNull(headerFirstValue);
                    assertEquals("val1", headerFirstValue);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        List<String> vals = response.getHeader("header1");
        assertNotNull(vals);
        assertEquals(2, vals.size());
        assertEquals("val1", vals.get(0));
        assertEquals("val2", vals.get(1));
    }

    @Test
    public void responseHeadersFlush() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    context.response().addHeaderValues("header1", Arrays.asList("val1", "val2"));

                    context.response().flush();

                    // Does nothing, headers sent!
                    context.response().removeHeader("header1");
                    context.response().setHeader("header1", Arrays.asList("val3"));
                    context.response().setHeader("header2", Arrays.asList("val3"));

                    List<String> vals = context.response().getHeader("header1");
                    assertNotNull(vals);
                    assertEquals(2, vals.size());

                    vals = context.response().getHeader("header2");
                    assertNotNull(vals);
                    assertEquals(0, vals.size());

                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        List<String> vals = response.getHeader("header1");
        assertNotNull(vals);
        assertEquals(2, vals.size());
        assertEquals("val1", vals.get(0));
        assertEquals("val2", vals.get(1));
    }

}
