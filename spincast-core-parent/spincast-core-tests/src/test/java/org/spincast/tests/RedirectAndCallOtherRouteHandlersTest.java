package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.exceptions.RedirectException;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.client.config.RequestConfig;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.common.net.HttpHeaders;

public class RedirectAndCallOtherRouteHandlersTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Test
    public void redirectAbsolutePath() throws Exception {

        getRouter().GET("/permanently/one/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().redirect("/two/three", true);
            }
        });

        getRouter().GET("/permanently/one/two/three").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                throw new RuntimeException("Not here!");
            }
        });

        getRouter().GET("/temporary/one/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().redirect("/two/three", false);
            }
        });

        getRouter().GET("/temporary/one/two/three").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                throw new RuntimeException("Not here!");
            }
        });

        getRouter().GET("/custom/one/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().redirect("/two/three", HttpStatus.SC_SEE_OTHER);
            }
        });

        getRouter().GET("/custom/one/two/three").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                throw new RuntimeException("Not here!");
            }
        });

        getRouter().GET("/two/three").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendHtml(SpincastTestUtils.TEST_STRING);
            }
        });

        RequestConfig noRedirectConfig = RequestConfig.custom().setRedirectsEnabled(false).build();

        //==========================================
        // Without following the permanent redirect
        //==========================================
        IHttpResponse response = GET("/permanently/one/two").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_PERMANENTLY, response.getStatus());
        assertTrue(StringUtils.isBlank(response.getContentAsString()));

        //==========================================
        // Following the permanent redirect
        //==========================================
        response = GET("/permanently/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());

        //==========================================
        // Without following the permanent redirect
        //==========================================
        response = GET("/temporary/one/two").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertTrue(StringUtils.isBlank(response.getContentAsString()));

        //==========================================
        // Following the temporary redirect
        //==========================================
        response = GET("/temporary/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());

        //==========================================
        // Without following the custom redirect
        //==========================================
        response = GET("/custom/one/two").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_SEE_OTHER, response.getStatus());
        assertTrue(StringUtils.isBlank(response.getContentAsString()));

        //==========================================
        // Following the custom redirect
        //==========================================
        response = GET("/custom/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());

    }

    @Test
    public void redirectRelativePath() throws Exception {

        getRouter().GET("/permanently/one/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().redirect("two/three", true);
            }
        });

        getRouter().GET("/permanently/one/two/three").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendHtml("permanently!");
            }
        });

        getRouter().GET("/temporary/one/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().redirect("two/three", false);
            }
        });

        getRouter().GET("/temporary/one/two/three").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendHtml("temporary!");
            }
        });

        getRouter().GET("/custom/one/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().redirect("two/three", HttpStatus.SC_SEE_OTHER);
            }
        });

        getRouter().GET("/custom/one/two/three").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendHtml("custom!");
            }
        });

        getRouter().GET("/two/three").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                throw new RuntimeException("Not here!");
            }
        });

        RequestConfig noRedirectConfig = RequestConfig.custom().setRedirectsEnabled(false).build();

        //==========================================
        // Without following the permanent redirect
        //==========================================
        IHttpResponse response = GET("/permanently/one/two").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_PERMANENTLY, response.getStatus());
        assertTrue(StringUtils.isBlank(response.getContentAsString()));

        //==========================================
        // Following the permanent redirect
        //==========================================
        response = GET("/permanently/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("permanently!", response.getContentAsString());

        //==========================================
        // Without following the permanent redirect
        //==========================================
        response = GET("/temporary/one/two").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertTrue(StringUtils.isBlank(response.getContentAsString()));

        //==========================================
        // Following the temporary redirect
        //==========================================
        response = GET("/temporary/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("temporary!", response.getContentAsString());

        //==========================================
        // Without following the custom redirect
        //==========================================
        response = GET("/custom/one/two").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_SEE_OTHER, response.getStatus());
        assertTrue(StringUtils.isBlank(response.getContentAsString()));

        //==========================================
        // Following the custom redirect
        //==========================================
        response = GET("/custom/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("custom!", response.getContentAsString());

    }

    @Test
    public void redirectDoubleSlashStartingUrl() throws Exception {

        // @see http://stackoverflow.com/a/12840255/843699
        String urlDoubleSlash = createTestUrl("/test");
        assertTrue(urlDoubleSlash.startsWith("http://"));

        final String urlDoubleSlashFinal = urlDoubleSlash.substring("http:".length());

        getRouter().GET("/test").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendHtml(SpincastTestUtils.TEST_STRING);
            }
        });

        getRouter().GET("/permanently/one/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().redirect(urlDoubleSlashFinal, true);
            }
        });

        getRouter().GET("/temporary/one/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().redirect(urlDoubleSlashFinal, false);
            }
        });

        getRouter().GET("/custom/one/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().redirect(urlDoubleSlashFinal, HttpStatus.SC_SEE_OTHER);
            }
        });

        RequestConfig noRedirectConfig = RequestConfig.custom().setRedirectsEnabled(false).build();

        //==========================================
        // Without following the permanent redirect
        //==========================================
        IHttpResponse response = GET("/permanently/one/two").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_PERMANENTLY, response.getStatus());
        assertTrue(StringUtils.isBlank(response.getContentAsString()));

        //==========================================
        // Following the permanent redirect
        //==========================================
        response = GET("/permanently/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());

        //==========================================
        // Without following the permanent redirect
        //==========================================
        response = GET("/temporary/one/two").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertTrue(StringUtils.isBlank(response.getContentAsString()));

        //==========================================
        // Following the temporary redirect
        //==========================================
        response = GET("/temporary/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());

        //==========================================
        // Without following the custom redirect
        //==========================================
        response = GET("/custom/one/two").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_SEE_OTHER, response.getStatus());
        assertTrue(StringUtils.isBlank(response.getContentAsString()));

        //==========================================
        // Following the custom redirect
        //==========================================
        response = GET("/custom/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());

    }

    @Test
    public void useAnotherRoutehandler() throws Exception {

        final IHandler<IDefaultRequestContext> handler2 = new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendPlainText("2");
            }
        };

        getRouter().ALL("/*{path}").pos(-1).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendPlainText("B");
            }
        });

        // Use handler2!
        getRouter().ALL("/*{path}").pos(1).save(handler2);

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendPlainText("1");

                // Use handler2!
                handler2.handle(spincast);

                spincast.response().sendPlainText("3");
            }
        });

        IHttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("B1232", response.getContentAsString());
    }

    @Test
    public void standardRedirection() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().redirect("/two", false);
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendPlainText("two");
            }
        });

        IHttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void redirectException() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                throw new RedirectException("/two", false);
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendPlainText("two");
            }
        });

        IHttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void standardRedirectionDoesntSkipRemainingFilters() throws Exception {

        getRouter().after().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendPlainText("after");
            }
        });

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendPlainText("main");
                spincast.response().redirect("/test", false);
            }
        });

        RequestConfig noRedirectConfig = RequestConfig.custom().setRedirectsEnabled(false).build();

        IHttpResponse response = GET("/").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("mainafter", response.getContentAsString());
    }

    @Test
    public void standardRedirectCanBeCancelled() throws Exception {

        getRouter().after().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {

                spincast.response().setStatusCode(HttpStatus.SC_OK);
                spincast.response().removeHeader(HttpHeaders.LOCATION);
                spincast.response().sendPlainText("after");
            }
        });

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendPlainText("main");
                spincast.response().redirect("/test", false);
            }
        });

        IHttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("mainafter", response.getContentAsString());
    }

    @Test
    public void redirectExceptionDoesSkipRemainingFilters() throws Exception {

        getRouter().after().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendPlainText("after");
            }
        });

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendPlainText("nope");
                throw new RedirectException("/test", true);
            }
        });

        RequestConfig noRedirectConfig = RequestConfig.custom().setRedirectsEnabled(false).build();

        IHttpResponse response = GET("/").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_PERMANENTLY, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void redirectExceptionTemporarily() throws Exception {

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext spincast) {
                spincast.response().sendPlainText("nope");
                throw new RedirectException("/test", false);
            }
        });

        RequestConfig noRedirectConfig = RequestConfig.custom().setRedirectsEnabled(false).build();

        IHttpResponse response = GET("/").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("", response.getContentAsString());
    }

}
