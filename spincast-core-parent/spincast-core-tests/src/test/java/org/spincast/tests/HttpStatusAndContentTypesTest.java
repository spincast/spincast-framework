package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.dictionary.SpincastCoreDictionaryEntriesDefault;
import org.spincast.core.exceptions.NotFoundException;
import org.spincast.core.exceptions.PublicExceptionDefault;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonManager;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.xml.XmlManager;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;

public class HttpStatusAndContentTypesTest extends NoAppStartHttpServerTestingBase {

    @Inject
    private JsonManager jsonManager;

    @Inject
    private XmlManager xmlManager;

    @Inject
    private Dictionary dictionary;

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected XmlManager getXmlManager() {
        return this.xmlManager;
    }

    protected String getDefaultExceptionMessage() {
        return this.dictionary.get(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_EXCEPTION_DEFAULTMESSAGE);
    }

    protected String getDefaultNotFoundMessage() {
        return this.dictionary.get(SpincastCoreDictionaryEntriesDefault.MESSAGE_KEY_ROUTE_NOT_FOUND_DEFAULTMESSAGE);
    }

    @Test
    public void notFoundDefault() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/two").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        // Default content-type for a Not Found
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(getDefaultNotFoundMessage(), response.getContentAsString());
    }

    @Test
    public void notFoundPlainText() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/two").addPlainTextAcceptHeader().send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(getDefaultNotFoundMessage(), response.getContentAsString());
    }

    @Test
    public void notFoundPlainTextCustomMessage() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new NotFoundException("Custom message");
            }
        });

        HttpResponse response = GET("/one").addPlainTextAcceptHeader().send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Custom message", response.getContentAsString());
    }

    @Test
    public void notFoundJson() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/two").addJsonAcceptHeader().send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("{\"error\":\"" + getDefaultNotFoundMessage() + "\"}", response.getContentAsString());
    }

    @Test
    public void notFoundJsonCustomMessage() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new NotFoundException("Custom message");
            }
        });

        HttpResponse response = GET("/one").addJsonAcceptHeader().send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("{\"error\":\"Custom message\"}", response.getContentAsString());
    }

    @Test
    public void notFoundXml() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/two").addXMLAcceptHeader().send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<JsonObject><error>" + getDefaultNotFoundMessage() + "</error></JsonObject>",
                     response.getContentAsString());
    }

    @Test
    public void notFoundXmlCustomMessage() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new NotFoundException("Custom message");
            }
        });

        HttpResponse response = GET("/one").addXMLAcceptHeader().send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<JsonObject><error>Custom message</error></JsonObject>", response.getContentAsString());
    }

    @Test
    public void notFoundHtml() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/two").addHTMLAcceptHeader().send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<pre>" + getDefaultNotFoundMessage() + "</pre>",
                     response.getContentAsString());
    }

    @Test
    public void notFoundHtmlCustomMessage() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new NotFoundException("Custom message");
            }
        });

        HttpResponse response = GET("/one").addHTMLAcceptHeader().send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<pre>Custom message</pre>", response.getContentAsString());
    }

    @Test
    public void serverErrorDefault() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertFalse(context.request().isJsonShouldBeReturn());
                assertFalse(context.request().isHTMLShouldBeReturn());
                assertFalse(context.request().isXMLShouldBeReturn());
                assertTrue(context.request().isPlainTextShouldBeReturn());

                throw new RuntimeException("Some exception");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        // Default content-type for an exception
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(getDefaultExceptionMessage(), response.getContentAsString());
    }

    @Test
    public void serverErrorDefaultPublic() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertFalse(context.request().isJsonShouldBeReturn());
                assertFalse(context.request().isHTMLShouldBeReturn());
                assertFalse(context.request().isXMLShouldBeReturn());
                assertTrue(context.request().isPlainTextShouldBeReturn());

                // Public message!
                throw new PublicExceptionDefault("Some exception");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        // Default content-type for an exception
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Some exception", response.getContentAsString());
    }

    @Test
    public void serverErrorDedicatedMethod() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertFalse(context.request().isJsonShouldBeReturn());
                assertFalse(context.request().isHTMLShouldBeReturn());
                assertFalse(context.request().isXMLShouldBeReturn());
                assertTrue(context.request().isPlainTextShouldBeReturn());

                throw new RuntimeException("Some exception");
            }
        });

        HttpResponse response = GET("/one").addPlainTextAcceptHeader().send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(getDefaultExceptionMessage(), response.getContentAsString());
    }

    @Test
    public void serverErrorHtml() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertFalse(context.request().isJsonShouldBeReturn());
                assertTrue(context.request().isHTMLShouldBeReturn());
                assertFalse(context.request().isXMLShouldBeReturn());
                assertFalse(context.request().isPlainTextShouldBeReturn());

                throw new RuntimeException("Some exception");
            }
        });

        HttpResponse response =
                GET("/one").addHeaderValue("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<pre>" + getDefaultExceptionMessage() + "</pre>", response.getContentAsString());
    }

    @Test
    public void serverErrorHtmlDedicatedMethod() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertFalse(context.request().isJsonShouldBeReturn());
                assertTrue(context.request().isHTMLShouldBeReturn());
                assertFalse(context.request().isXMLShouldBeReturn());
                assertFalse(context.request().isPlainTextShouldBeReturn());
                throw new RuntimeException("Some exception");
            }
        });

        HttpResponse response =
                GET("/one").addHTMLAcceptHeader().send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<pre>" + getDefaultExceptionMessage() + "</pre>", response.getContentAsString());
    }

    @Test
    public void serverErrorJson() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertTrue(context.request().isJsonShouldBeReturn());
                assertFalse(context.request().isHTMLShouldBeReturn());
                assertFalse(context.request().isXMLShouldBeReturn());
                assertFalse(context.request().isPlainTextShouldBeReturn());

                throw new RuntimeException("Some exception");
            }
        });

        HttpResponse response = GET("/one").addHeaderValue("Accept", "application/json").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("{\"error\":\"" + getDefaultExceptionMessage() + "\"}", response.getContentAsString());
    }

    @Test
    public void serverErrorJsonDedicatedMethod() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertTrue(context.request().isJsonShouldBeReturn());
                assertFalse(context.request().isHTMLShouldBeReturn());
                assertFalse(context.request().isXMLShouldBeReturn());
                assertFalse(context.request().isPlainTextShouldBeReturn());

                throw new RuntimeException("Some exception");
            }
        });

        HttpResponse response = GET("/one").addJsonAcceptHeader().send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("{\"error\":\"" + getDefaultExceptionMessage() + "\"}", response.getContentAsString());
    }

    @Test
    public void serverErrorXml() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertFalse(context.request().isJsonShouldBeReturn());
                assertFalse(context.request().isHTMLShouldBeReturn());
                assertTrue(context.request().isXMLShouldBeReturn());
                assertFalse(context.request().isPlainTextShouldBeReturn());

                throw new RuntimeException("Some exception");
            }
        });

        HttpResponse response = GET("/one").addHeaderValue("Accept", "application/xml").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<JsonObject><error>" + getDefaultExceptionMessage() + "</error></JsonObject>",
                     response.getContentAsString());
    }

    @Test
    public void serverErrorXmlDedicatedMethod() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertFalse(context.request().isJsonShouldBeReturn());
                assertFalse(context.request().isHTMLShouldBeReturn());
                assertTrue(context.request().isXMLShouldBeReturn());
                assertFalse(context.request().isPlainTextShouldBeReturn());

                throw new RuntimeException("Some exception");
            }
        });

        HttpResponse response = GET("/one").addHeaderValue("Accept", "application/xml").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<JsonObject><error>" + getDefaultExceptionMessage() + "</error></JsonObject>",
                     response.getContentAsString());
    }

    @Test
    public void text() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void textCustom() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendCharacters(SpincastTestUtils.TEST_STRING, "application/custom; charset=utf-8");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("application/custom; charset=utf-8", response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void html() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendHtml(SpincastTestUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void json() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("key1", SpincastTestUtils.TEST_STRING);
                map.put("key2", "val2");

                String jsonString = context.json().toJsonString(map);
                context.response().sendJson(jsonString);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());
        assertNotNull(response.getContentAsString());

        Map<String, Object> map = getJsonManager().fromStringToMap(response.getContentAsString());
        assertNotNull(map);
        assertEquals(SpincastTestUtils.TEST_STRING, map.get("key1"));
        assertEquals("val2", map.get("key2"));
    }

    @Test
    public void jsonObj() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                Map<String, Object> json = new HashMap<String, Object>();
                json.put("key1", SpincastTestUtils.TEST_STRING);
                json.put("key2", "val2");
                context.response().sendJson(json);
            }
        });

        HttpResponse response = GET("/one").send();
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

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("key1", SpincastTestUtils.TEST_STRING);
                map.put("key2", "val2");

                String xml = context.xml().toXml(map);
                context.response().sendXml(xml);
            }
        });

        HttpResponse response = GET("/one").send();
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

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("key1", SpincastTestUtils.TEST_STRING);
                map.put("key2", "val2");
                context.response().sendXml(map);
            }
        });

        HttpResponse response = GET("/one").send();
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

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().setStatusCode(HttpStatus.SC_EXPECTATION_FAILED);
                context.response().sendHtml(SpincastTestUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_EXPECTATION_FAILED, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void customStatusCodeButException() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().setStatusCode(HttpStatus.SC_EXPECTATION_FAILED);
                context.response().sendHtml(SpincastTestUtils.TEST_STRING);

                throw new RuntimeException(SpincastTestUtils.TEST_STRING);

            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
    }

    @Test
    public void guessingContentTypeFromExtension() throws Exception {

        getRouter().GET("/one.mkv").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendBytes(new byte[0]);
            }
        });

        HttpResponse response = GET("/one.mkv").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("video/x-matroska", response.getContentType());

    }

    @Test
    public void noGuessingIfContentTypeSpecified() throws Exception {

        getRouter().GET("/one.mkv").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    context.response().sendBytes(SpincastTestUtils.TEST_STRING.getBytes("UTF-8"), "application/test");
                    context.response().sendBytes(SpincastTestUtils.TEST_STRING.getBytes("UTF-8"));
                    context.response().sendBytes(SpincastTestUtils.TEST_STRING.getBytes("UTF-8"));
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one.mkv").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("application/test", response.getContentType());
        assertNotNull(response.getContentAsString());
        assertEquals(SpincastTestUtils.TEST_STRING + SpincastTestUtils.TEST_STRING + SpincastTestUtils.TEST_STRING,
                     response.getContentAsString());
    }

    @Test
    public void guessStaticResourceContentTypeFromTargetFilePath() throws Exception {

        getRouter().file("/image").classpath("/image.jpg").save();

        HttpResponse response = GET("/image").send();
        assertNotNull(response);
        assertEquals("image/jpeg", response.getContentType());

    }

    @Test
    public void requestContentType() throws Exception {

        getRouter().POST("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String body = context.request().getBodyAsString();
                assertNotNull(body);
                assertEquals("<toto>the entity</toto>", body);

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), contentType);

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        HttpResponse response =
                POST("/").setEntityString("<toto>the entity</toto>", ContentTypeDefaults.XML.getMainVariationWithUtf8Charset())
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

}
