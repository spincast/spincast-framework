package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.GzipOption;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;

public class GzipTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void gzipped() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING_LONG);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertTrue(response.isGzipped());
    }

    @Test
    public void notGzipped() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                try {
                    context.response().sendBytes("As bytes".getBytes("UTF-8"));
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertFalse(response.isGzipped());
    }

    @Test
    public void unknowContentType() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                try {
                    context.response().sendBytes("As bytes".getBytes("UTF-8"));
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertFalse(response.isGzipped());
    }

    @Test
    public void changeContentType() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                try {
                    context.response().sendBytes("As bytes".getBytes("UTF-8"));
                    context.response().sendPlainText("text");
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertTrue(response.isGzipped());
    }

    @Test
    public void guessingContentType() throws Exception {

        getRouter().GET("/one.txt").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                try {
                    context.response().sendBytes("As bytes".getBytes("UTF-8"));
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one.txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertTrue(response.isGzipped());
    }

    @Test
    public void resourceGzipped() throws Exception {

        getRouter().file("/txt").classpath("/someFile.txt").handle();

        HttpResponse response = GET("/txt").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertTrue(response.isGzipped());
    }

    @Test
    public void resourceNotGzipped() throws Exception {

        getRouter().file("/image").classpath("/image.jpg").handle();

        HttpResponse response = GET("/image").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertFalse(response.isGzipped());
    }

    @Test
    public void disableGzip() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().setGzipOption(GzipOption.DISABLE);
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING_LONG);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertFalse(response.isGzipped());
    }

    @Test
    public void disableGzipInFilter() throws Exception {

        Handler<DefaultRequestContext> noGzip = new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().setGzipOption(GzipOption.DISABLE);
            }
        };

        getRouter().GET("/one").before(noGzip).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertFalse(response.isGzipped());
    }

    @Test
    public void forceGzip() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().setGzipOption(GzipOption.FORCE);
                context.response().sendBytes(new byte[0], ContentTypeDefaults.BINARY.getMainVariation());
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertTrue(response.isGzipped());
    }

    @Test
    public void defaultGzip() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().setGzipOption(GzipOption.DEFAULT);
                context.response().sendBytes(new byte[0], ContentTypeDefaults.BINARY.getMainVariation());
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertFalse(response.isGzipped());
    }

    @Test
    public void headersSent() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals(GzipOption.DEFAULT, context.response().getGzipOption());

                context.response().setGzipOption(GzipOption.DISABLE);
                assertEquals(GzipOption.DISABLE, context.response().getGzipOption());

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING, true);

                context.response().setGzipOption(GzipOption.FORCE);
                assertEquals(GzipOption.DISABLE, context.response().getGzipOption());
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertFalse(response.isGzipped());
    }
}
