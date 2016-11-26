package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.locale.LocaleResolver;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpHeaders;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class LocaleTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Test
    public void defaultLocale() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Locale localeToUse = context.getLocaleToUse();
                context.response().sendPlainText(localeToUse.toString());

            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(getSpincastConfig().getDefaultLocale().toString(), response.getContentAsString());
    }

    @Test
    public void acceptLanguageHeader() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Locale localeToUse = context.getLocaleToUse();
                context.response().sendPlainText(localeToUse.toString());

            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ACCEPT_LANGUAGE, "fr_CA");

        HttpResponse response = GET("/one").addHeaderValue(HttpHeaders.ACCEPT_LANGUAGE, "fr_CA").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("fr_CA", response.getContentAsString());
    }

    @Test
    public void localeCookie() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Locale localeToUse = context.getLocaleToUse();
                context.response().sendPlainText(localeToUse.toString());
            }
        });

        Cookie cookie = getCookieFactory().createCookie(getSpincastConfig().getCookieNameLocale(), "jp");
        cookie.setDomain(getSpincastConfig().getServerHost());
        cookie.setPath("/");

        HttpResponse response =
                GET("/one").addCookie(cookie).addHeaderValue(HttpHeaders.ACCEPT_LANGUAGE, "fr_CA").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("jp", response.getContentAsString());
    }

    @Test
    public void outOfRequestScope() throws Exception {

        LocaleResolver localeResolver = getInjector().getInstance(LocaleResolver.class);
        assertNotNull(localeResolver);

        Locale localeToUse = localeResolver.getLocaleToUse();
        assertNotNull(localeToUse);

        assertEquals(getSpincastConfig().getDefaultLocale(), localeToUse);

    }

}
