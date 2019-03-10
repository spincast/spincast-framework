package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonObject;
import org.spincast.core.json.JsonObjectDefault;
import org.spincast.core.routing.Handler;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.xml.XmlManager;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;

public class FormatsTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected XmlManager xmlManager;

    @Inject
    protected TemplatingEngine templatingEngine;

    @Test
    public void toJsonString() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject jsonObj = context.json().create();
                jsonObj.set("someBoolean", true);
                jsonObj.set("someInt", 123);

                JsonObject jsonObj2 = context.json().create();
                jsonObj2.set("anotherBoolean", true);
                jsonObj2.set("anotherInt", 44444);
                jsonObj2.set("innerObj", jsonObj);

                String jsonString = context.json().toJsonString(jsonObj2);

                context.response().sendPlainText(jsonString);

            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}".length(),
                     response.getContentAsString().length());
    }

    @Test
    public void fromJsonStringToJsonObject() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String jsonString =
                        "{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}";

                JsonObject jsonObj = context.json().fromString(jsonString);
                assertNotNull(jsonObj);
                assertEquals(true, jsonObj.getBoolean("anotherBoolean"));
                assertNotNull(jsonObj.getJsonObject("innerObj"));
                assertTrue(jsonObj.getJsonObject("innerObj") instanceof JsonObjectDefault);

            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void fromJsonStringToMap() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String jsonString =
                        "{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}";

                Map<String, Object> map = context.json().fromStringToMap(jsonString);
                assertNotNull(map);
                assertEquals(true, (boolean)map.get("anotherBoolean"));
                assertNotNull(map.get("innerObj"));
                assertTrue(map.get("innerObj") instanceof Map);

            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void fromJsonStringToT() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String jsonString =
                        "{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}";

                JsonObject jsonObj = context.json().fromString(jsonString, JsonObject.class);
                assertNotNull(jsonObj);
                assertEquals(true, jsonObj.getBoolean("anotherBoolean"));
                assertNotNull(jsonObj.getJsonObject("innerObj"));
                assertTrue(jsonObj.getJsonObject("innerObj") instanceof JsonObjectDefault);

            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void fromJsonInputStreamToJsonObject() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    String jsonString =
                            "{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}";

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));

                    JsonObject jsonObject = context.json().fromInputStream(byteArrayInputStream);
                    assertNotNull(jsonObject);
                    assertEquals(true, jsonObject.getBoolean("anotherBoolean"));
                    assertNotNull(jsonObject.getJsonObject("innerObj"));
                    assertTrue(jsonObject.getJsonObject("innerObj") instanceof JsonObject);
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void fromJsonInputStreamToMap() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    String jsonString =
                            "{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}";

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));

                    Map<String, Object> map = context.json().fromInputStreamToMap(byteArrayInputStream);
                    assertNotNull(map);
                    assertEquals(true, (boolean)map.get("anotherBoolean"));
                    assertNotNull(map.get("innerObj"));
                    assertTrue(map.get("innerObj") instanceof Map);
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void fromJsonInputStreamToT() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    String jsonString =
                            "{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}";

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));

                    JsonObject jsonObj = context.json().fromInputStream(byteArrayInputStream, JsonObject.class);
                    assertNotNull(jsonObj);
                    assertTrue(jsonObj instanceof JsonObjectDefault);
                    assertEquals(true, jsonObj.getBoolean("anotherBoolean"));
                    assertNotNull(jsonObj.getJsonObject("innerObj"));
                    assertTrue(jsonObj.getJsonObject("innerObj") instanceof JsonObjectDefault);
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void toXmlStringFromMap() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("someBoolean", true);
                map.put("someInt", 123);

                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("anotherBoolean", true);
                map2.put("anotherInt", 44444);
                map2.put("innerObj", map);

                String xmlString = context.xml().toXml(map2);

                context.response().sendPlainText(xmlString);
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(("<HashMap><innerObj><someBoolean>true</someBoolean><someInt>123</someInt>" +
                      "</innerObj><anotherBoolean>true</anotherBoolean><anotherInt>44444</anotherInt></HashMap>").length(),
                     response.getContentAsString().length());
    }

    @Test
    public void toXmlStringFromJsonObject() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject jsonObj = context.json().create();
                jsonObj.set("someBoolean", true);
                jsonObj.set("someInt", 123);

                JsonObject jsonObj2 = context.json().create();
                jsonObj2.set("anotherBoolean", true);
                jsonObj2.set("anotherInt", 44444);
                jsonObj2.set("innerObj", jsonObj);

                String xmlString = context.xml().toXml(jsonObj2);

                context.response().sendPlainText(xmlString);
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        String xml = response.getContentAsString();
        assertNotNull(xml);

        JsonObject json = this.xmlManager.fromXml(xml);
        assertNotNull(xml);
        assertEquals(true, json.getBoolean("anotherBoolean"));
        assertEquals(new Integer(44444), json.getInteger("anotherInt"));
        JsonObject inner = json.getJsonObject("innerObj");
        assertNotNull(inner);
        assertEquals(true, inner.getBoolean("someBoolean"));
        assertEquals(new Integer(123), inner.getInteger("someInt"));
    }

    @Test
    public void evaluate() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String placeholder = context.templating().createPlaceholder("name");
                String content = "Hello " + placeholder + "!";
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("name", "Toto");
                String evaluated = context.templating().evaluate(content, params);
                assertEquals("Hello Toto!", evaluated);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void fromTemplate() throws Exception {

        final File testFile = new File(createTestingFilePath());
        String placeholder = this.templatingEngine.createPlaceholder("param1");
        FileUtils.writeStringToFile(testFile, "<p>test : " + placeholder + "</p>", "UTF-8");

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("param1", "Toto");
                String evaluated = context.templating().fromTemplate(testFile.getAbsolutePath(), false, params);
                assertEquals("<p>test : Toto</p>", evaluated);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
