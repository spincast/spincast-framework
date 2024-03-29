package org.spincast.plugins.pebble.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Handler;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.pebble.SpincastPebbleTemplatingEngineConfig;
import org.spincast.plugins.pebble.SpincastPebbleTemplatingEngineConfigDefault;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class PebbleTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected Module getExtraOverridingModule() {
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

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response().getModel().set("param1", "Hello!");
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

        getRouter().GET("/test.css").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().getModel().set("fontPxSize", 16);
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
        jsonObj.set("name", "Stromgol");

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
        jsonObj.set("param1", "Stromgol");

        String result = this.templatingEngine.fromTemplate("template.html", jsonObj);
        assertNotNull(result);
        assertEquals("<p>test : Stromgol</p>", result);
    }

    @Test
    public void fromTemplateWithListJsonObject() throws Exception {

        List<String> list = new ArrayList<String>();
        list.add("aaa");
        list.add("bbb");
        list.add("ccc");

        JsonObject jsonObj = this.jsonManager.create();
        jsonObj.set("list", list);

        String result = this.templatingEngine.fromTemplate("template2.html", jsonObj);
        assertNotNull(result);
        assertEquals("aaabbbccc", result);
    }

    @Test
    public void fromTemplateWithListMap() throws Exception {

        List<String> list = new ArrayList<String>();
        list.add("aaa");
        list.add("bbb");
        list.add("ccc");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("list", list);

        String result = this.templatingEngine.fromTemplate("template2.html", params);
        assertNotNull(result);
        assertEquals("aaabbbccc", result);
    }

    @Test
    public void fromTemplateWithJsonObjectListJsonObject() throws Exception {

        List<JsonObject> list = new ArrayList<JsonObject>();
        JsonObject obj1 = this.jsonManager.create();
        list.add(obj1);
        obj1.set("name", "aaa");
        JsonObject obj2 = this.jsonManager.create();
        list.add(obj2);
        obj2.set("name", "bbb");
        JsonObject obj3 = this.jsonManager.create();
        list.add(obj3);
        obj3.set("name", "ccc");

        JsonObject jsonObj = this.jsonManager.create();
        jsonObj.set("list", list);

        String result = this.templatingEngine.fromTemplate("template3.html", jsonObj);
        assertNotNull(result);
        assertEquals("aaabbbccc", result);
    }

    @Test
    public void fromTemplateWithJsonObjectListMap() throws Exception {

        List<JsonObject> list = new ArrayList<JsonObject>();
        JsonObject obj1 = this.jsonManager.create();
        list.add(obj1);
        obj1.set("name", "aaa");
        JsonObject obj2 = this.jsonManager.create();
        list.add(obj2);
        obj2.set("name", "bbb");
        JsonObject obj3 = this.jsonManager.create();
        list.add(obj3);
        obj3.set("name", "ccc");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("list", list);

        String result = this.templatingEngine.fromTemplate("template3.html", params);
        assertNotNull(result);
        assertEquals("aaabbbccc", result);
    }

    @Test
    public void defaultVariables() throws Exception {

        getRouter().GET("/one/${param1}").id("test").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.variables().set("oneVar", "oneVal");

                context.response().getModel().set("extraParam1", "extraParam1Val");

                context.response().sendTemplateHtml("/templateDefaultVars.html");
            }
        });

        HttpResponse response = GET("/one/test1?key1=val1").setCookie("cookie1", "cookie1Val", true).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());

        String spincastCurrentVersion = getSpincastUtils().getSpincastCurrentVersion();
        boolean isSnapshot = spincastCurrentVersion.endsWith("-SNAPSHOT");
        String cacheBuster = getSpincastUtils().getCacheBusterCode();
        String fullUrl = "https://" + getSpincastConfig().getServerHost() + ":" + getSpincastConfig().getHttpsServerPort() +
                         "/one/test1?key1=val1";

        StringBuilder expected = new StringBuilder();
        expected.append("en").append("|");
        expected.append(spincastCurrentVersion).append("|");
        expected.append(isSnapshot).append("|");
        expected.append(cacheBuster).append("|");
        expected.append("test").append("|");
        expected.append(fullUrl).append("|");
        expected.append("true").append("|"); // isHttps
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

    @Test
    public void nullValue() throws Exception {

        JsonObject params = this.jsonManager.create();
        params.set("map", null);

        String result = this.templatingEngine.fromTemplate("template5.html", params);
        assertNotNull(result);
        assertEquals("xx", result);
    }

    /**
     * Ne passe pas présentement!!
     * https://github.com/PebbleTemplates/pebble/issues/446
     */
    @Test
    public void nullValueInMap() throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", null);
        params.put("map", map);

        String result = this.templatingEngine.fromTemplate("template6.html", params);
        assertNotNull(result);
        assertEquals("xx", result);
    }

    @Test
    public void dynamicJsonObjects() throws Exception {

        List<JsonObject> list = new ArrayList<JsonObject>();
        JsonObject obj1 = this.jsonManager.create();
        list.add(obj1);
        obj1.set("name", "aaa");
        JsonObject obj2 = this.jsonManager.create();
        list.add(obj2);
        obj2.set("name", "bbb");
        JsonObject obj3 = this.jsonManager.create();
        list.add(obj3);
        obj3.set("name", "ccc");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("list", list);

        String result = this.templatingEngine.fromTemplate("template3.html", params);
        assertNotNull(result);
        assertEquals("aaabbbccc", result);
    }

    @Test
    public void dynamicJsonArrays() throws Exception {

        List<JsonArray> list = new ArrayList<JsonArray>();
        JsonArray arr = this.jsonManager.createArray();
        list.add(arr);
        arr.add("aaa");
        arr.add("bbb");
        arr.add("ccc");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("list", list);

        String result = this.templatingEngine.fromTemplate("template4.html", params);
        assertNotNull(result);
        assertEquals("aaabbbccc", result);
    }


}
