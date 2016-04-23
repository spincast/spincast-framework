package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class HttpStatusAndContentTypesTest extends DefaultIntegrationTestingBase {

    @Test
    public void notFound() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        SpincastTestHttpResponse response = get("/two");
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

        SpincastTestHttpResponse response = get("/one");
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

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

        SpincastTestHttpResponse response = get("/one", headers);
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

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");

        SpincastTestHttpResponse response = get("/one", headers);
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

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/xml");

        SpincastTestHttpResponse response = get("/one", headers);
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

        SpincastTestHttpResponse response = get("/one");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void textCustom() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendCharacters(SpincastTestUtils.TEST_STRING, "application/custom; charset=utf-8");
            }
        });

        SpincastTestHttpResponse response = get("/one");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("application/custom; charset=utf-8", response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void html() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendHtml(SpincastTestUtils.TEST_STRING);
            }
        });

        SpincastTestHttpResponse response = get("/one");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void json() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                Map<String, Object> json = new HashMap<String, Object>();
                json.put("key1", SpincastTestUtils.TEST_STRING);
                json.put("key2", "val2");
                context.response().sendJson(json);
            }
        });

        SpincastTestHttpResponse response = get("/one");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());
        assertNotNull(response.getContent());

        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = mapper.readValue(response.getContent(), Map.class);
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
                context.response().sendXml(map);
            }
        });

        SpincastTestHttpResponse response = get("/one");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertNotNull(response.getContent());

        XmlMapper mapper = new XmlMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = mapper.readValue(response.getContent(), Map.class);
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

        SpincastTestHttpResponse response = get("/one");
        assertEquals(HttpStatus.SC_EXPECTATION_FAILED, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
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

        SpincastTestHttpResponse response = get("/one");
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

        SpincastTestHttpResponse response = get("/one.mkv");
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

        SpincastTestHttpResponse response = get("/one.mkv");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("application/test", response.getContentType());
        assertNotNull(response.getContent());
        assertEquals(SpincastTestUtils.TEST_STRING + SpincastTestUtils.TEST_STRING + SpincastTestUtils.TEST_STRING,
                     response.getContent());
    }

}
