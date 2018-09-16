package org.spincast.plugins.httpclient.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.TestingMode;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.testing.core.utils.SpincastTestingUtils;

import com.google.inject.Inject;

public class HttpClientTestSllSelfSignedNotAccepted extends NoAppStartHttpServerTestingBase {

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass2() {
        return HttpsTestConfig.class;
    }

    protected static class HttpsTestConfig extends SpincastConfigTestingDefault {

        private int httpsServerPort = -1;

        /**
         * Constructor
         */
        @Inject
        protected HttpsTestConfig(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public int getHttpServerPort() {
            return -1;
        }

        @Override
        public int getHttpsServerPort() {
            if (this.httpsServerPort < 0) {
                this.httpsServerPort = SpincastTestingUtils.findFreePort();
            }
            return this.httpsServerPort;
        }

        @Override
        public String getHttpsKeyStorePath() {
            return "/self-signed-certificate.jks";
        }

        @Override
        public String getHttpsKeyStoreType() {
            return "JKS";
        }

        @Override
        public String getHttpsKeyStoreStorePass() {
            return "myStorePass";
        }

        @Override
        public String getHttpsKeyStoreKeyPass() {
            return "myKeyPass";
        }
    }

    //==========================================
    // Here, we do NOT disable SSL certificate errors!
    //==========================================
    @Override
    protected boolean isDisableSllCetificateErrors() {
        return false;
    }

    @Test
    public void selfSignedCetificateNotAccepted() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        try {
            GET("/", false, true).send();
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void selfSignedCetificateAcceptedBecauseInlineRule() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/", false, true).disableSslCertificateErrors().send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());

    }

}
