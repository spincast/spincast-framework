package org.spincast.plugins.timezoneresolver.tests;

import static org.junit.Assert.assertEquals;

import java.util.TimeZone;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.timezone.TimeZoneResolver;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.inject.Inject;

public class TimeZoneResolverTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected TimeZoneResolver timeZoneResolver;

    protected TimeZoneResolver getTimeZoneResolver() {
        return this.timeZoneResolver;
    }

    @Test
    public void defaultNoRequestContext() throws Exception {
        TimeZone tz = getTimeZoneResolver().getTimeZoneToUse();
        assertEquals(getSpincastConfig().getDefaultTimeZone().getID(), tz.getID());
    }

    @Test
    public void requestContextNoCookie() throws Exception {

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                TimeZone tz = getTimeZoneResolver().getTimeZoneToUse();
                assertEquals(getSpincastConfig().getDefaultTimeZone().getID(), tz.getID());

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
                assertEquals(getSpincastConfig().getDefaultTimeZone().getID(), tz.getID());

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
