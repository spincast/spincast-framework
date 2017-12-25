package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.commons.lang3.time.DateUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class CookiesTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void setCookieCheckEncoding() throws Exception {

        final String random = UUID.randomUUID().toString();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Cookie cookie = getCookieFactory().createCookie(SpincastTestUtils.TEST_STRING + "name");
                cookie.setValue(SpincastTestUtils.TEST_STRING + random);
                context.response().addCookie(cookie);

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        Map<String, Cookie> cookies = response.getCookies();
        assertEquals(1, cookies.size());
        Cookie cookie = cookies.get(SpincastTestUtils.TEST_STRING + "name");
        assertNotNull(cookie);
        assertEquals(SpincastTestUtils.TEST_STRING + "name", cookie.getName());
        assertEquals(SpincastTestUtils.TEST_STRING + random, cookie.getValue());
    }

    @Test
    public void setGetDeleteCookie() throws Exception {

        final String random = UUID.randomUUID().toString();

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Cookie cookie = getCookieFactory().createCookie("name");
                cookie.setValue(random);
                context.response().addCookie(cookie);
            }
        });

        getRouter().GET("/two").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String cookie = context.request().getCookie("name");
                assertNotNull(cookie);
                assertEquals(random, cookie);

                context.response().deleteCookie("name");

                // Cookie should still be there
                cookie = context.request().getCookie("name");
                assertNotNull(cookie);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        Map<String, Cookie> cookies = response.getCookies();
        Cookie cookie = cookies.get("name");
        assertNotNull(cookie);
        assertEquals("name", cookie.getName());
        assertEquals(random, cookie.getValue());
        cookie.setSecure(false);

        response = GET("/two").addCookies(cookies.values()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        cookies = response.getCookies();
        assertEquals(0, cookies.size());
    }

    @Test
    public void setGetDeleteClientSide() throws Exception {

        final String random = UUID.randomUUID().toString();

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Cookie cookie = getCookieFactory().createCookie("name");
                cookie.setValue(random);
                context.response().addCookie(cookie);
            }
        });

        getRouter().GET("/two").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String cookie = context.request().getCookie("name");
                assertNull(cookie);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        Map<String, Cookie> cookies = response.getCookies();
        Cookie cookie = cookies.get("name");
        assertNotNull(cookie);
        assertEquals("name", cookie.getName());
        assertEquals(random, cookie.getValue());

        response = GET("/two").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        cookies = response.getCookies();
        assertEquals(0, cookies.size());
    }

    @Test
    public void removeAllCookies() throws Exception {

        final String random1 = UUID.randomUUID().toString();
        final String random2 = UUID.randomUUID().toString();

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Map<String, String> cookies = context.request().getCookies();
                assertEquals(cookies.size(), 2);

                String cookie = cookies.get("name1");
                assertEquals(random1, cookie);

                cookie = cookies.get("name2");
                assertEquals(random2, cookie);

                context.response().addCookieSession("name3", "val3");

                context.response().deleteAllCookiesUserHas();
            }
        });

        Cookie cookie = getCookieFactory().createCookie("name1", random1);
        cookie.setDomain(getSpincastConfig().getServerHost());
        cookie.setPath("/");
        cookie.setSecure(false);

        Cookie cookie2 = getCookieFactory().createCookie("name2", random2);
        cookie2.setDomain(getSpincastConfig().getServerHost());
        cookie2.setPath("/");
        cookie2.setSecure(false);

        HttpResponse response = GET("/one").addCookie(cookie).addCookie(cookie2).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        Map<String, Cookie> cookies = response.getCookies();
        assertEquals(cookies.size(), 0);
    }

    @Test
    public void invalidCookieNull() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.response().addCookie(null);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        Map<String, Cookie> cookies = response.getCookies();
        assertEquals(cookies.size(), 0);
    }

    @Test
    public void invalidCookieEmptyName() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Cookie cookie = getCookieFactory().createCookie("");
                context.response().addCookie(cookie);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        Map<String, Cookie> cookies = response.getCookies();
        assertEquals(cookies.size(), 0);
    }

    @Test
    public void keepAllInfos() throws Exception {

        final String random = UUID.randomUUID().toString();
        final Date expires = DateUtils.addDays(new Date(), 1);

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Cookie cookie = getCookieFactory().createCookie("name1",
                                                                random,
                                                                "/one",
                                                                "localhost",
                                                                expires,
                                                                false,
                                                                false,
                                                                false,
                                                                1);
                context.response().addCookie(cookie);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        Map<String, Cookie> cookies = response.getCookies();
        assertEquals(1, cookies.size());
        Cookie cookie = cookies.get("name1");
        assertNotNull(cookie);
        assertEquals("name1", cookie.getName());
        assertEquals("localhost", cookie.getDomain());
        assertEquals(expires.toString(), cookie.getExpires().toString());
        assertEquals("/one", cookie.getPath());
        assertEquals(random, cookie.getValue());
        //assertEquals(1, cookie.getVersion()); // HttpClient returns 0 with the specs used by manually tested.
        assertEquals(false, cookie.isDiscard());
        //assertEquals(false, cookie.isHttpOnly()); // "httpOnly" not supported in current HttpClient version!
        assertEquals(false, cookie.isSecure());
    }

    @Test
    public void setExpiresUsingMaxAgeGreaterThanZero() throws Exception {

        final String random = UUID.randomUUID().toString();
        final Integer maxAge = 1000;
        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Cookie cookie = getCookieFactory().createCookie("name1");
                cookie.setValue(random);
                cookie.setExpiresUsingMaxAge(maxAge);

                context.response().addCookie(cookie);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        Cookie cookie = response.getCookie("name1");
        assertNotNull(cookie);

        //==========================================
        // We give ourself an acceptable range
        //==========================================
        Date now = new Date();
        Date expiresTest = DateUtils.addSeconds(now, maxAge);
        Date expiresTestFrom = DateUtils.addSeconds(expiresTest, -10);
        Date expiresTestTo = DateUtils.addSeconds(expiresTest, 10);
        Date expires = cookie.getExpires();

        assertTrue(expiresTestFrom.before(expires));
        assertTrue(expires.before(expiresTestTo));
    }

    @Test
    public void setExpiresUsingMaxAgeZero() throws Exception {

        final String random = UUID.randomUUID().toString();
        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Cookie cookie = getCookieFactory().createCookie("name1");
                cookie.setValue(random);
                cookie.setExpiresUsingMaxAge(0);

                context.response().addCookie(cookie);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        Cookie cookie = response.getCookie("name1");
        assertNotNull(cookie);
        assertNull(cookie.getExpires());
    }

    @Test
    public void setExpiresUsingMaxAgeUnderZero() throws Exception {

        final String random = UUID.randomUUID().toString();
        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Cookie cookie = getCookieFactory().createCookie("name1");
                cookie.setValue(random);
                cookie.setExpiresUsingMaxAge(-1);

                context.response().addCookie(cookie);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        Cookie cookie = response.getCookie("name1");
        assertNull(cookie);
    }

    @Test
    public void secureCookieViaHttp() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String cookie = context.request().getCookie("myKey");
                assertNull(cookie);
                context.response().sendPlainText("ok");
            }
        });

        Cookie cookie = getCookieFactory().createCookie("myKey", "titi");

        HttpResponse response = GET("/").addCookie(cookie).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void unsecureCookieViaHttp() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String cookie = context.request().getCookie("myKey");
                assertNotNull(cookie);
                assertEquals("titi", cookie);
                context.response().sendPlainText("ok");
            }
        });

        Cookie cookie = getCookieFactory().createCookie("myKey", "titi");
        cookie.setSecure(false);

        HttpResponse response = GET("/").addCookie(cookie).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

}
