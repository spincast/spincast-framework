package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.SpincastTestConfig;

import com.google.inject.Module;

public class RequestBodyMaxSizeTest extends SpincastDefaultNoAppIntegrationTestBase {

    protected static class TestingSpincastConfig2 extends SpincastTestConfig {

        //==========================================
        // Max 10 bytes
        //==========================================
        @Override
        public long getServerMaxRequestBodyBytes() {
            return 10;
        }
    }

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected Class<? extends SpincastConfig> getSpincastConfigClass() {
                return TestingSpincastConfig2.class;
            }
        };
    }

    @Test
    public void get() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void postSmall() throws Exception {

        getRouter().POST("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response =
                POST("/one").setEntityString("1234567890", ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void postTooBig() throws Exception {

        getRouter().POST("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response()
                       .sendPlainText("ok sdfsdasdasdfsd453sfsdf 3fdgdfawdgdfg 23423asdfsd453sfsdf " +
                                      "3fdgdfgdfasdfwersd453sfsdf 3fdgdfgdfasdfsd453sfsdf 3fdgdfgdf4ertertert453 3fdgdfgdfg " +
                                      "ertertertfsdfs32453453 3fdgdfgdfg ertertertertertertertertetertetertq24234");
            }
        });

        HttpResponse response =
                POST("/one").setEntityString("12345678901", ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset()).send();
        assertEquals(HttpStatus.SC_REQUEST_TOO_LONG, response.getStatus());
    }

}
