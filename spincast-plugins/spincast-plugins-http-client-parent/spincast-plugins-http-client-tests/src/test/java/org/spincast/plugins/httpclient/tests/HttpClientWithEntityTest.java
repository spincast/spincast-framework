package org.spincast.plugins.httpclient.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.json.IJsonManager;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.xml.IXmlManager;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.http.HttpEntity;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.entity.ContentType;
import org.spincast.shaded.org.apache.http.entity.InputStreamEntity;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class HttpClientWithEntityTest extends DefaultIntegrationTestingBase {

    @Inject
    protected IJsonManager jsonManager;

    @Inject
    protected IXmlManager xmlManager;

    @Test
    public void postEmptyEntity() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertEquals("", context.request().getBodyAsString());
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response = POST("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void postAddEntityFormDataValue() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String value = context.request().getFormDataFirst("key1");
                assertNotNull(value);
                assertEquals("value1", value);

                value = context.request().getFormDataFirst("key2");
                assertNotNull(value);
                assertEquals("value2", value);

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response = POST("/").addEntityFormDataValue("key1", "value1")
                                          .addEntityFormDataValue("key2", "value2").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void postSetEntityFormData() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                List<String> values = context.request().getFormData("key1");
                assertNotNull(values);
                assertEquals(2, values.size());

                assertEquals("value1", values.get(0));
                assertEquals("value2", values.get(1));

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response = POST("/").setEntityFormData("key1", Lists.newArrayList("value1", "value2"))
                                          .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void postSetEntityString() throws Exception {

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

    @Test
    public void postSetEntityHttpEntity() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                String body = context.request().getBodyAsString();
                assertNotNull(body);
                assertEquals("Le bœuf et l'éléphant!", body);

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), contentType);

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        InputStream stream = getClass().getClassLoader().getResourceAsStream("someFile.txt");
        try {

            HttpEntity entity =
                    new InputStreamEntity(stream, ContentType.parse(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset()));

            IHttpResponse response = POST("/").setEntity(entity).send();

            assertEquals(HttpStatus.SC_OK, response.getStatus());
            assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
            assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());

        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    @Test
    public void postSendBinaryEntity() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

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

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        InputStream stream = getClass().getClassLoader().getResourceAsStream("image.jpg");
        try {

            HttpEntity entity =
                    new InputStreamEntity(stream, ContentType.parse("image/jpeg"));

            IHttpResponse response = POST("/").setEntity(entity).send();

            assertEquals(HttpStatus.SC_OK, response.getStatus());
            assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
            assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());

        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    @Test
    public void postSetEntityStringJson() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject jsonObj = context.request().getJsonBodyAsJsonObject();
                assertNotNull(jsonObj);
                assertEquals("test", jsonObj.getString("name"));

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), contentType);

                assertEquals(HttpMethod.POST, context.request().getHttpMethod());

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response =
                POST("/").setEntityString("{\"name\":\"test\"}", ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                         .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void putSetJsonEntity() throws Exception {

        getRouter().PUT("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject jsonObj = context.request().getJsonBodyAsJsonObject();
                assertNotNull(jsonObj);
                assertEquals("test", jsonObj.getString("name"));

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), contentType);

                assertEquals(HttpMethod.PUT, context.request().getHttpMethod());

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        IJsonObject obj = this.jsonManager.create();
        obj.put("name", "test");

        IHttpResponse response = PUT("/").setEntityJson(obj).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void patchSetEntityStringJson() throws Exception {

        getRouter().PATCH("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject jsonObj = context.request().getJsonBodyAsJsonObject();
                assertNotNull(jsonObj);
                assertEquals("test", jsonObj.getString("name"));

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), contentType);

                assertEquals(HttpMethod.PATCH, context.request().getHttpMethod());

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response =
                PATCH("/").setEntityString("{\"name\":\"test\"}", ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                          .send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void uploadFile() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    File uploadedFile = context.request().getUploadedFileFirst("someName");
                    assertNotNull(uploadedFile);

                    String content = FileUtils.readFileToString(uploadedFile, "UTF-8");
                    assertEquals("Le bœuf et l'éléphant!", content);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = POST("/one").addEntityFileUpload("someFile.txt", true, "someName").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void overwriteEntity() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    File uploadedFile = context.request().getUploadedFileFirst("someName");
                    assertNotNull(uploadedFile);

                    String content = FileUtils.readFileToString(uploadedFile, "UTF-8");
                    assertEquals("Le bœuf et l'éléphant!", content);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = POST("/one").addEntityFormDataValue("key1", "value1")
                                             .addEntityFileUpload("someFile.txt", true, "someName").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void setEntityJson() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject jsonObj = context.request().getJsonBodyAsJsonObject();
                assertNotNull(jsonObj);
                assertEquals("test", jsonObj.getString("name"));

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), contentType);

                assertEquals(HttpMethod.POST, context.request().getHttpMethod());

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        IJsonObject obj = this.jsonManager.create();
        obj.put("name", "test");

        IHttpResponse response = POST("/").setEntityJson(obj).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void setEntityXml() throws Exception {

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                IJsonObject jsonObj = context.request().getXmlBodyAsJsonObject();
                assertNotNull(jsonObj);
                assertEquals("test", jsonObj.getString("name"));

                String contentType = context.request().getContentType();
                assertNotNull(contentType);
                assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), contentType);

                assertEquals(HttpMethod.POST, context.request().getHttpMethod());

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        IJsonObject obj = this.jsonManager.create();
        obj.put("name", "test");

        IHttpResponse response = POST("/").setEntityXml(obj).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

}
