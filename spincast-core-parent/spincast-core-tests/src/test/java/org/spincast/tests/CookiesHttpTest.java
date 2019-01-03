package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import org.spincast.testing.core.utils.SpincastTestingUtils;

import com.google.inject.Inject;

public class CookiesHttpTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass2() {
        return HttpTestConfig.class;
    }

    protected static class HttpTestConfig extends SpincastConfigTestingDefault {

        private int httpServerPort = -1;

        /**
         * Constructor
         */
        @Inject
        protected HttpTestConfig(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public String getPublicUrlBase() {
            return "http://" + getServerHost() + ":" + getHttpServerPort();
        }

        @Override
        public int getHttpServerPort() {
            if (this.httpServerPort < 0) {
                this.httpServerPort = SpincastTestingUtils.findFreePort();
            }
            return this.httpServerPort;
        };

        @Override
        public int getHttpsServerPort() {
            return -1;
        }
    }

    @Test
    public void secureCookieViaHttp() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String cookie = context.request().getCookieValue("myKey");
                assertNull(cookie);
                context.response().sendPlainText("ok");
            }
        });

        Cookie cookie = getCookieFactory().createCookie("myKey", "titi");

        HttpResponse response = GET("/", false, false).setCookie(cookie).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void unsecureCookieViaHttp() throws Exception {

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

        HttpResponse response = GET("/", false, false).setCookie(cookie).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
