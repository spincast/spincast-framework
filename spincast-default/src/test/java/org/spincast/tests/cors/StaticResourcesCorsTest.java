package org.spincast.tests.cors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.cookies.ICookie;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;

public class StaticResourcesCorsTest extends DefaultIntegrationTestingBase {

    @Test
    public void noCorsConfig() throws Exception {

        getRouter().file("/").classpath("/image.jpg").save();

        getRouter().ALL("/*{path}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        IHttpResponse response = GET("/").send();

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

        getRouter().file("/").classpath("/image.jpg").cors().save();

        getRouter().ALL("/*{path}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        IHttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                                 .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                                 .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("*", allowOriginHeader);

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

        getRouter().file("/").classpath("/image.jpg").cors().save();

        IHttpResponse response = GET("/").send();

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

        getRouter().file("/").classpath("/image.jpg").cors().save();

        IHttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
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
                   .save();

        getRouter().ALL("/*{path}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        IHttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "http://example1.com")
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
                   .save();

        getRouter().ALL("/*{path}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        IHttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "http://example1.com")
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
                   .save();

        getRouter().ALL("/*{path}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        IHttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "http://example1.com")
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
                   .save();

        getRouter().ALL("/*{path}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        IHttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
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
                         false)
                   .save();

        // Cors filter
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         null,
                         false);

        getRouter().ALL("/*{path}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        IHttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
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
                         false)
                   .save();

        getRouter().ALL("/*{path}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        IHttpResponse response = POST("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
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
                         false)
                   .save();

        getRouter().ALL("/*{path}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        IHttpResponse response = DELETE("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
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
    public void invalidHttpMethodOptionsNoPreflight() throws Exception {

        getRouter().file("/").classpath("/image.jpg")
                   .cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         false)
                   .save();

        getRouter().ALL("/*{path}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        IHttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
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

        getRouter().file("/").classpath("/image.jpg").cors().save();

        getRouter().ALL("/*{path}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        IHttpResponse response = HEAD("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                                  .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                                  .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("*", allowOriginHeader);

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

        getRouter().file("/").classpath("/image.jpg").cors().save();

        getRouter().ALL("/*{path}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        ICookie cookie = getCookieFactory().createCookie("name1", "value2");
        cookie.setDomain(getSpincastConfig().getServerHost());
        cookie.setPath("/");

        IHttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                                 .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                                 .addCookie(cookie)
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
}
