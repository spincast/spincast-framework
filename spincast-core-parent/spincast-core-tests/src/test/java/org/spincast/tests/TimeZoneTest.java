package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.TimeZone;

import org.junit.Test;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.timezone.TimeZoneResolver;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class TimeZoneTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void defaultTimeZone() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                TimeZone timeZoneToUse = context.getTimeZoneToUse();
                context.response().sendPlainText(timeZoneToUse.toString());
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(getSpincastConfig().getDefaultTimeZone().toString(), response.getContentAsString());
    }

    @Test
    public void timeZoneCookie() throws Exception {

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                TimeZone timeZoneToUse = context.getTimeZoneToUse();
                context.response().sendPlainText(timeZoneToUse.getID());
            }
        });

        TimeZone timeZone = TimeZone.getTimeZone("America/Los_Angeles");
        String timeZoneId = timeZone.getID();

        Cookie cookie = getCookieFactory().createCookie(getSpincastConfig().getCookieNameTimeZoneId(), timeZoneId);
        cookie.setDomain(getSpincastConfig().getServerHost());
        cookie.setPath("/");
        cookie.setSecure(false);

        HttpResponse response = GET("/one").setCookie(cookie).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(timeZoneId, response.getContentAsString());
    }

    @Test
    public void outOfRequestScope() throws Exception {

        TimeZoneResolver timeZoneResolver = getInjector().getInstance(TimeZoneResolver.class);
        assertNotNull(timeZoneResolver);

        TimeZone timeZone = timeZoneResolver.getTimeZoneToUse();
        assertNotNull(timeZone);

        assertEquals(getSpincastConfig().getDefaultTimeZone(), timeZone);
    }

}
