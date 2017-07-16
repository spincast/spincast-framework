package org.spincast.plugins.pebble.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Handler;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.pebble.SpincastPebbleTemplatingEngineConfig;
import org.spincast.plugins.pebble.SpincastPebbleTemplatingEngineConfigDefault;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class PebbleTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected Module getExtraOverridingModule2() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastPebbleTemplatingEngineConfig.class).to(SpincastPebbleTemplatingEngineConfigTesting.class)
                                                                .in(Scopes.SINGLETON);
            }
        };
    }

    /**
     * We enable strict variables
     */
    public static class SpincastPebbleTemplatingEngineConfigTesting extends SpincastPebbleTemplatingEngineConfigDefault {

        @Inject
        public SpincastPebbleTemplatingEngineConfigTesting(SpincastConfig spincastConfig) {
            super(spincastConfig);
        }

        @Override
        public boolean isStrictVariablesEnabled() {
            return true;
        }
    }

    @Inject
    protected TemplatingEngine templatingEngine;

    @Inject
    protected SpincastPebbleTemplatingEngineConfig spincastPebbleTemplatingEngineConfig;

    @Inject
    protected JsonManager jsonManager;

    @Inject
    protected SpincastUtils spincastUtils;

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected SpincastPebbleTemplatingEngineConfig getSpincastPebbleTemplatingEngineConfig() {
        return this.spincastPebbleTemplatingEngineConfig;
    }

    @Test
    public void htmlTemplate() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response().getModel().put("param1", "Hello!");
                context.response().sendTemplateHtml("/template.html");
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<p>test : Hello!</p>", response.getContentAsString());
    }

    @Test
    public void genericTemplate() throws Exception {

        getRouter().GET("/test.css").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().getModel().put("fontPxSize", 16);
                context.response().sendTemplate("/template.css", "text/css");
            }
        });

        HttpResponse response = GET("/test.css").send();

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
        getRouter().file("/test.css").pathAbsolute(generatedFilePath).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                nbrTimeCalled[0]++;

                context.response().getModel().put("fontPxSize", 16);
                context.response().sendTemplate("/template.css", "text/css");
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

        String result = this.templatingEngine.evaluate("Hello {{name}}", params);
        assertNotNull(result);
        assertEquals("Hello Stromgol", result);
    }

    @Test
    public void evaluateJsonObject() throws Exception {

        JsonObject jsonObj = this.jsonManager.create();
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

        JsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("param1", "Stromgol");

        String result = this.templatingEngine.fromTemplate("template.html", jsonObj);
        assertNotNull(result);
        assertEquals("<p>test : Stromgol</p>", result);
    }

    @Test
    public void defaultVariables() throws Exception {

        getRouter().GET("/one/${param1}").id("test").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.variables().add("oneVar", "oneVal");

                context.response().getModel().put("extraParam1", "extraParam1Val");

                context.response().sendTemplateHtml("/templateDefaultVars.html");
            }
        });

        HttpResponse response = GET("/one/test1?key1=val1").addCookie("cookie1", "cookie1Val").send();

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
        assertTrue(getSpincastPebbleTemplatingEngineConfig().isStrictVariablesEnabled());
    }

}
