package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.TestingMode;
import org.spincast.core.routing.Handler;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;

public class CookiesHttpsTest extends NoAppStartHttpServerTestingBase {

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
        public String getPublicUrlBase() {
            return "https://" + getServerHost() + ":" + getHttpsServerPort();
        }

        @Override
        public int getHttpsServerPort() {
            if (this.httpsServerPort < 0) {
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
            return "myStorePass";
        }

        @Override
        public String getHttpsKeyStoreKeyPass() {
            return "myKeyPass";
        }
    }

    @Test
    public void secureCookieViaHttps() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String cookie = context.request().getCookieValue("myKey");
                assertNotNull(cookie);
                assertEquals("titi", cookie);
                context.response().sendPlainText("ok");
            }
        });

        Cookie cookie = getCookieFactory().createCookie("myKey", "titi");

        HttpResponse response = GET("/", false, true).setCookie(cookie).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void unsecureCookieViaHttps() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String cookie = context.request().getCookieValue("myKey");
                assertNotNull(cookie);
                assertEquals("titi", cookie);
                context.response().sendPlainText("ok");
            }
        });

        Cookie cookie = getCookieFactory().createCookie("myKey", "titi");
        cookie.setSecure(false);

        HttpResponse response = GET("/", false, true).setCookie(cookie).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
