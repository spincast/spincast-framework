package org.spincast.tests.https;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.SpincastTestConfig;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Module;

public class HttpsTest extends DefaultIntegrationTestingBase {

    protected static class HttpsTestConfig extends SpincastTestConfig {

        private int httpsServerPort = -1;

        @Override
        public int getHttpServerPort() {
            return -1;
        }

        @Override
        public int getHttpsServerPort() {
            if(this.httpsServerPort < 0) {
                this.httpsServerPort = SpincastTestUtils.findFreePort();
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
            return "secret";
        }

        @Override
        public String getHttpsKeyStoreKeypass() {
            return "secret";
        }
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

            @Override
            protected Class<? extends ISpincastConfig> getSpincastConfigClass() {
                return HttpsTestConfig.class;
            }
        };
    }

    @Test
    public void https() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response = GET("/one", false, true).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

}
