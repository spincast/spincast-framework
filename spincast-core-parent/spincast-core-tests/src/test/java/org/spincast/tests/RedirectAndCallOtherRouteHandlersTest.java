package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.exceptions.RedirectException;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.flash.FlashMessage;
import org.spincast.core.flash.FlashMessageLevel;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.client.config.RequestConfig;
import org.spincast.testing.core.utils.SpincastTestingUtils;

import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;

public class RedirectAndCallOtherRouteHandlersTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected JsonManager jsonManager;

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Test
    public void redirectAbsolutePath() throws Exception {

        getRouter().GET("/permanently/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().redirect("/two/three", true);
            }
        });

        getRouter().GET("/permanently/one/two/three").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                throw new RuntimeException("Not here!");
            }
        });

        getRouter().GET("/temporary/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().redirect("/two/three", false);
            }
        });

        getRouter().GET("/temporary/one/two/three").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                throw new RuntimeException("Not here!");
            }
        });

        getRouter().GET("/custom/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().redirect("/two/three", HttpStatus.SC_SEE_OTHER);
            }
        });

        getRouter().GET("/custom/one/two/three").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                throw new RuntimeException("Not here!");
            }
        });

        getRouter().GET("/two/three").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendHtml(SpincastTestingUtils.TEST_STRING);
            }
        });

        RequestConfig noRedirectConfig = RequestConfig.custom().setRedirectsEnabled(false).build();

        //==========================================
        // Without following the permanent redirect
        //==========================================
        HttpResponse response = GET("/permanently/one/two").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_PERMANENTLY, response.getStatus());
        assertTrue(StringUtils.isBlank(response.getContentAsString()));

        //==========================================
        // Following the permanent redirect
        //==========================================
        response = GET("/permanently/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());

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
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());

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
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());

    }

    @Test
    public void redirectRelativePath() throws Exception {

        getRouter().GET("/permanently/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().redirect("two/three", true);
            }
        });

        getRouter().GET("/permanently/one/two/three").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendHtml("permanently!");
            }
        });

        getRouter().GET("/temporary/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().redirect("two/three", false);
            }
        });

        getRouter().GET("/temporary/one/two/three").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendHtml("temporary!");
            }
        });

        getRouter().GET("/custom/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().redirect("two/three", HttpStatus.SC_SEE_OTHER);
            }
        });

        getRouter().GET("/custom/one/two/three").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendHtml("custom!");
            }
        });

        getRouter().GET("/two/three").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                throw new RuntimeException("Not here!");
            }
        });

        RequestConfig noRedirectConfig = RequestConfig.custom().setRedirectsEnabled(false).build();

        //==========================================
        // Without following the permanent redirect
        //==========================================
        HttpResponse response = GET("/permanently/one/two").setRequestConfig(noRedirectConfig).send();
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

        getRouter().GET("/test").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendHtml(SpincastTestingUtils.TEST_STRING);
            }
        });

        getRouter().GET("/permanently/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().redirect(urlDoubleSlashFinal, true);
            }
        });

        getRouter().GET("/temporary/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().redirect(urlDoubleSlashFinal, false);
            }
        });

        getRouter().GET("/custom/one/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().redirect(urlDoubleSlashFinal, HttpStatus.SC_SEE_OTHER);
            }
        });

        RequestConfig noRedirectConfig = RequestConfig.custom().setRedirectsEnabled(false).build();

        //==========================================
        // Without following the permanent redirect
        //==========================================
        HttpResponse response = GET("/permanently/one/two").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_PERMANENTLY, response.getStatus());
        assertTrue(StringUtils.isBlank(response.getContentAsString()));

        //==========================================
        // Following the permanent redirect
        //==========================================
        response = GET("/permanently/one/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());

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
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());

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
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());

    }

    @Test
    public void useAnotherRoutehandler() throws Exception {

        final Handler<DefaultRequestContext> handler2 = new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendPlainText("2");
            }
        };

        getRouter().ALL("/*{path}").pos(-1).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendPlainText("B");
            }
        });

        // Use handler2!
        getRouter().ALL("/*{path}").pos(1).handle(handler2);

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendPlainText("1");

                // Use handler2!
                handler2.handle(spincast);

                spincast.response().sendPlainText("3");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("B1232", response.getContentAsString());
    }

    @Test
    public void standardRedirection() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().redirect("/two", false);
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendPlainText("two");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void redirectException() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                throw new RedirectException("/two", false);
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendPlainText("two");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    /**
     * An empty URL redirect to the same page
     */
    @Test
    public void redirectEmpty() throws Exception {

        final boolean[] redirected = new boolean[]{false};

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if (!redirected[0]) {
                    redirected[0] = true;
                    context.response().redirect();
                    return;
                }
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());

        assertTrue(redirected[0]);
    }

    /**
     * An queryString only URL redirect to the same page
     */
    @Test
    public void redirectEmptyWithQueryString() throws Exception {

        final boolean[] redirected = new boolean[]{false};

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if (!redirected[0]) {
                    assertNull(context.request().getQueryStringParamFirst("titi"));
                    redirected[0] = true;
                    context.response().redirect("?titi=toto");
                    return;
                }
                assertEquals("toto", context.request().getQueryStringParamFirst("titi"));
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());

        assertTrue(redirected[0]);
    }

    /**
     * An anchor only URL redirect to the same page
     */
    @Test
    public void redirectEmptyWithAnchor() throws Exception {

        final boolean[] redirected = new boolean[]{false};

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if (!redirected[0]) {
                    redirected[0] = true;
                    context.response().redirect("#anchor");
                    return;
                }
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());

        assertTrue(redirected[0]);
    }

    /**
     * An empty URL redirect to the same page
     */
    @Test
    public void redirectExceptionEmpty() throws Exception {

        final boolean[] redirected = new boolean[]{false};

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if (!redirected[0]) {
                    redirected[0] = true;
                    throw new RedirectException("", false);
                }
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());

        assertTrue(redirected[0]);
    }

    @Test
    public void standardRedirectionDoesntSkipRemainingFilters() throws Exception {

        getRouter().ALL().pos(10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendPlainText("after");
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendPlainText("main");
                spincast.response().redirect("/test", false);
            }
        });

        RequestConfig noRedirectConfig = RequestConfig.custom().setRedirectsEnabled(false).build();

        HttpResponse response = GET("/").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("mainafter", response.getContentAsString());
    }

    @Test
    public void standardRedirectCanBeCancelled() throws Exception {

        getRouter().ALL().pos(10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {

                spincast.response().setStatusCode(HttpStatus.SC_OK);
                spincast.response().removeHeader(HttpHeaders.LOCATION);
                spincast.response().sendPlainText("after");
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendPlainText("main");
                spincast.response().redirect("/test", false);
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("mainafter", response.getContentAsString());
    }

    @Test
    public void redirectExceptionSkipRemainingBeforeFiltersAndMainHandler() throws Exception {

        final boolean[] wasCalled1 = new boolean[]{false};
        final boolean[] wasCalled2 = new boolean[]{false};
        final boolean[] wasCalled3 = new boolean[]{false};
        final boolean[] wasCalled4 = new boolean[]{false};
        final boolean[] wasCalled5 = new boolean[]{false};
        final boolean[] wasCalled6 = new boolean[]{false};

        getRouter().ALL().pos(-100).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                wasCalled1[0] = true;
            }
        });

        getRouter().ALL().pos(-90).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                wasCalled2[0] = true;
                throw new RedirectException("/test", true);
            }
        });

        getRouter().ALL().pos(-80).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                wasCalled3[0] = true;
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                wasCalled4[0] = true;
            }
        });

        getRouter().ALL().pos(10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                wasCalled5[0] = true;
            }
        });

        getRouter().ALL().pos(20).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                wasCalled6[0] = true;
            }
        });

        RequestConfig noRedirectConfig = RequestConfig.custom().setRedirectsEnabled(false).build();

        HttpResponse response = GET("/").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_PERMANENTLY, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("", response.getContentAsString());
        assertTrue(wasCalled1[0]);
        assertTrue(wasCalled2[0]);
        assertFalse(wasCalled3[0]);
        assertFalse(wasCalled4[0]);

        //==========================================
        // The response is sent before the after
        // filters are called...
        //==========================================
        Thread.sleep(1000);

        assertTrue(wasCalled5[0]);
        assertTrue(wasCalled6[0]);
    }

    @Test
    public void redirectExceptionDoesNotSkipRemainingAfterFilters() throws Exception {

        final boolean[] wasCalled = new boolean[]{false};
        getRouter().ALL().pos(10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                wasCalled[0] = true;
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendPlainText("nope");
                throw new RedirectException("/test", true);
            }
        });

        RequestConfig noRedirectConfig = RequestConfig.custom().setRedirectsEnabled(false).build();

        HttpResponse response = GET("/").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_PERMANENTLY, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("", response.getContentAsString());

        //==========================================
        // The response is sent before the after
        // filters are called...
        //==========================================
        Thread.sleep(1000);

        assertTrue(wasCalled[0]);
    }

    @Test
    public void redirectExceptionTemporarily() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext spincast) {
                spincast.response().sendPlainText("nope");
                throw new RedirectException("/test", false);
            }
        });

        RequestConfig noRedirectConfig = RequestConfig.custom().setRedirectsEnabled(false).build();

        HttpResponse response = GET("/").setRequestConfig(noRedirectConfig).send();
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void redirectWithFlashMessage() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().redirect("/two", FlashMessageLevel.SUCCESS, "my flash message");
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                FlashMessage flashMessage = context.request().getFlashMessage();
                assertNotNull(flashMessage);
                assertEquals(FlashMessageLevel.SUCCESS, flashMessage.getFlashType());
                assertEquals("my flash message", flashMessage.getText());
                context.response().sendPlainText("two");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void redirectExceptionWithFlashMessage() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RedirectException("/two", FlashMessageLevel.SUCCESS, "my flash message");
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                FlashMessage flashMessage = context.request().getFlashMessage();
                assertNotNull(flashMessage);
                assertEquals(FlashMessageLevel.SUCCESS, flashMessage.getFlashType());
                assertEquals("my flash message", flashMessage.getText());
                context.response().sendPlainText("two");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("two", response.getContentAsString());
    }

    @Test
    public void redirectWithFlashMessageAutomaticallyAddedToAlerts() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().redirect("/two", FlashMessageLevel.SUCCESS, "my flash message");
            }
        });

        getRouter().GET("/two").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().getModel().set("key1", "val1");
                context.response().sendJson();
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());

        String json = response.getContentAsString();

        String spincastModelRootVariableName = getSpincastConfig().getSpincastModelRootVariableName();

        JsonObject jsonObj = getJsonManager().fromString(json);
        assertEquals("val1", jsonObj.getString("key1"));
        assertEquals("my flash message", jsonObj.getString(spincastModelRootVariableName + ".alerts[0].text"));
        assertEquals("SUCCESS", jsonObj.getString(spincastModelRootVariableName + ".alerts[0].alertType.name"));
    }

}
