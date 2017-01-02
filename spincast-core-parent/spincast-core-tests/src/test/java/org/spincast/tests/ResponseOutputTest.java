package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObject;
import org.spincast.core.response.AlertLevel;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;

public class ResponseOutputTest extends IntegrationTestNoAppDefaultContextsBase {

    @Inject
    protected JsonManager jsonManager;

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    @Test
    public void noOutput() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                // nothing!
            }
        });

        HttpResponse response = GET("/one").send();

        // 200 plain/text, no content
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void flush() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
                context.response().flush();
                context.response().sendPlainText("123");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING + "123", response.getContentAsString());
    }

    @Test
    public void end() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);

                try {
                    context.response().end();
                    fail();
                } catch(Exception ex) {
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());

    }

    @Test
    public void textThenBytes() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("first");
            }
        });

        getRouter().ALL("/*{path}").pos(1).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                try {
                    context.response().sendBytes(SpincastTestUtils.TEST_STRING.getBytes("UTF-8"));
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("first" + SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void textThenBytesAndContentType() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("first");
            }
        });

        getRouter().ALL("/*{path}").pos(1).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                try {
                    context.response().sendBytes(SpincastTestUtils.TEST_STRING.getBytes("UTF-8"), "application/test");
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("application/test", response.getContentType());
        assertEquals("first" + SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void textAndBytesFlush() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING, true);
            }
        });

        getRouter().ALL("/*{path}").pos(1).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                try {
                    context.response().sendBytes(SpincastTestUtils.TEST_STRING.getBytes("UTF-8"));
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING + SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void clearBuffer() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        getRouter().ALL("/*{path}").pos(1).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().resetBuffer();
                context.response().sendHtml("<b>hello</b>");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<b>hello</b>", response.getContentAsString());
    }

    @Test
    public void clearBufferAlreadyFlushed() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
                context.response().flush();
            }
        });

        getRouter().ALL("/*{path}").pos(1).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().resetBuffer();
                context.response().sendHtml("<b>hello</b>");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING + "<b>hello</b>", response.getContentAsString());
    }

    @Test
    public void clearBufferAlreadyFlushed2() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING, true);
            }
        });

        getRouter().ALL("/*{path}").pos(1).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().resetBuffer();
                context.response().sendHtml("<b>hello</b>");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING + "<b>hello</b>", response.getContentAsString());
    }

    @Test
    public void bytes() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                try {
                    URL resource = getClass().getClassLoader().getResource("someFile.txt");
                    byte[] bytes = Files.readAllBytes(Paths.get(resource.toURI()));
                    context.response().sendBytes(bytes);
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.BINARY.getMainVariation(), response.getContentType());
        assertEquals("Le bœuf et l'éléphant!", response.getContentAsString());
    }

    @Test
    public void responseFluent() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response()
                       .setStatusCode(HttpStatus.SC_LOCKED)
                       .addHeaderValue("titi", "toto")
                       .sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_LOCKED, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
        assertEquals("toto", response.getHeaderFirst("titi"));
    }

    @Test
    public void sendModelAsJson() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response().getModel().put("someKey", "test1");
                context.response().getModel().put("aaa.bbb", "test2");

                context.response().sendJson();

                // ok not flushed
                context.response().setStatusCode(HttpStatus.SC_LOCKED);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_LOCKED, response.getStatus());
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());

        String json = response.getContentAsString();
        assertEquals("{\"aaa\":{\"bbb\":\"test2\"},\"someKey\":\"test1\"}", json);
    }

    @Test
    public void sendModelAsJsonFlush() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response().getModel().put("someKey", "test1");
                context.response().getModel().put("aaa.bbb", "test2");

                context.response().sendJson(true);

                // Doesn't work!
                context.response().setStatusCode(HttpStatus.SC_LOCKED);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());

        String json = response.getContentAsString();
        assertEquals("{\"aaa\":{\"bbb\":\"test2\"},\"someKey\":\"test1\"}", json);
    }

    @Test
    public void sendModelAsXml() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response().getModel().put("someKey", "test1");
                context.response().getModel().put("aaa.bbb", "test2");

                context.response().sendXml();

                // ok not flushed
                context.response().setStatusCode(HttpStatus.SC_LOCKED);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_LOCKED, response.getStatus());
        assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), response.getContentType());

        String xml = response.getContentAsString();
        assertEquals("<JsonObject><aaa><bbb>test2</bbb></aaa><someKey>test1</someKey></JsonObject>", xml);
    }

    @Test
    public void sendModelAsXmlFlush() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response().getModel().put("someKey", "test1");
                context.response().getModel().put("aaa.bbb", "test2");

                context.response().sendXml(true);

                // Doesn't work!
                context.response().setStatusCode(HttpStatus.SC_LOCKED);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.XML.getMainVariationWithUtf8Charset(), response.getContentType());

        String xml = response.getContentAsString();
        assertEquals("<JsonObject><aaa><bbb>test2</bbb></aaa><someKey>test1</someKey></JsonObject>", xml);
    }

    @Test
    public void addAlert() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response().getModel().put("someKey", "test1");

                context.response().addAlert(AlertLevel.SUCCESS, "my alert");

                context.response().sendJson();
            }
        });

        String spincastModelRootVariableName = getSpincastConfig().getSpincastModelRootVariableName();

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.JSON.getMainVariationWithUtf8Charset(), response.getContentType());

        String json = response.getContentAsString();

        JsonObject jsonObj = getJsonManager().fromString(json);
        assertEquals("test1", jsonObj.getString("someKey"));
        assertEquals("my alert", jsonObj.getString(spincastModelRootVariableName + ".alerts[0].text"));
        assertEquals("SUCCESS", jsonObj.getString(spincastModelRootVariableName + ".alerts[0].alertType"));
    }

}
