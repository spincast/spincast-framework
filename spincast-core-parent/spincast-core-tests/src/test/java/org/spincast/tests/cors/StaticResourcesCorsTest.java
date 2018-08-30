package org.spincast.tests.cors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.spincast.core.cookies.Cookie;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.HttpMethod;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;

public class StaticResourcesCorsTest extends NoAppStartHttpServerTestingBase {

    @Test
    public void noCorsConfig() throws Exception {

        getRouter().file("/").classpath("/image.jpg").handle();

        getRouter().ALL("/*{path}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = GET("/").send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNull(allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader);

        String allowMehodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMehodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void corsDefault() throws Exception {

        getRouter().file("/").classpath("/image.jpg").cors().handle();

        getRouter().ALL("/*{path}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                        .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                        .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNotNull(allowCredentialsHeader);
        assertEquals("true", allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNotNull(exposeHeadersHeader);
        assertEquals("", exposeHeadersHeader);

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNull(allowHeadersHeader);

        String allowMethodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMethodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void corsFilterButNoCorsHeaders() throws Exception {

        getRouter().file("/").classpath("/image.jpg").cors().handle();

        HttpResponse response = GET("/").send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNull(allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader);

        String allowMehodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMehodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void sameOriginWithCorsHeaders() throws Exception {

        getRouter().file("/").classpath("/image.jpg").cors().handle();

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                        .addHeaderValue(HttpHeaders.HOST, "example1.com")
                                        .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNull(allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader);

        String allowMehodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMehodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());

    }

    @Test
    public void corsFilterSpecificOriginOnlyValid() throws Exception {

        getRouter().file("/").classpath("/image.jpg")
                   .cors(Sets.newHashSet("http://example1.com", "https://example1.com"))
                   .handle();

        getRouter().ALL("/*{path}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "http://example1.com")
                                        .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                        .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("http://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNotNull(allowCredentialsHeader);
        assertEquals("true", allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNotNull(exposeHeadersHeader);
        assertEquals("", exposeHeadersHeader);

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNull(allowHeadersHeader);

        String allowMethodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMethodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void corsFilterSpecificOriginOnlyInvalid() throws Exception {

        getRouter().file("/").classpath("/image.jpg")
                   .cors(Sets.newHashSet("http://example3.com"))
                   .handle();

        getRouter().ALL("/*{path}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "http://example1.com")
                                        .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                        .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNull(allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader);

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNull(allowHeadersHeader);

        String allowMethodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMethodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void corsFilterSpecificOriginOnlyInvalidProtocole() throws Exception {

        getRouter().file("/").classpath("/image.jpg")
                   .cors(Sets.newHashSet("https://example1.com"))
                   .handle();

        getRouter().ALL("/*{path}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "http://example1.com")
                                        .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                        .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNull(allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader);

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNull(allowHeadersHeader);

        String allowMethodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMethodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void extraHeadersAllowedToBeRead() throws Exception {

        getRouter().file("/").classpath("/image.jpg")
                   .cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"))
                   .handle();

        getRouter().ALL("/*{path}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                        .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                        .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNotNull(allowCredentialsHeader);
        assertEquals("true", allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNotNull(exposeHeadersHeader);
        assertTrue("extra-header-to-be-read-1,extra-header-to-be-read-2".equals(exposeHeadersHeader) ||
                   "extra-header-to-be-read-2,extra-header-to-be-read-1".equals(exposeHeadersHeader));

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNull(allowHeadersHeader);

        String allowMethodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMethodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void noCookies() throws Exception {

        getRouter().file("/").classpath("/image.jpg")
                   .cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         Sets.newHashSet("*"),
                         false)
                   .handle();

        // Cors filter
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         null,
                         false);

        getRouter().ALL("/*{path}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                        .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                        .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader); // no header == FALSE

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNotNull(exposeHeadersHeader);
        assertTrue("extra-header-to-be-read-1,extra-header-to-be-read-2".equals(exposeHeadersHeader) ||
                   "extra-header-to-be-read-2,extra-header-to-be-read-1".equals(exposeHeadersHeader));

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNull(allowHeadersHeader);

        String allowMethodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMethodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void invalidHttpMethodPost() throws Exception {

        getRouter().file("/").classpath("/image.jpg")
                   .cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         Sets.newHashSet("*"),
                         false)
                   .handle();

        getRouter().ALL("/*{path}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = POST("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                         .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                         .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNull(allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader);

        String allowMehodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMehodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        //==========================================
        // Undertow serves a resource when the method is
        // POST... 
        //==========================================
        //assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, response.getStatus());
    }

    @Test
    public void invalidHttpMethodDelete() throws Exception {

        getRouter().file("/").classpath("/image.jpg")
                   .cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         Sets.newHashSet("*"),
                         false)
                   .handle();

        getRouter().ALL("/*{path}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = DELETE("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                           .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                           .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNull(allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader);

        String allowMehodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMehodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, response.getStatus());
    }

    @Test
    public void httpMethodHead() throws Exception {

        getRouter().file("/").classpath("/image.jpg").cors().handle();

        getRouter().ALL("/*{path}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = HEAD("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                         .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                         .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNotNull(allowCredentialsHeader);
        assertEquals("true", allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNotNull(exposeHeadersHeader);
        assertEquals("", exposeHeadersHeader);

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNull(allowHeadersHeader);

        String allowMethodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMethodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void wildcardNotValidWhenThereAreCookies() throws Exception {

        getRouter().file("/").classpath("/image.jpg").cors().handle();

        getRouter().ALL("/*{path}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        Cookie cookie = getCookieFactory().createCookie("name1", "value2");
        cookie.setDomain(getSpincastConfig().getServerHost());
        cookie.setPath("/");

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                        .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                        .setCookie(cookie)
                                        .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNotNull(allowCredentialsHeader);
        assertEquals("true", allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNotNull(exposeHeadersHeader);
        assertEquals("", exposeHeadersHeader);

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNull(allowHeadersHeader);

        String allowMethodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMethodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("image/jpeg", response.getContentType());
    }

    @Test
    public void preflightDefault() throws Exception {

        getRouter().file("/").classpath("/image.jpg").cors().handle();

        getRouter().ALL("/*{path}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                                            .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNotNull(allowCredentialsHeader);
        assertEquals("true", allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader); // simple request header only

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNotNull(allowHeadersHeader);
        assertEquals("", allowHeadersHeader);

        String allowMethodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNotNull(allowMethodsHeader);
        Set<String> methods = new HashSet<>(Arrays.asList(StringUtils.split(allowMethodsHeader, ",")));
        assertEquals(3, methods.size());
        assertTrue(methods.contains(HttpMethod.GET.toString()));
        assertTrue(methods.contains(HttpMethod.HEAD.toString()));
        assertTrue(methods.contains(HttpMethod.OPTIONS.toString()));

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void preflightInvalidMethodAsked() throws Exception {

        getRouter().file("/").classpath("/image.jpg").cors().handle();

        getRouter().ALL("/*{path}").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "PUT")
                                            .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNull(allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader);

        String allowMehodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMehodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());

    }
}
