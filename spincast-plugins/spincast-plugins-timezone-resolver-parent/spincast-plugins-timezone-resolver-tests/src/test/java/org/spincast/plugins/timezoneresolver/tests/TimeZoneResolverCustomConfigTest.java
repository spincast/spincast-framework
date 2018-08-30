package org.spincast.plugins.timezoneresolver.tests;

import static org.junit.Assert.assertEquals;

import java.util.TimeZone;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.TestingMode;
import org.spincast.core.routing.Handler;
import org.spincast.core.timezone.TimeZoneResolver;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;

import com.google.inject.Inject;

public class TimeZoneResolverCustomConfigTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass2() {
        return CustomConfig.class;
    }

    static class CustomConfig extends SpincastConfigTestingDefault {

        @Inject
        protected CustomConfig(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public TimeZone getDefaultTimeZone() {
            return TimeZone.getTimeZone("America/Los_Angeles");
        }
    }

    @Inject
    protected TimeZoneResolver timeZoneResolver;

    protected TimeZoneResolver getTimeZoneResolver() {
        return this.timeZoneResolver;
    }

    @Test
    public void requestContextNoCookie() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                TimeZone tz = getTimeZoneResolver().getTimeZoneToUse();
                assertEquals("America/Los_Angeles", tz.getID());

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void withCookie() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                TimeZone tz = getTimeZoneResolver().getTimeZoneToUse();
                assertEquals("America/New_York", tz.getID());

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").setCookie(getSpincastConfig().getCookieNameTimeZoneId(),
                                                   "America/New_York",
                                                   false)
                                        .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void withCookieInvalid() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                TimeZone tz = getTimeZoneResolver().getTimeZoneToUse();
                assertEquals("America/Los_Angeles", tz.getID());

                context.response().sendPlainText("ok");
            }
        });

        HttpResponse response = GET("/").setCookie(getSpincastConfig().getCookieNameTimeZoneId(),
                                                   "xxx",
                                                   false)
                                        .send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
