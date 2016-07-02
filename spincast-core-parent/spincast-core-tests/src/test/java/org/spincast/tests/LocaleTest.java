package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.cookies.ICookie;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.locale.ILocaleResolver;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpHeaders;
import org.spincast.shaded.org.apache.http.HttpStatus;

public class LocaleTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Test
    public void defaultLocale() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                Locale localeToUse = context.getLocaleToUse();
                context.response().sendPlainText(localeToUse.toString());

            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(getSpincastConfig().getDefaultLocale().toString(), response.getContentAsString());
    }

    @Test
    public void acceptLanguageHeader() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                Locale localeToUse = context.getLocaleToUse();
                context.response().sendPlainText(localeToUse.toString());

            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ACCEPT_LANGUAGE, "fr_CA");

        IHttpResponse response = GET("/one").addHeaderValue(HttpHeaders.ACCEPT_LANGUAGE, "fr_CA").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("fr_CA", response.getContentAsString());
    }

    @Test
    public void localeCookie() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                Locale localeToUse = context.getLocaleToUse();
                context.response().sendPlainText(localeToUse.toString());
            }
        });

        ICookie cookie = getCookieFactory().createCookie(SpincastConstants.COOKIE_NAME_LOCALE_TO_USE, "jp");
        cookie.setDomain(getSpincastConfig().getServerHost());
        cookie.setPath("/");

        IHttpResponse response =
                GET("/one").addCookie(cookie).addHeaderValue(HttpHeaders.ACCEPT_LANGUAGE, "fr_CA").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("jp", response.getContentAsString());
    }

    @Test
    public void outOfRequestScope() throws Exception {

        ILocaleResolver localeResolver = getInjector().getInstance(ILocaleResolver.class);
        assertNotNull(localeResolver);

        Locale localeToUse = localeResolver.getLocaleToUse();
        assertNotNull(localeToUse);

        assertEquals(getSpincastConfig().getDefaultLocale(), localeToUse);

    }

}
