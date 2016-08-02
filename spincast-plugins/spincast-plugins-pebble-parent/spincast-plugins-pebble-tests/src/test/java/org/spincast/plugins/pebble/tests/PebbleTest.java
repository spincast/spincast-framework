package org.spincast.plugins.pebble.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.IHandler;
import org.spincast.core.templating.ITemplatingEngine;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.plugins.pebble.ISpincastPebbleTemplatingEngineConfig;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;

public class PebbleTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Inject
    protected ITemplatingEngine templatingEngine;

    @Inject
    protected ISpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig;

    @Inject
    protected IJsonManager jsonManager;

    @Inject
    protected ISpincastUtils spincastUtils;

    protected ISpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected ISpincastPebbleTemplatingEngineConfig getSpincastPebbleTemplatingEngineConfig() {
        return this.spincastPebbleTemplatingEngineConfig;
    }

    @Override
    protected void clearRoutes() {
        //==========================================
        // We do not remove the routes before each test
        // since we want to test a filter that is
        // automatically added.
        // But we remove any route which id is "test".
        //==========================================
        getRouter().removeRoute("test");
    }

    @Test
    public void htmlTemplate() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("param1", "Hello!");
                context.response().sendHtmlTemplate("/template.html", params);
            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<p>test : Hello!</p>", response.getContentAsString());
    }

    @Test
    public void genericTemplate() throws Exception {

        getRouter().GET("/test.css").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("fontPxSize", 16);
                context.response().sendTemplate("/template.css", "text/css", params);
            }
        });

        IHttpResponse response = GET("/test.css").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("body {font-size : 16px;}", response.getContentAsString());
    }

    @Test
    public void resourceUsingTemplate() throws Exception {

        String generatedFilePath = getTestingWritableDir().getAbsolutePath() + "/generated.css";

        final int[] nbrTimeCalled = new int[]{0};

        //==========================================
        // When using router.file(...) and a generator, the generated
        // resource will automatically be saved.
        //==========================================
        getRouter().file("/test.css").pathAbsolute(generatedFilePath).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                nbrTimeCalled[0]++;

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("fontPxSize", 16);
                context.response().sendTemplate("/template.css", "text/css", params);
            }
        });

        IHttpResponse response = GET("/test.css").send();
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

        String result = this.templatingEngine.evaluate("Hello {{name}}", params);
        assertNotNull(result);
        assertEquals("Hello Stromgol", result);
    }

    @Test
    public void evaluateJsonObject() throws Exception {

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("name", "Stromgol");

        String result = this.templatingEngine.evaluate("Hello {{name}}", jsonObj);
        assertNotNull(result);
        assertEquals("Hello Stromgol", result);
    }

    @Test
    public void fromTemplateMap() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("param1", "Stromgol");

        String result = this.templatingEngine.fromTemplate("template.html", params);
        assertNotNull(result);
        assertEquals("<p>test : Stromgol</p>", result);
    }

    @Test
    public void fromTemplateJsonObject() throws Exception {

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("param1", "Stromgol");

        String result = this.templatingEngine.fromTemplate("template.html", jsonObj);
        assertNotNull(result);
        assertEquals("<p>test : Stromgol</p>", result);
    }

    @Test
    public void defaultVariables() throws Exception {

        getRouter().GET("/one/${param1}").id("test").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                context.variables().add("oneVar", "oneVal");

                context.response().sendHtmlTemplate("/templateDefaultVars.html",
                                                    SpincastStatics.params("extraParam1", "extraParam1Val"));
            }
        });

        IHttpResponse response = GET("/one/test1?key1=val1").addCookie("cookie1", "cookie1Val").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String spincastCurrentVersion = getSpincastUtils().getSpincastCurrentVersion();
        boolean isSnapshot = spincastCurrentVersion.endsWith("-SNAPSHOT");
        String cacheBuster = getSpincastUtils().getCacheBusterCode();
        String fullUrl = "http://" + getSpincastConfig().getServerHost() + ":" + getSpincastConfig().getHttpServerPort() +
                         "/one/test1?key1=val1";

        StringBuilder expected = new StringBuilder();
        expected.append("en").append("|");
        expected.append(spincastCurrentVersion).append("|");
        expected.append(isSnapshot).append("|");
        expected.append(cacheBuster).append("|");
        expected.append("test").append("|");
        expected.append(fullUrl).append("|");
        expected.append("false").append("|");
        expected.append("test1").append("|");
        expected.append("val1").append("|");
        expected.append("cookie1Val").append("|");
        expected.append("oneVal").append("|");
        expected.append("extraParam1Val").append("|");

        String result = response.getContentAsString();
        assertEquals(expected.toString(), result);
    }

    @Test
    public void cacheValuesNotDebugMode() throws Exception {
        assertEquals(0, getSpincastPebbleTemplatingEngineConfig().getTemplateCacheItemNbr());
        assertEquals(200, getSpincastPebbleTemplatingEngineConfig().getTagCacheTypeItemNbr());
        assertFalse(getSpincastPebbleTemplatingEngineConfig().isStrictVariablesEnabled());
    }

}
