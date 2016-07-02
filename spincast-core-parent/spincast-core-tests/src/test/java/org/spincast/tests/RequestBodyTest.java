package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class RequestBodyTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Test
    public void bodyAsInputStream() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {

                    InputStream inputStream = context.request().getBodyAsInputStream();
                    assertNotNull(inputStream);

                    String content = IOUtils.toString(inputStream, "UTF-8");
                    assertNotNull(content);
                    assertTrue(content.contains("Le bœuf et l'éléphant!"));
                    assertTrue(content.contains("Content-Type:"));

                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = POST("/one").addEntityFileUpload("someFile.txt", true, "someName")
                                             .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void bodyAsByteArray() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {

                    byte[] bodyBytes = context.request().getBodyAsByteArray();
                    assertNotNull(bodyBytes);

                    String content = new String(bodyBytes, "UTF-8");
                    assertNotNull(content);
                    assertEquals("{\"name\":\"" + SpincastTestUtils.TEST_STRING + "\"}", content);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = POST("/one").setEntityString("{\"name\":\"" + SpincastTestUtils.TEST_STRING + "\"}",
                                                              ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                                             .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void bodyAsString() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    String utf8String = context.request().getBodyAsString();
                    assertNotNull(utf8String);
                    assertEquals("{\"name\":\"" + SpincastTestUtils.TEST_STRING + "\"}", utf8String);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = POST("/one").setEntityString("{\"name\":\"" + SpincastTestUtils.TEST_STRING + "\"}",
                                                              ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                                             .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void bodyAsStringSpecificEncoding() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    String content = context.request().getBodyAsString("ISO-8859-15");
                    assertNotNull(content);
                    assertTrue(content.contains("Le bœuf et l'éléphant!"));
                    assertTrue(content.contains("Content-Type:"));
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = POST("/one").addEntityFileUpload("someFile_Iso8859-15.txt", true, "someName")
                                             .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void bodyAsStringIncorrectEncoding() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    // UTF-8 by default!
                    String content = context.request().getBodyAsString();
                    assertNotNull(content);
                    assertFalse(content.contains("Le bœuf et l'éléphant!"));
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = POST("/one").addEntityFileUpload("someFile_Iso8859-15.txt", true, "someName")
                                             .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

    }

    @Test
    public void jsonBodyAsJsonObject() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    IJsonObject json = context.request().getJsonBodyAsJsonObject();
                    assertNotNull(json);

                    Object name = json.get("name");
                    assertNotNull(name);
                    assertTrue(name instanceof String);
                    assertEquals(SpincastTestUtils.TEST_STRING, (String)name);

                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = POST("/one").setEntityString("{\"name\":\"" + SpincastTestUtils.TEST_STRING + "\"}",
                                                              ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                                             .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void jsonBodyAsMap() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    Map<String, Object> map = context.request().getJsonBodyAsMap();
                    assertNotNull(map);

                    Object name = map.get("name");
                    assertNotNull(name);
                    assertTrue(name instanceof String);
                    assertEquals(SpincastTestUtils.TEST_STRING, (String)name);

                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = POST("/one").setEntityString("{\"name\":\"" + SpincastTestUtils.TEST_STRING + "\"}",
                                                              ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                                             .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    public static class UserTest {

        public String name;
    }

    @Test
    public void jsonBodyAsUserDefinedClass() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    UserTest user = context.request().getJsonBody(UserTest.class);
                    assertNotNull(user);
                    assertNotNull(user.name);
                    assertEquals(SpincastTestUtils.TEST_STRING, user.name);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = POST("/one").setEntityString("{\"name\":\"" + SpincastTestUtils.TEST_STRING + "\"}",
                                                              ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                                             .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void xmlBodyAsJsonObject() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    IJsonObject json = context.request().getXmlBodyAsJsonObject();
                    assertNotNull(json);

                    Object name = json.get("name");
                    assertNotNull(name);
                    assertTrue(name instanceof String);
                    assertEquals(SpincastTestUtils.TEST_STRING, (String)name);

                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response =
                POST("/one").setEntityString("<user><name>" + SpincastTestUtils.TEST_STRING + "</name></user>",
                                             ContentTypeDefaults.XML.getMainVariationWithUtf8Charset())
                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void xmlBodyAsMap() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    Map<String, Object> map = context.request().getXmlBodyAsMap();
                    assertNotNull(map);

                    Object name = map.get("name");
                    assertNotNull(name);
                    assertTrue(name instanceof String);
                    assertEquals(SpincastTestUtils.TEST_STRING, (String)name);

                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response =
                POST("/one").setEntityString("<user><name>" + SpincastTestUtils.TEST_STRING + "</name></user>",
                                             ContentTypeDefaults.XML.getMainVariationWithUtf8Charset())
                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void xmlBodyAsUserDefinedClass() throws Exception {

        getRouter().POST("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    UserTest user = context.request().getXmlBody(UserTest.class);
                    assertNotNull(user);
                    assertNotNull(user.name);
                    assertEquals(SpincastTestUtils.TEST_STRING, user.name);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response =
                POST("/one").setEntityString("<user><name>" + SpincastTestUtils.TEST_STRING + "</name></user>",
                                             ContentTypeDefaults.XML.getMainVariationWithUtf8Charset())
                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
