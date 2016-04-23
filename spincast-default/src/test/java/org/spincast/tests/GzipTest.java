package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.GzipOption;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.http.Header;
import org.spincast.shaded.org.apache.http.HttpHeaders;
import org.spincast.shaded.org.apache.http.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.client.entity.DecompressingEntity;
import org.spincast.shaded.org.apache.http.util.EntityUtils;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class GzipTest extends DefaultIntegrationTestingBase {

    @Test
    public void gziped() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING_LONG);
            }
        });

        HttpResponse response = getRawResponse(createTestUrl("/one"));
        try {
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            // DecompressingEntity from HttpClient means the response was compressed!
            assertTrue(response.getEntity() instanceof DecompressingEntity);

            //==========================================
            // Apache HttpClient removes the "Content-Encoding" header!
            //==========================================
            //String header = response.getHeaderFirst(HttpHeaders.CONTENT_ENCODING);
            //assertNotNull(header);
            //assertTrue(header.toLowerCase().contains("gzip"));

            Header header = response.getFirstHeader(HttpHeaders.TRANSFER_ENCODING);
            assertNotNull(header);
            assertTrue(header.getValue().toLowerCase().contains("chunked"));
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    @Test
    public void notGziped() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                try {
                    context.response().sendBytes("As bytes".getBytes("UTF-8"));
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = getRawResponse(createTestUrl("/one"));
        try {
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            // DecompressingEntity from HttpClient means the response was compressed!
            assertFalse(response.getEntity() instanceof DecompressingEntity);

            Header header = response.getFirstHeader(HttpHeaders.TRANSFER_ENCODING);
            assertNull(header);
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    @Test
    public void unknowContentType() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                try {
                    context.response().sendBytes("As bytes".getBytes("UTF-8"));
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = getRawResponse(createTestUrl("/one"));
        try {
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            // DecompressingEntity from HttpClient means the response was compressed!
            assertFalse(response.getEntity() instanceof DecompressingEntity);

            Header header = response.getFirstHeader(HttpHeaders.TRANSFER_ENCODING);
            assertNull(header);
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    @Test
    public void changeContentType() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                try {
                    context.response().sendBytes("As bytes".getBytes("UTF-8"));
                    context.response().sendPlainText("text");
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = getRawResponse(createTestUrl("/one"));
        try {
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            // DecompressingEntity from HttpClient means the response was compressed!
            assertTrue(response.getEntity() instanceof DecompressingEntity);

            Header header = response.getFirstHeader(HttpHeaders.TRANSFER_ENCODING);
            assertNotNull(header);
            assertTrue(header.getValue().toLowerCase().contains("chunked"));
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    @Test
    public void guessingContentType() throws Exception {

        getRouter().GET("/one.txt").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                try {
                    context.response().sendBytes("As bytes".getBytes("UTF-8"));
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = getRawResponse(createTestUrl("/one.txt"));
        try {
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            // DecompressingEntity from HttpClient means the response was compressed!
            assertTrue(response.getEntity() instanceof DecompressingEntity);

            Header header = response.getFirstHeader(HttpHeaders.TRANSFER_ENCODING);
            assertNotNull(header);
            assertTrue(header.getValue().toLowerCase().contains("chunked"));
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    @Test
    public void resourceGziped() throws Exception {

        getRouter().file("/txt").classpath("/someFile.txt").save();

        HttpResponse response = getRawResponse(createTestUrl("/txt"));
        try {
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            // DecompressingEntity from HttpClient means the response was compressed!
            assertTrue(response.getEntity() instanceof DecompressingEntity);
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    @Test
    public void resourceNotGziped() throws Exception {

        getRouter().file("/image").classpath("/image.jpg").save();

        HttpResponse response = getRawResponse(createTestUrl("/image"));
        try {
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            // DecompressingEntity from HttpClient means the response was compressed!
            assertTrue(response.getEntity() instanceof DecompressingEntity);
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }

    }

    @Test
    public void disableGzip() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().setGzipOption(GzipOption.DISABLE);
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING_LONG);
            }
        });

        HttpResponse response = getRawResponse(createTestUrl("/one"));
        try {
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            // DecompressingEntity from HttpClient means the response was compressed!
            assertFalse(response.getEntity() instanceof DecompressingEntity);
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    @Test
    public void disableGzipInFilter() throws Exception {

        IHandler<IDefaultRequestContext> noGzip = new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().setGzipOption(GzipOption.DISABLE);
            }
        };

        getRouter().GET("/one").before(noGzip).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        HttpResponse response = getRawResponse(createTestUrl("/one"));
        try {
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            // DecompressingEntity from HttpClient means the response was compressed!
            assertFalse(response.getEntity() instanceof DecompressingEntity);
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    @Test
    public void forceGzip() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().setGzipOption(GzipOption.FORCE);
                context.response().sendBytes(new byte[0], ContentTypeDefaults.BINARY.getMainVariation());
            }
        });

        HttpResponse response = getRawResponse(createTestUrl("/one"));
        try {
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            // DecompressingEntity from HttpClient means the response was compressed!
            assertTrue(response.getEntity() instanceof DecompressingEntity);
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    @Test
    public void defaultGzip() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().setGzipOption(GzipOption.DEFAULT);
                context.response().sendBytes(new byte[0], ContentTypeDefaults.BINARY.getMainVariation());
            }
        });

        HttpResponse response = getRawResponse(createTestUrl("/one"));
        try {
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            // DecompressingEntity from HttpClient means the response was compressed!
            assertFalse(response.getEntity() instanceof DecompressingEntity);
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    @Test
    public void headersSent() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertEquals(GzipOption.DEFAULT, context.response().getGzipOption());

                context.response().setGzipOption(GzipOption.DISABLE);
                assertEquals(GzipOption.DISABLE, context.response().getGzipOption());

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING, true);

                context.response().setGzipOption(GzipOption.FORCE);
                assertEquals(GzipOption.DISABLE, context.response().getGzipOption());
            }
        });

        HttpResponse response = getRawResponse(createTestUrl("/one"));
        try {
            assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            // DecompressingEntity from HttpClient means the response was compressed!
            assertFalse(response.getEntity() instanceof DecompressingEntity);
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }
}
