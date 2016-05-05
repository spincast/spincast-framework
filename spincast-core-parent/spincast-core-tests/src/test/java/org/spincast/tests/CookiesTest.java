package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.spincast.core.cookies.ICookie;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.commons.lang3.time.DateUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class CookiesTest extends DefaultIntegrationTestingBase {

    @Test
    public void setCookieCheckEncoding() throws Exception {

        final String random = UUID.randomUUID().toString();

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                ICookie cookie = getCookieFactory().createCookie(SpincastTestUtils.TEST_STRING + "name");
                cookie.setValue(SpincastTestUtils.TEST_STRING + random);
                context.cookies().addCookie(cookie);

                context.response().sendPlainText("test");
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        Map<String, ICookie> cookies = response.getCookies();
        assertEquals(1, cookies.size());
        ICookie cookie = cookies.get(SpincastTestUtils.TEST_STRING + "name");
        assertNotNull(cookie);
        assertEquals(SpincastTestUtils.TEST_STRING + "name", cookie.getName());
        assertEquals(SpincastTestUtils.TEST_STRING + random, cookie.getValue());
    }

    @Test
    public void setGetDeleteCookie() throws Exception {

        final String random = UUID.randomUUID().toString();

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                ICookie cookie = getCookieFactory().createCookie("name");
                cookie.setValue(random);
                context.cookies().addCookie(cookie);
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                ICookie cookie = context.cookies().getCookie("name");
                assertNotNull(cookie);
                assertEquals(random, cookie.getValue());
                assertFalse(cookie.isExpired());

                context.cookies().deleteCookie("name");

                // Cookie should still be there, but with a
                // Expires date in the past!
                cookie = context.cookies().getCookie("name");
                assertNotNull(cookie);

                assertTrue(cookie.isExpired());

                Date expires = cookie.getExpires();
                assertTrue(expires.before(new Date()));
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        Map<String, ICookie> cookies = response.getCookies();
        ICookie cookie = cookies.get("name");
        assertNotNull(cookie);
        assertEquals("name", cookie.getName());
        assertEquals(random, cookie.getValue());

        response = GET("/two").addCookies(cookies.values()).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        cookies = response.getCookies();
        assertEquals(0, cookies.size());
    }

    @Test
    public void setGetDeleteClientSide() throws Exception {

        final String random = UUID.randomUUID().toString();

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                ICookie cookie = getCookieFactory().createCookie("name");
                cookie.setValue(random);
                context.cookies().addCookie(cookie);
            }
        });

        getRouter().GET("/two").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                ICookie cookie = context.cookies().getCookie("name");
                assertNull(cookie);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        Map<String, ICookie> cookies = response.getCookies();
        ICookie cookie = cookies.get("name");
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

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                Map<String, ICookie> cookies = context.cookies().getCookies();
                assertEquals(cookies.size(), 2);

                ICookie cookie = cookies.get("name1");
                assertEquals(random1, cookie.getValue());

                cookie = cookies.get("name2");
                assertEquals(random2, cookie.getValue());

                context.cookies().deleteAllCookies();
            }
        });

        ICookie cookie = getCookieFactory().createCookie("name1", random1);
        cookie.setDomain(getSpincastConfig().getServerHost());
        cookie.setPath("/");

        ICookie cookie2 = getCookieFactory().createCookie("name2", random2);
        cookie2.setDomain(getSpincastConfig().getServerHost());
        cookie2.setPath("/");

        IHttpResponse response = GET("/one").addCookie(cookie).addCookie(cookie2).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        Map<String, ICookie> cookies = response.getCookies();
        assertEquals(cookies.size(), 0);
    }

    @Test
    public void invalidCookieNull() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                context.cookies().addCookie(null);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        Map<String, ICookie> cookies = response.getCookies();
        assertEquals(cookies.size(), 0);
    }

    @Test
    public void invalidCookieEmptyName() throws Exception {

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                ICookie cookie = getCookieFactory().createCookie("");
                context.cookies().addCookie(cookie);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        Map<String, ICookie> cookies = response.getCookies();
        assertEquals(cookies.size(), 0);
    }

    @Test
    public void keepAllInfos() throws Exception {

        final String random = UUID.randomUUID().toString();
        final Date expires = DateUtils.addDays(new Date(), 1);

        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                ICookie cookie = getCookieFactory().createCookie("name1",
                                                                 random,
                                                                 "/one",
                                                                 "localhost",
                                                                 expires,
                                                                 false,
                                                                 false,
                                                                 false,
                                                                 1);
                context.cookies().addCookie(cookie);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        Map<String, ICookie> cookies = response.getCookies();
        assertEquals(1, cookies.size());
        ICookie cookie = cookies.get("name1");
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
        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                ICookie cookie = getCookieFactory().createCookie("name1");
                cookie.setValue(random);
                cookie.setExpiresUsingMaxAge(maxAge);

                context.cookies().addCookie(cookie);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        ICookie cookie = response.getCookie("name1");
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
        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                ICookie cookie = getCookieFactory().createCookie("name1");
                cookie.setValue(random);
                cookie.setExpiresUsingMaxAge(0);

                context.cookies().addCookie(cookie);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        ICookie cookie = response.getCookie("name1");
        assertNotNull(cookie);
        assertNull(cookie.getExpires());
    }

    @Test
    public void setExpiresUsingMaxAgeUnderZero() throws Exception {

        final String random = UUID.randomUUID().toString();
        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                ICookie cookie = getCookieFactory().createCookie("name1");
                cookie.setValue(random);
                cookie.setExpiresUsingMaxAge(-1);

                context.cookies().addCookie(cookie);
            }
        });

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        ICookie cookie = response.getCookie("name1");
        assertNull(cookie);
    }

    @Test
    public void resetCookies() throws Exception {

        final String random = UUID.randomUUID().toString();
        getRouter().GET("/one").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                ICookie cookie = context.cookies().getCookie("name1");
                assertNotNull(cookie);
                Date expires = cookie.getExpires();
                assertTrue(expires == null || expires.after(new Date()));

                context.cookies().deleteAllCookies();
                cookie = context.cookies().getCookie("name1");
                assertNotNull(cookie);
                expires = cookie.getExpires();
                assertTrue(expires.before(new Date()));

                ICookie cookie2 = getCookieFactory().createCookie("name2", "val2");
                context.cookies().addCookie(cookie2);

                cookie = context.cookies().getCookie("name2");
                assertNotNull(cookie);

                context.cookies().resetCookies();

                cookie = context.cookies().getCookie("name1");
                assertNotNull(cookie);
                expires = cookie.getExpires();
                assertTrue(expires == null || expires.after(new Date()));

                cookie = context.cookies().getCookie("name2");
                assertNull(cookie);

                ICookie cookie3 = getCookieFactory().createCookie("name3", "val3");
                context.cookies().addCookie(cookie3);
            }
        });

        ICookie cookie = getCookieFactory().createCookie("name1", random);
        cookie.setDomain(getSpincastConfig().getServerHost());
        cookie.setPath("/");

        IHttpResponse response = GET("/one").addCookie(cookie).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        cookie = response.getCookie("name1");
        assertNotNull(cookie);

        cookie = response.getCookie("name2");
        assertNull(cookie);

        cookie = response.getCookie("name3");
        assertNotNull(cookie);
    }

}
