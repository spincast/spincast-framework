package org.spincast.plugins.httpclient.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonArray;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.server.UploadedFile;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.xml.XmlManager;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.http.HttpEntity;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.entity.ContentType;
import org.spincast.shaded.org.apache.http.entity.InputStreamEntity;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class HttpClientWithEntityTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected JsonManager jsonManager;

    @Inject
    protected XmlManager xmlManager;

    @Test
    public void postEmptyEntity() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = POST("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void postAddEntityFormDataValue() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String value = context.request().getFormBodyAsJsonObject().getString("key1");
                assertNotNull(value);
                assertEquals("value1", value);

                value = context.request().getFormBodyAsJsonObject().getString("key2");
                assertNotNull(value);
                assertEquals("value2", value);

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = POST("/").addFormBodyFieldValue("key1", "value1")
                                         .addFormBodyFieldValue("key2", "value2").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void postSetEntityFormData() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonArray values = context.request().getFormBodyAsJsonObject().getJsonArray("key1");
                assertNotNull(values);
                assertEquals(2, values.size());

                assertEquals("value1", values.getString(0));
                assertEquals("value2", values.getString(1));

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = POST("/").setFormBodyField("key1[0]", Lists.newArrayList("value1"))
                                         .setFormBodyField("key1[1]", Lists.newArrayList("value2"))
                                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void postSetEntityString() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String body = context.request().getBodyAsString();
                assertNotNull(body);
                assertEquals("<toto>the entity</toto>", body);

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), contentType);

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response =
                POST("/").setStringBody("<toto>the entity</toto>", ContentTypeDefaults.XML.getMainVariationWithUtf8Charset())
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void postSetEntityHttpEntity() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String body = context.request().getBodyAsString();
                assertNotNull(body);
                assertEquals("Le bœuf et l'éléphant!", body);

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), contentType);

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        InputStream stream = getClass().getClassLoader().getResourceAsStream("someFile.txt");
        try {

            HttpEntity entity =
                    new InputStreamEntity(stream, ContentType.parse(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset()));

            HttpResponse response = POST("/").setBody(entity).send();

            assertEquals(HttpStatus.SC_OK, response.getStatus());
            assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
            assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());

        } finally {
            SpincastStatics.closeQuietly(stream);
        }
    }

    @Test
    public void postSendBinaryEntity() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                byte[] bytes = context.request().getBodyAsByteArray();
                assertNotNull(bytes);
                assertTrue(bytes.length > 100);
                assertEquals(-1, bytes[0]);
                assertEquals(-40, bytes[1]);
                assertEquals(-1, bytes[2]);
                assertEquals(-37, bytes[3]);

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals("image/jpeg", contentType);

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        InputStream stream = getClass().getClassLoader().getResourceAsStream("image.jpg");
        try {

            HttpEntity entity =
                    new InputStreamEntity(stream, ContentType.parse("image/jpeg"));

            HttpResponse response = POST("/").setBody(entity).send();

            assertEquals(HttpStatus.SC_OK, response.getStatus());
            assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
            assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());

        } finally {
            SpincastStatics.closeQuietly(stream);
        }
    }

    @Test
    public void postSetEntityStringJson() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject jsonObj = context.request().getJsonBody();
                assertNotNull(jsonObj);
                assertEquals("test", jsonObj.getString("name"));

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), contentType);

                assertEquals(HttpMethod.POST, context.request().getHttpMethod());

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response =
                POST("/").setStringBody("{\"name\":\"test\"}", ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void putSetJsonEntity() throws Exception {

        getRouter().PUT("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject jsonObj = context.request().getJsonBody();
                assertNotNull(jsonObj);
                assertEquals("test", jsonObj.getString("name"));

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), contentType);

                assertEquals(HttpMethod.PUT, context.request().getHttpMethod());

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        JsonObject obj = this.jsonManager.create();
        obj.set("name", "test");

        HttpResponse response = PUT("/").setJsonStringBody(obj).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void patchSetEntityStringJson() throws Exception {

        getRouter().PATCH("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject jsonObj = context.request().getJsonBody();
                assertNotNull(jsonObj);
                assertEquals("test", jsonObj.getString("name"));

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), contentType);

                assertEquals(HttpMethod.PATCH, context.request().getHttpMethod());

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response =
                PATCH("/").setStringBody("{\"name\":\"test\"}", ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                          .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void uploadFile() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    UploadedFile uploadedFile = context.request().getUploadedFileFirst("someName");
                    assertNotNull(uploadedFile);

                    String content = FileUtils.readFileToString(uploadedFile.getFile(), "UTF-8");
                    assertEquals("Le bœuf et l'éléphant!", content);
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = POST("/one").addFileToUploadBody("someFile.txt", true, "someName").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void overwriteEntity() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    UploadedFile uploadedFile = context.request().getUploadedFileFirst("someName");
                    assertNotNull(uploadedFile);

                    String content = FileUtils.readFileToString(uploadedFile.getFile(), "UTF-8");
                    assertEquals("Le bœuf et l'éléphant!", content);
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = POST("/one").addFormBodyFieldValue("key1", "value1")
                                            .addFileToUploadBody("someFile.txt", true, "someName").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void setEntityJson() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject jsonObj = context.request().getJsonBody();
                assertNotNull(jsonObj);

                assertEquals("test", jsonObj.getString("name"));

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), contentType);

                assertEquals(HttpMethod.POST, context.request().getHttpMethod());

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        JsonObject obj = this.jsonManager.create();
        obj.set("name", "test");

        HttpResponse response = POST("/").setJsonStringBody(obj).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void setEntityXml() throws Exception {

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                JsonObject jsonObj = context.request().getXmlBodyAsJsonObject();
                assertNotNull(jsonObj);
                assertEquals("test", jsonObj.getString("name"));

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), contentType);

                assertEquals(HttpMethod.POST, context.request().getHttpMethod());

                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        JsonObject obj = this.jsonManager.create();
        obj.set("name", "test");

        HttpResponse response = POST("/").setXmlStringBody(obj).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

}
