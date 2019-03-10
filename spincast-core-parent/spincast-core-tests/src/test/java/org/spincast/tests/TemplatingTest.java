package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Handler;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;

/**
 * There are some tests we can't do here because we don't know
 * the implementation of the templating engine that is going to be used.
 * <p>
 * Depending on the implementation, the placeholders format may not
 * be the same: "${name}" vs "{{name}}", for example.
 * </p>
 * <p>
 * So one limitation for the tests here is that we can't directly
 * use a file on the classpath, we have to generate the content
 * to be parsed by ourself and make sure the correct placeholders
 * format is used.
 * </p>
 * <p>
 * For tests specfic to an implementation, checs the implementation
 * plugin's project!
 * </p>
 */
public class TemplatingTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected TemplatingEngine templatingEngine;

    @Inject
    protected JsonManager jsonManager;

    @Inject
    protected SpincastUtils spincastUtils;

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    @Test
    public void htmlTemplate() throws Exception {

        final File testFile = new File(createTestingFilePath());
        String placeholder = this.templatingEngine.createPlaceholder("param1");
        FileUtils.writeStringToFile(testFile, "<p>test : " + placeholder + "</p>", "UTF-8");

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response().getModel().set("param1", "Hello!");

                context.response().sendTemplateHtml(testFile.getAbsolutePath(), false);
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<p>test : Hello!</p>", response.getContentAsString());
    }

    @Test
    public void genericTemplate() throws Exception {

        final File testFile = new File(createTestingFilePath());
        String placeholder = this.templatingEngine.createPlaceholder("fontPxSize");
        FileUtils.writeStringToFile(testFile, "body {font-size : " + placeholder + "px;}", "UTF-8");

        getRouter().GET("/test.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response().getModel().set("fontPxSize", 16);

                context.response().sendTemplate(testFile.getAbsolutePath(), false, "text/css");
            }
        });

        HttpResponse response = GET("/test.css").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("body {font-size : 16px;}", response.getContentAsString());
    }

    @Test
    public void resourceUsingTemplate() throws Exception {

        final File testFile = new File(createTestingFilePath());
        String placeholder = this.templatingEngine.createPlaceholder("fontPxSize");
        FileUtils.writeStringToFile(testFile, "body {font-size : " + placeholder + "px;}", "UTF-8");

        String generatedFilePath = getTestingWritableTempDir().getAbsolutePath() + "/generated.css";

        final int[] nbrTimeCalled = new int[]{0};

        //==========================================
        // When using router.file(...) and a generator, the generated
        // resource will automatically be saved.
        //==========================================
        getRouter().file("/test.css").pathAbsolute(generatedFilePath).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                nbrTimeCalled[0]++;

                context.response().getModel().set("fontPxSize", 16);

                context.response().sendTemplate(testFile.getAbsolutePath(), false, "text/css");
            }
        });

        HttpResponse response = GET("/test.css").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("body {font-size : 16px;}", response.getContentAsString());
        assertEquals(1, nbrTimeCalled[0]);

        response = GET("/test.css").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("body {font-size : 16px;}", response.getContentAsString());

        // Still 1!
        assertEquals(1, nbrTimeCalled[0]);
    }

    @Test
    public void evaluateMap() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Stromgol");

        String placeholder = this.templatingEngine.createPlaceholder("name");
        String result = this.templatingEngine.evaluate("Hello " + placeholder, params);
        assertNotNull(result);
        assertEquals("Hello Stromgol", result);
    }

    @Test
    public void evaluateJsonObject() throws Exception {

        JsonObject jsonObj = this.jsonManager.create();
        jsonObj.set("name", "Stromgol");

        String placeholder = this.templatingEngine.createPlaceholder("name");
        String result = this.templatingEngine.evaluate("Hello " + placeholder, jsonObj);
        assertNotNull(result);
        assertEquals("Hello Stromgol", result);
    }

    @Test
    public void fromTemplateMap() throws Exception {

        final File testFile = new File(createTestingFilePath());
        String placeholder = this.templatingEngine.createPlaceholder("param1");
        FileUtils.writeStringToFile(testFile, "<p>test : " + placeholder + "</p>", "UTF-8");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("param1", "Stromgol");

        String result = this.templatingEngine.fromTemplate(testFile.getAbsolutePath(), false, params);
        assertNotNull(result);
        assertEquals("<p>test : Stromgol</p>", result);
    }

    @Test
    public void fromTemplateJsonObject() throws Exception {

        final File testFile = new File(createTestingFilePath());
        String placeholder = this.templatingEngine.createPlaceholder("param1");
        FileUtils.writeStringToFile(testFile, "<p>test : " + placeholder + "</p>", "UTF-8");

        JsonObject jsonObj = this.jsonManager.create();
        jsonObj.set("param1", "Stromgol");

        String result = this.templatingEngine.fromTemplate(testFile.getAbsolutePath(), false, jsonObj);
        assertNotNull(result);
        assertEquals("<p>test : Stromgol</p>", result);
    }

    @Test
    public void defaultTemplateVariables() throws Exception {

        getRouter().GET("/one/${param1}").id("test").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.variables().set("oneVar", "oneVal");

                Map<String, Object> varsRoot = context.templating().getTemplatingGlobalVariables();
                assertNotNull(varsRoot);

                @SuppressWarnings("unchecked")
                Map<String, Object> vars = (Map<String, Object>)varsRoot.get("spincast");
                assertNotNull(vars);

                assertEquals("en",
                             vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_LANG_ABREV));
                assertEquals(getSpincastUtils().getSpincastCurrentVersion(),
                             vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_SPINCAST_CURRENT_VERSION));

                assertNotNull(vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_SPINCAST_CURRENT_VERSION_IS_SNAPSHOT));

                assertEquals(getSpincastUtils().getCacheBusterCode(),
                             vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_CACHE_BUSTER));

                assertNotNull(getSpincastUtils().getCacheBusterCode(),
                              vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_REQUEST_SCOPED_VARIABLES));

                @SuppressWarnings("unchecked")
                Map<String, Object> requestScopedVars =
                        (Map<String, Object>)vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_REQUEST_SCOPED_VARIABLES);

                assertEquals("oneVal", requestScopedVars.get("oneVar"));

                assertEquals("test",
                             vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_ROUTE_ID));

                assertEquals("https://" + getSpincastConfig().getServerHost() + ":" + getSpincastConfig().getHttpsServerPort() +
                             "/one/test1?key1=val1",
                             vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_FULL_URL));

                assertEquals(true,
                             vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_IS_HTTPS));

                assertNotNull(vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_PATH_PARAMS));

                @SuppressWarnings("unchecked")
                Map<String, String> pathParams =
                        (Map<String, String>)vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_PATH_PARAMS);

                assertEquals("test1", pathParams.get("param1"));

                assertNotNull(vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_QUERYSTRING_PARAMS));

                @SuppressWarnings("unchecked")
                Map<String, List<String>> queryStringParams =
                        (Map<String, List<String>>)vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_QUERYSTRING_PARAMS);

                assertEquals("val1", queryStringParams.get("key1").get(0));

                assertNotNull(vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_COOKIES));

                @SuppressWarnings("unchecked")
                Map<String, Cookie> cookies =
                        (Map<String, Cookie>)vars.get(SpincastConstants.TemplatingGlobalVariables.DEFAULT_GLOBAL_TEMPLATING_VAR_KEY_COOKIES);

                assertEquals("cookie1Val", cookies.get("cookie1"));

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one/test1?key1=val1").setCookie("cookie1", "cookie1Val", false)
                                                           .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

}
