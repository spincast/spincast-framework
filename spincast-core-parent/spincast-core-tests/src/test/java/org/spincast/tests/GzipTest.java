package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.GzipOption;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class GzipTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Test
    public void gzipped() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING_LONG);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertTrue(response.isGzipped());
    }

    @Test
    public void notGzipped() throws Exception {

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

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertFalse(response.isGzipped());
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

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertFalse(response.isGzipped());
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

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertTrue(response.isGzipped());
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

        IHttpResponse response = GET("/one.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertTrue(response.isGzipped());
    }

    @Test
    public void resourceGzipped() throws Exception {

        getRouter().file("/txt").classpath("/someFile.txt").save();

        IHttpResponse response = GET("/txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertTrue(response.isGzipped());
    }

    @Test
    public void resourceNotGzipped() throws Exception {

        getRouter().file("/image").classpath("/image.jpg").save();

        IHttpResponse response = GET("/image").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertFalse(response.isGzipped());
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

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertFalse(response.isGzipped());
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

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertFalse(response.isGzipped());
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

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertTrue(response.isGzipped());
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

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertFalse(response.isGzipped());
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

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertFalse(response.isGzipped());
    }
}
