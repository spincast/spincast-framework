package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.IHandler;
import org.spincast.core.templating.ITemplatingEngine;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

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
public class TemplatingTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Inject
    protected ITemplatingEngine templatingEngine;

    @Inject
    protected IJsonManager jsonManager;

    @Test
    public void htmlTemplate() throws Exception {

        final File testFile = new File(createTestingFilePath());
        String placeholder = this.templatingEngine.createPlaceholder("param1");
        FileUtils.writeStringToFile(testFile, "<p>test : " + placeholder + "</p>", "UTF-8");

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("param1", "Hello!");
                context.response().sendHtmlTemplate(testFile.getAbsolutePath(), false, params);
            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<p>test : Hello!</p>", response.getContentAsString());
    }

    @Test
    public void genericTemplate() throws Exception {

        final File testFile = new File(createTestingFilePath());
        String placeholder = this.templatingEngine.createPlaceholder("fontPxSize");
        FileUtils.writeStringToFile(testFile, "body {font-size : " + placeholder + "px;}", "UTF-8");

        getRouter().GET("/test.css").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("fontPxSize", 16);
                context.response().sendTemplate(testFile.getAbsolutePath(), false, "text/css", params);
            }
        });

        IHttpResponse response = GET("/test.css").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals("body {font-size : 16px;}", response.getContentAsString());
    }

    @Test
    public void resourceUsingTemplate() throws Exception {

        final File testFile = new File(createTestingFilePath());
        String placeholder = this.templatingEngine.createPlaceholder("fontPxSize");
        FileUtils.writeStringToFile(testFile, "body {font-size : " + placeholder + "px;}", "UTF-8");

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
                context.response().sendTemplate(testFile.getAbsolutePath(), false, "text/css", params);
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

        String placeholder = this.templatingEngine.createPlaceholder("name");
        String result = this.templatingEngine.evaluate("Hello " + placeholder, params);
        assertNotNull(result);
        assertEquals("Hello Stromgol", result);
    }

    @Test
    public void evaluateJsonObject() throws Exception {

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("name", "Stromgol");

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

        IJsonObject jsonObj = this.jsonManager.create();
        jsonObj.put("param1", "Stromgol");

        String result = this.templatingEngine.fromTemplate(testFile.getAbsolutePath(), false, jsonObj);
        assertNotNull(result);
        assertEquals("<p>test : Stromgol</p>", result);
    }

}
