package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;

public class RequestBodyTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void bodyAsInputStream() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    InputStream inputStream = context.request().getBodyAsInputStream();
                    assertNotNull(inputStream);

                    String content = IOUtils.toString(inputStream, "UTF-8");
                    assertNotNull(content);
                    assertTrue(content.contains("Le bœuf et l'éléphant!"));
                    assertTrue(content.contains("Content-Type:"));

                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = POST("/one").addFileToUploadBody("someFile.txt", true, "someName")
                                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void bodyAsByteArray() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {

                    byte[] bodyBytes = context.request().getBodyAsByteArray();
                    assertNotNull(bodyBytes);

                    String content = new String(bodyBytes, "UTF-8");
                    assertNotNull(content);
                    assertEquals("{\"name\":\"" + SpincastTestingUtils.TEST_STRING + "\"}", content);
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = POST("/one").setStringBody("{\"name\":\"" + SpincastTestingUtils.TEST_STRING + "\"}",
                                                             ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void bodyAsString() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    String utf8String = context.request().getBodyAsString();
                    assertNotNull(utf8String);
                    assertEquals("{\"name\":\"" + SpincastTestingUtils.TEST_STRING + "\"}", utf8String);
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = POST("/one").setStringBody("{\"name\":\"" + SpincastTestingUtils.TEST_STRING + "\"}",
                                                             ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void bodyAsStringSpecificEncoding() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    String content = context.request().getStringBody("ISO-8859-15");
                    assertNotNull(content);
                    assertTrue(content.contains("Le bœuf et l'éléphant!"));
                    assertTrue(content.contains("Content-Type:"));
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = POST("/one").addFileToUploadBody("someFile_Iso8859-15.txt", true, "someName")
                                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void bodyAsStringIncorrectEncoding() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    // UTF-8 by default!
                    String content = context.request().getBodyAsString();
                    assertNotNull(content);
                    assertFalse(content.contains("Le bœuf et l'éléphant!"));
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = POST("/one").addFileToUploadBody("someFile_Iso8859-15.txt", true, "someName")
                                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

    }

    @Test
    public void jsonBodyAsJsonObject() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    JsonObject json = context.request().getJsonBody();
                    assertNotNull(json);

                    //==========================================
                    // Can't read the InputStream again!
                    //==========================================
                    try {
                        json = context.request().getJsonBody();
                        fail();
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }

                    String name = json.getString("name");
                    assertNotNull(name);
                    assertEquals(SpincastTestingUtils.TEST_STRING, name);

                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = POST("/one").setStringBody("{\"name\":\"" + SpincastTestingUtils.TEST_STRING + "\"}",
                                                             ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void jsonBodyAsMap() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    Map<String, Object> map = context.request().getJsonBodyAsMap();
                    assertNotNull(map);

                    Object name = map.get("name");
                    assertNotNull(name);
                    assertTrue(name instanceof String);
                    assertEquals(SpincastTestingUtils.TEST_STRING, name);

                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = POST("/one").setStringBody("{\"name\":\"" + SpincastTestingUtils.TEST_STRING + "\"}",
                                                             ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    public static class UserTest {

        public String name;
    }

    @Test
    public void jsonBodyAsUserDefinedClass() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    UserTest user = context.request().getJsonBody(UserTest.class);
                    assertNotNull(user);
                    assertNotNull(user.name);
                    assertEquals(SpincastTestingUtils.TEST_STRING, user.name);
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = POST("/one").setStringBody("{\"name\":\"" + SpincastTestingUtils.TEST_STRING + "\"}",
                                                             ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset())
                                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void xmlBodyAsJsonObject() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    JsonObject json = context.request().getXmlBodyAsJsonObject();
                    assertNotNull(json);

                    String name = json.getString("name");
                    assertNotNull(name);
                    assertEquals(SpincastTestingUtils.TEST_STRING, name);

                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response =
                POST("/one").setStringBody("<user><name>" + SpincastTestingUtils.TEST_STRING + "</name></user>",
                                             ContentTypeDefaults.XML.getMainVariationWithUtf8Charset())
                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void xmlBodyAsMap() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    Map<String, Object> map = context.request().getXmlBodyAsMap();
                    assertNotNull(map);

                    Object name = map.get("name");
                    assertNotNull(name);
                    assertTrue(name instanceof String);
                    assertEquals(SpincastTestingUtils.TEST_STRING, name);

                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response =
                POST("/one").setStringBody("<user><name>" + SpincastTestingUtils.TEST_STRING + "</name></user>",
                                             ContentTypeDefaults.XML.getMainVariationWithUtf8Charset())
                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void xmlBodyAsUserDefinedClass() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    UserTest user = context.request().getXmlBody(UserTest.class);
                    assertNotNull(user);
                    assertNotNull(user.name);
                    assertEquals(SpincastTestingUtils.TEST_STRING, user.name);
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response =
                POST("/one").setStringBody("<user><name>" + SpincastTestingUtils.TEST_STRING + "</name></user>",
                                             ContentTypeDefaults.XML.getMainVariationWithUtf8Charset())
                            .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
