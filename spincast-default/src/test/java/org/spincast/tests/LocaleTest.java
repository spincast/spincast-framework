package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.locale.ILocaleResolver;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.http.HttpHeaders;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.client.CookieStore;
import org.spincast.shaded.org.apache.http.impl.cookie.BasicClientCookie;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;

public class LocaleTest extends DefaultIntegrationTestingBase {

    @Test
    public void defaultLocale() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                Locale localeToUse = context.getLocaleToUse();
                context.response().sendPlainText(localeToUse.toString());

            }
        });

        SpincastTestHttpResponse response = get("/one");

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(getSpincastConfig().getDefaultLocale().toString(), response.getContent());
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

        SpincastTestHttpResponse response = get("/one", headers);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("fr_CA", response.getContent());
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

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ACCEPT_LANGUAGE, "fr_CA");

        CookieStore cookieStore = getCookieStore();
        BasicClientCookie basicClientCookie = new BasicClientCookie(SpincastConstants.COOKIE_NAME_LOCALE_TO_USE, "jp");
        basicClientCookie.setDomain(getSpincastConfig().getServerHost());
        basicClientCookie.setPath("/");
        cookieStore.addCookie(basicClientCookie);

        SpincastTestHttpResponse response = get("/one", headers);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("jp", response.getContent());
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
