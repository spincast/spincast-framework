package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.TestingMode;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;

public class RequestBodyMaxSizeTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass2() {
        return TestingSpincastConfig2.class;
    }

    protected static class TestingSpincastConfig2 extends SpincastConfigTestingDefault {

        /**
         * Constructor
         */
        @Inject
        protected TestingSpincastConfig2(SpincastConfigPluginConfig spincastConfigPluginConfig,
                                         @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        //==========================================
        // Max 10 bytes
        //==========================================
        @Override
        public long getServerMaxRequestBodyBytes() {
            return 10;
        }
    }

    @Test
    public void get() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

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

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response =
                POST("/one").setStringBody("1234567890", ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ok", response.getContentAsString());
    }

    @Test
    public void postTooBig() throws Exception {

        getRouter().POST("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response()
                       .sendPlainText("ok sdfsdasdasdfsd453sfsdf 3fdgdfawdgdfg 23423asdfsd453sfsdf " +
                                      "3fdgdfgdfasdfwersd453sfsdf 3fdgdfgdfasdfsd453sfsdf 3fdgdfgdf4ertertert453 3fdgdfgdfg " +
                                      "ertertertfsdfs32453453 3fdgdfgdfg ertertertertertertertertetertetertq24234");
            }
        });

        HttpResponse response =
                POST("/one").setStringBody("12345678901", ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset()).send();
        assertEquals(HttpStatus.SC_REQUEST_TOO_LONG, response.getStatus());
    }

}
