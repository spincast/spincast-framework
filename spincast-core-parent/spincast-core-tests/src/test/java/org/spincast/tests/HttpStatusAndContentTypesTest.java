package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.xml.IXmlManager;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;

public class HttpStatusAndContentTypesTest extends DefaultIntegrationTestingBase {

    @Inject
    private IJsonManager jsonManager;

    @Inject
    private IXmlManager xmlManager;

    protected IJsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected IXmlManager getXmlManager() {
        return this.xmlManager;
    }

    @Test
    public void notFound() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        IHttpResponse response = GET("/two").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void serverErrorDefault() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertFalse(context.request().isJsonRequest());

                throw new RuntimeException("Some exception");
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        // Default content-type for an exception
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
    }

    @Test
    public void serverErrorHtml() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertFalse(context.request().isJsonRequest());

                throw new RuntimeException("Some exception");
            }
        });

        IHttpResponse response =
                GET("/one").addHeaderValue("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
    }

    @Test
    public void serverErrorJson() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertTrue(context.request().isJsonRequest());

                throw new RuntimeException("Some exception");
            }
        });

        IHttpResponse response = GET("/one").addHeaderValue("Accept", "application/json").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());
    }

    @Test
    public void serverErrorXml() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertFalse(context.request().isJsonRequest());

                throw new RuntimeException("Some exception");
            }
        });

        IHttpResponse response = GET("/one").addHeaderValue("Accept", "application/xml").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), response.getContentType());
    }

    @Test
    public void text() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void textCustom() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendCharacters(SpincastTestUtils.TEST_STRING, "application/custom; charset=utf-8");
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("application/custom; charset=utf-8", response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void html() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendHtml(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void json() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("key1", SpincastTestUtils.TEST_STRING);
                map.put("key2", "val2");

                String jsonString = context.json().toJsonString(map);
                context.response().sendJson(jsonString);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());
        assertNotNull(response.getContentAsString());

        Map<String, Object> map = getJsonManager().fromJsonStringToMap(response.getContentAsString());
        assertNotNull(map);
        assertEquals(SpincastTestUtils.TEST_STRING, map.get("key1"));
        assertEquals("val2", map.get("key2"));
    }

    @Test
    public void jsonObj() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                Map<String, Object> json = new HashMap<String, Object>();
                json.put("key1", SpincastTestUtils.TEST_STRING);
                json.put("key2", "val2");
                context.response().sendJsonObj(json);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());
        assertNotNull(response.getContentAsString());

        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = mapper.readValue(response.getContentAsString(), Map.class);
        assertNotNull(map);
        assertEquals(SpincastTestUtils.TEST_STRING, map.get("key1"));
        assertEquals("val2", map.get("key2"));
    }

    @Test
    public void xml() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("key1", SpincastTestUtils.TEST_STRING);
                map.put("key2", "val2");

                String xml = context.xml().toXml(map);
                context.response().sendXml(xml);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertNotNull(response.getContentAsString());

        @SuppressWarnings("unchecked")
        Map<String, Object> map = getXmlManager().fromXml(response.getContentAsString(), Map.class);
        assertNotNull(map);
        assertEquals(SpincastTestUtils.TEST_STRING, map.get("key1"));
        assertEquals("val2", map.get("key2"));
    }

    @Test
    public void xmlObj() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("key1", SpincastTestUtils.TEST_STRING);
                map.put("key2", "val2");
                context.response().sendXmlObj(map);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertNotNull(response.getContentAsString());

        XmlMapper mapper = new XmlMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = mapper.readValue(response.getContentAsString(), Map.class);
        assertNotNull(map);
        assertEquals(SpincastTestUtils.TEST_STRING, map.get("key1"));
        assertEquals("val2", map.get("key2"));
    }

    @Test
    public void customStatusCode() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().setStatusCode(HttpStatus.SC_EXPECTATION_FAILED);
                context.response().sendHtml(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_EXPECTATION_FAILED, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void customStatusCodeButException() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().setStatusCode(HttpStatus.SC_EXPECTATION_FAILED);
                context.response().sendHtml(SpincastTestUtils.TEST_STRING);

                throw new RuntimeException(SpincastTestUtils.TEST_STRING);

            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
    }

    @Test
    public void guessingContentTypeFromExtension() throws Exception {

        getRouter().GET("/one.mkv").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendBytes(new byte[0]);
            }
        });

        IHttpResponse response = GET("/one.mkv").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("video/x-matroska", response.getContentType());

    }

    @Test
    public void noGuessingIfContentTypeSpecified() throws Exception {

        getRouter().GET("/one.mkv").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    context.response().sendBytes(SpincastTestUtils.TEST_STRING.getBytes("UTF-8"), "application/test");
                    context.response().sendBytes(SpincastTestUtils.TEST_STRING.getBytes("UTF-8"));
                    context.response().sendBytes(SpincastTestUtils.TEST_STRING.getBytes("UTF-8"));
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = GET("/one.mkv").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("application/test", response.getContentType());
        assertNotNull(response.getContentAsString());
        assertEquals(SpincastTestUtils.TEST_STRING + SpincastTestUtils.TEST_STRING + SpincastTestUtils.TEST_STRING,
                     response.getContentAsString());
    }

    @Test
    public void guessStaticResourceContentTypeFromTargetFilePath() throws Exception {

        getRouter().file("/image").classpath("/image.jpg").save();

        IHttpResponse response = GET("/image").send();
        assertNotNull(response);
        assertEquals("image/jpeg", response.getContentType());

    }

    @Test
    public void requestContentType() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String body = context.request().getBodyAsString();
                assertNotNull(body);
                assertEquals("<toto>the entity</toto>", body);

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), contentType);

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response =
                POST("/").setEntityString("<toto>the entity</toto>", ContentTypeDefaults.XML.getMainVariationWithUtf8Charset())
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

}
