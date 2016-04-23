package org.spincast.tests;

import static org.junit.Assert.assertEquals;
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
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;

import com.google.inject.Inject;

/**
 * Those test are specific to the Pebble templating engine
 * and may fails with another one!
 */
public class TemplatingTest extends DefaultIntegrationTestingBase {

    @Inject
    protected ITemplatingEngine templatingEngine;

    @Inject
    protected IJsonManager jsonManager;

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

        SpincastTestHttpResponse response = get("/one");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<p>test : Hello!</p>", response.getContent());
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

        SpincastTestHttpResponse response = get("/test.css");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("body {font-size : 16px;}", response.getContent());
    }

    @Test
    public void resourceUsingTemplate() throws Exception {

        String generatedFilePath = getTestingWritableDir().getAbsolutePath() + "/generated.css";

        final int[] nbrTimeCalled = new int[]{0};

        //==========================================
        // When using router.file(...) and a generator, the generated
        // resource will automatically be saved.
        //==========================================
        getRouter().file("/test.css").fileSystem(generatedFilePath).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                nbrTimeCalled[0]++;

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("fontPxSize", 16);
                context.response().sendTemplate("/template.css", "text/css", params);
            }
        });

        SpincastTestHttpResponse response = get("/test.css");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("body {font-size : 16px;}", response.getContent());
        assertEquals(1, nbrTimeCalled[0]);

        response = get("/test.css");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("body {font-size : 16px;}", response.getContent());

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

}
