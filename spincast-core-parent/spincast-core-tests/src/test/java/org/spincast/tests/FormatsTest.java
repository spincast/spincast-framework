package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.json.IJsonObjectFactory;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.IHandler;
import org.spincast.core.templating.ITemplatingEngine;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.xml.IXmlManager;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.inject.Inject;

public class FormatsTest extends DefaultIntegrationTestingBase {

    @Inject
    protected IJsonObjectFactory jsonObjectFactory;

    @Inject
    protected IXmlManager xmlManager;

    @Inject
    protected ITemplatingEngine templatingEngine;

    @Test
    public void toJsonString() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject jsonObj = context.json().create();
                jsonObj.put("someBoolean", true);
                jsonObj.put("someInt", 123);

                IJsonObject jsonObj2 = context.json().create();
                jsonObj2.put("anotherBoolean", true);
                jsonObj2.put("anotherInt", 44444);
                jsonObj2.put("innerObj", jsonObj);

                String jsonString = context.json().toJsonString(jsonObj2);

                context.response().sendPlainText(jsonString);

            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}".length(),
                     response.getContentAsString().length());
    }

    @Test
    public void fromJsonStringToJsonObject() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String jsonString =
                        "{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}";

                IJsonObject jsonObj = context.json().create(jsonString);
                assertNotNull(jsonObj);
                assertEquals(true, jsonObj.getBoolean("anotherBoolean"));
                assertNotNull(jsonObj.getJsonObject("innerObj"));
                assertTrue(jsonObj.getJsonObject("innerObj") instanceof JsonObject);

            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void fromJsonStringToMap() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String jsonString =
                        "{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}";

                Map<String, Object> map = context.json().fromJsonStringToMap(jsonString);
                assertNotNull(map);
                assertEquals(true, (boolean)map.get("anotherBoolean"));
                assertNotNull(map.get("innerObj"));
                assertTrue(map.get("innerObj") instanceof Map);

            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void fromJsonStringToT() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String jsonString =
                        "{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}";

                IJsonObject jsonObj = context.json().fromJsonString(jsonString, IJsonObject.class);
                assertNotNull(jsonObj);
                assertEquals(true, jsonObj.getBoolean("anotherBoolean"));
                assertNotNull(jsonObj.getJsonObject("innerObj"));
                assertTrue(jsonObj.getJsonObject("innerObj") instanceof JsonObject);

            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void fromJsonInputStreamToJsonObject() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    String jsonString =
                            "{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}";

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));

                    IJsonObject jsonObject = context.json().create(byteArrayInputStream);
                    assertNotNull(jsonObject);
                    assertEquals(true, (boolean)jsonObject.get("anotherBoolean"));
                    assertNotNull(jsonObject.get("innerObj"));
                    assertTrue(jsonObject.get("innerObj") instanceof IJsonObject);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void fromJsonInputStreamToMap() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    String jsonString =
                            "{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}";

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));

                    Map<String, Object> map = context.json().fromJsonInputStreamToMap(byteArrayInputStream);
                    assertNotNull(map);
                    assertEquals(true, (boolean)map.get("anotherBoolean"));
                    assertNotNull(map.get("innerObj"));
                    assertTrue(map.get("innerObj") instanceof Map);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void fromJsonInputStreamToT() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    String jsonString =
                            "{\"innerObj\":{\"someBoolean\":true,\"someInt\":123},\"anotherBoolean\":true,\"anotherInt\":44444}";

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));

                    IJsonObject jsonObj = context.json().fromJsonInputStream(byteArrayInputStream, IJsonObject.class);
                    assertNotNull(jsonObj);
                    assertTrue(jsonObj instanceof JsonObject);
                    assertEquals(true, jsonObj.getBoolean("anotherBoolean"));
                    assertNotNull(jsonObj.getJsonObject("innerObj"));
                    assertTrue(jsonObj.getJsonObject("innerObj") instanceof JsonObject);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void toXmlStringFromMap() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

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

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(("<HashMap><innerObj><someBoolean>true</someBoolean><someInt>123</someInt>" +
                      "</innerObj><anotherBoolean>true</anotherBoolean><anotherInt>44444</anotherInt></HashMap>").length(),
                     response.getContentAsString().length());
    }

    @Test
    public void toXmlStringFromJsonObject() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject jsonObj = context.json().create();
                jsonObj.put("someBoolean", true);
                jsonObj.put("someInt", 123);

                IJsonObject jsonObj2 = context.json().create();
                jsonObj2.put("anotherBoolean", true);
                jsonObj2.put("anotherInt", 44444);
                jsonObj2.put("innerObj", jsonObj);

                String xmlString = context.xml().toXml(jsonObj2);

                context.response().sendPlainText(xmlString);
            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        String xml = response.getContentAsString();
        assertNotNull(xml);

        IJsonObject json = this.xmlManager.fromXml(xml);
        assertNotNull(xml);
        assertEquals(true, json.getBoolean("anotherBoolean"));
        assertEquals(new Integer(44444), json.getInteger("anotherInt"));
        IJsonObject inner = json.getJsonObject("innerObj");
        assertNotNull(inner);
        assertEquals(true, inner.getBoolean("someBoolean"));
        assertEquals(new Integer(123), inner.getInteger("someInt"));
    }

    @Test
    public void evaluate() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String placeholder = context.templating().createPlaceholder("name");
                String content = "Hello " + placeholder + "!";
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("name", "Toto");
                String evaluated = context.templating().evaluate(content, params);
                assertEquals("Hello Toto!", evaluated);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void fromTemplate() throws Exception {

        final File testFile = new File(createTestingFilePath());
        String placeholder = this.templatingEngine.createPlaceholder("param1");
        FileUtils.writeStringToFile(testFile, "<p>test : " + placeholder + "</p>", "UTF-8");

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("param1", "Toto");
                String evaluated = context.templating().fromTemplate(testFile.getAbsolutePath(), false, params);
                assertEquals("<p>test : Toto</p>", evaluated);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
