package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.json.IJsonObject;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.http.HttpEntity;
import org.spincast.shaded.org.apache.http.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.client.HttpClient;
import org.spincast.shaded.org.apache.http.client.methods.HttpPost;
import org.spincast.shaded.org.apache.http.entity.StringEntity;
import org.spincast.shaded.org.apache.http.entity.mime.MultipartEntityBuilder;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class RequestBodyTest extends DefaultIntegrationTestingBase {

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

        HttpClient httpclient = getHttpClient();

        URL resource = getClass().getClassLoader().getResource("someFile.txt");
        File file = new File(resource.toURI());

        HttpEntity httpEntity = MultipartEntityBuilder.create().addBinaryBody("someName", file).build();

        HttpPost httpPost = new HttpPost(createTestUrl("/one"));
        httpPost.setEntity(httpEntity);

        HttpResponse response = httpclient.execute(httpPost);
        int status = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, status);
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

        HttpPost request = new HttpPost(createTestUrl("/one"));

        StringEntity input = new StringEntity("{\"name\":\"" + SpincastTestUtils.TEST_STRING + "\"}", "UTF-8");
        input.setContentType(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset());
        request.setEntity(input);

        HttpResponse response = getHttpClient().execute(request);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
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

        HttpPost request = new HttpPost(createTestUrl("/one"));

        StringEntity input = new StringEntity("{\"name\":\"" + SpincastTestUtils.TEST_STRING + "\"}", "UTF-8");
        input.setContentType(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset());
        request.setEntity(input);

        HttpResponse response = getHttpClient().execute(request);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
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

        HttpClient httpclient = getHttpClient();

        URL resource = getClass().getClassLoader().getResource("someFile_Iso8859-15.txt");
        File file = new File(resource.toURI());

        HttpEntity httpEntity = MultipartEntityBuilder.create().addBinaryBody("someName", file).build();

        HttpPost httpPost = new HttpPost(createTestUrl("/one"));
        httpPost.setEntity(httpEntity);

        HttpResponse response = httpclient.execute(httpPost);
        int status = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, status);
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

        HttpClient httpclient = getHttpClient();

        URL resource = getClass().getClassLoader().getResource("someFile_Iso8859-15.txt");
        File file = new File(resource.toURI());

        HttpEntity httpEntity = MultipartEntityBuilder.create().addBinaryBody("someName", file).build();

        HttpPost httpPost = new HttpPost(createTestUrl("/one"));
        httpPost.setEntity(httpEntity);

        HttpResponse response = httpclient.execute(httpPost);
        int status = response.getStatusLine().getStatusCode();
        assertEquals(HttpStatus.SC_OK, status);
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

        HttpPost request = new HttpPost(createTestUrl("/one"));

        StringEntity input = new StringEntity("{\"name\":\"" + SpincastTestUtils.TEST_STRING + "\"}", "UTF-8");
        input.setContentType(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset());
        request.setEntity(input);

        HttpResponse response = getHttpClient().execute(request);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
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

        HttpPost request = new HttpPost(createTestUrl("/one"));

        StringEntity input = new StringEntity("{\"name\":\"" + SpincastTestUtils.TEST_STRING + "\"}", "UTF-8");
        input.setContentType(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset());
        request.setEntity(input);

        HttpResponse response = getHttpClient().execute(request);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
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

        HttpPost request = new HttpPost(createTestUrl("/one"));

        StringEntity input = new StringEntity("{\"name\":\"" + SpincastTestUtils.TEST_STRING + "\"}", "UTF-8");
        input.setContentType(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset());
        request.setEntity(input);

        HttpResponse response = getHttpClient().execute(request);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
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

        HttpPost request = new HttpPost(createTestUrl("/one"));

        StringEntity input = new StringEntity("<user><name>" + SpincastTestUtils.TEST_STRING + "</name></user>",
                                              "UTF-8");
        input.setContentType(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset());
        request.setEntity(input);

        HttpResponse response = getHttpClient().execute(request);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
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

        HttpPost request = new HttpPost(createTestUrl("/one"));

        StringEntity input = new StringEntity("<user><name>" + SpincastTestUtils.TEST_STRING + "</name></user>",
                                              "UTF-8");
        input.setContentType(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset());
        request.setEntity(input);

        HttpResponse response = getHttpClient().execute(request);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
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

        HttpPost request = new HttpPost(createTestUrl("/one"));

        StringEntity input = new StringEntity("<user><name>" + SpincastTestUtils.TEST_STRING + "</name></user>",
                                              "UTF-8");
        input.setContentType(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset());
        request.setEntity(input);

        HttpResponse response = getHttpClient().execute(request);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

}
