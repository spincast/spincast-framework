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
import org.spincast.core.filters.SpincastFilters;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;

public class CorsBeforeFilterTest extends NoAppStartHttpServerTestingBase {

    @Inject
    protected SpincastFilters<DefaultRequestContext> spincastFilters;

    @Test
    public void corsFilterButNoCorsHeaders() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context);
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
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
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void sameOriginWithCorsHeaders() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context);
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

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
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void corsFilterDefault() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context);
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
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
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void corsFilterSpecificOriginOnlyValid() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("http://example1.com",
                                                                               "https://example1.com"));
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
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
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void corsFilterSpecificOriginOnlyInvalid() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context, Sets.newHashSet("http://example3.com"));
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

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

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context, Sets.newHashSet("https://example1.com"));
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

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

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("https://example1.com"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"));
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
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
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void noCookies() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("https://example1.com"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"),
                                                               null,
                                                               false);
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
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
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void allowedMethodsNotIncludedInSimpleCorsRequests() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("https://example1.com"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"),
                                                               Sets.newHashSet("extra-header-to-send-1",
                                                                               "extra-header-to-send-2"),
                                                               false,
                                                               Sets.newHashSet(HttpMethod.PATCH));
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                        .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                        .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

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
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void simpleRequestWithPost() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("https://example1.com"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"),
                                                               null,
                                                               false,
                                                               Sets.newHashSet(HttpMethod.PATCH));
            }
        });

        getRouter().POST("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = POST("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                         .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                         .addHeaderValue(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                                         .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

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
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void simpleRequestWithHead() throws Exception {

        final boolean[] inHandler = new boolean[]{false};

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("https://example1.com"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"),
                                                               null,
                                                               false,
                                                               Sets.newHashSet(HttpMethod.PATCH));
            }
        });

        getRouter().HEAD("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                inHandler[0] = true;
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = HEAD("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                         .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                         .send();

        assertTrue(inHandler[0]);

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

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
        assertTrue(StringUtils.isBlank(response.getContentAsString())); // no content on a HEAD request!
    }

    @Test
    public void preflightDefault() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context);
            }
        });

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
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
        for (HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void preflightNoMainHandler() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context);
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
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
        for (HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void preflightDefaultAllExtraHeadersToBeSentAreAllowed() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context);
            }
        });

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                                                            "extra-header-to-be-sent-3,extra-header-to-be-sent-4")
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
        assertTrue("extra-header-to-be-sent-3,extra-header-to-be-sent-4".equals(allowHeadersHeader) ||
                   "extra-header-to-be-sent-4,extra-header-to-be-sent-3".equals(allowHeadersHeader));

        String allowMethodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNotNull(allowMethodsHeader);
        Set<String> methods = new HashSet<>(Arrays.asList(StringUtils.split(allowMethodsHeader, ",")));
        for (HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void preflightSpecificOriginValid() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("http://example1.com",
                                                                               "https://example1.com"));
            }
        });

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
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

        String allowMethodHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNotNull(allowMethodHeader);
        Set<String> methods = new HashSet<>(Arrays.asList(StringUtils.split(allowMethodHeader, ",")));
        for (HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void preflightSpecificOriginInvalid() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context, Sets.newHashSet("http://example3.com"));
            }
        });

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
                                            .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNull(allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader);

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNull(allowHeadersHeader);

        String allowMethodHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMethodHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void preflightExtraHeadersToBeSentValid() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("https://example1.com"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"),
                                                               Sets.newHashSet("extra-header-to-be-sent-1",
                                                                               "extra-header-to-be-sent-2"));
            }
        });

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                                                            "extra-header-to-be-sent-1,extra-header-to-be-sent-2")
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
        assertTrue("extra-header-to-be-sent-1,extra-header-to-be-sent-2".equals(allowHeadersHeader) ||
                   "extra-header-to-be-sent-2,extra-header-to-be-sent-1".equals(allowHeadersHeader));

        String allowMethodHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNotNull(allowMethodHeader);
        Set<String> methods = new HashSet<>(Arrays.asList(StringUtils.split(allowMethodHeader, ",")));
        for (HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void preflightExtraHeadersToBeSentWildcard() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("https://example1.com"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"),
                                                               Sets.newHashSet("extra-header-to-be-sent-1",
                                                                               "*"));
            }
        });

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                                                            "extra-header-to-be-sent-1,extra-header-to-be-sent-2,extra-header-to-be-sent-3")
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
        Set<String> allowHeadersHeaderSet = new HashSet<>(Arrays.asList(StringUtils.split(allowHeadersHeader, ",")));
        assertEquals(3, allowHeadersHeaderSet.size());
        assertTrue(allowHeadersHeaderSet.contains("extra-header-to-be-sent-1"));
        assertTrue(allowHeadersHeaderSet.contains("extra-header-to-be-sent-2"));
        assertTrue(allowHeadersHeaderSet.contains("extra-header-to-be-sent-3"));

        String allowMethodHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNotNull(allowMethodHeader);
        Set<String> methods = new HashSet<>(Arrays.asList(StringUtils.split(allowMethodHeader, ",")));
        for (HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void preflightExtraHeadersToSentInvalid() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("https://example1.com"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"),
                                                               Sets.newHashSet("extra-header-to-send-1", "extra-header-2"));
            }
        });

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                                                            "extra-header-3")
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
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void preflightRequestForExtraHeadersToBeSentButNoneAllowed() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("*"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"),
                                                               null);
            }
        });

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                                                            "extra-header-to-be-sent-1")
                                            .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNull(allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader);

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNull(allowHeadersHeader);

        String allowMethodHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMethodHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void preflighNoCookies() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("https://example1.com"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"),
                                                               Sets.newHashSet("extra-header-to-be-sent-1",
                                                                               "extra-header-to-be-sent-2"),
                                                               false);
            }
        });

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
                                            .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader); // simple request header only

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNotNull(allowHeadersHeader);
        assertTrue("extra-header-to-be-sent-1,extra-header-to-be-sent-2".equals(allowHeadersHeader) ||
                   "extra-header-to-be-sent-2,extra-header-to-be-sent-1".equals(allowHeadersHeader));

        String allowMethodHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNotNull(allowMethodHeader);
        Set<String> methods = new HashSet<>(Arrays.asList(StringUtils.split(allowMethodHeader, ",")));
        for (HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void preflightHttpMethodsValid() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("https://example1.com"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"),
                                                               Sets.newHashSet("extra-header-to-be-sent-1",
                                                                               "extra-header-to-be-sent-2"),
                                                               false,
                                                               Sets.newHashSet(HttpMethod.DELETE, HttpMethod.PUT));
            }
        });

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
                                            .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader); // simple request header only

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNotNull(allowHeadersHeader);
        assertTrue("extra-header-to-be-sent-1,extra-header-to-be-sent-2".equals(allowHeadersHeader) ||
                   "extra-header-to-be-sent-2,extra-header-to-be-sent-1".equals(allowHeadersHeader));

        String allowMethodHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNotNull(allowMethodHeader);
        Set<String> methods = new HashSet<>(Arrays.asList(StringUtils.split(allowMethodHeader, ",")));
        assertEquals(3, methods.size());
        assertTrue(methods.contains(HttpMethod.DELETE.name()));
        assertTrue(methods.contains(HttpMethod.PUT.name()));
        assertTrue(methods.contains(HttpMethod.OPTIONS.name())); // Always allowed

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void preflightHttpMethodsInvalid() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("https://example1.com"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"),
                                                               Sets.newHashSet("extra-header-to-be-sent-1",
                                                                               "extra-header-to-be-sent-2"),
                                                               false,
                                                               Sets.newHashSet(HttpMethod.DELETE, HttpMethod.PUT));
            }
        });

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,TRACE")
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
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void preflightMaxAge() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("https://example1.com"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"),
                                                               Sets.newHashSet("extra-header-to-be-sent-1",
                                                                               "extra-header-to-be-sent-2"),
                                                               false,
                                                               Sets.newHashSet(HttpMethod.DELETE, HttpMethod.PUT),
                                                               123);
            }
        });

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
                                            .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader); // simple request header only

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNotNull(allowHeadersHeader);
        assertTrue("extra-header-to-be-sent-1,extra-header-to-be-sent-2".equals(allowHeadersHeader) ||
                   "extra-header-to-be-sent-2,extra-header-to-be-sent-1".equals(allowHeadersHeader));

        String allowMethodHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNotNull(allowMethodHeader);
        Set<String> methods = new HashSet<>(Arrays.asList(StringUtils.split(allowMethodHeader, ",")));
        assertEquals(3, methods.size());
        assertTrue(methods.contains(HttpMethod.DELETE.name()));
        assertTrue(methods.contains(HttpMethod.PUT.name()));
        assertTrue(methods.contains(HttpMethod.OPTIONS.name())); // Always allowed

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("123", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void preflightMaxAgeZero() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("https://example1.com"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"),
                                                               Sets.newHashSet("extra-header-to-be-sent-1",
                                                                               "extra-header-to-be-sent-2"),
                                                               false,
                                                               Sets.newHashSet(HttpMethod.DELETE, HttpMethod.PUT),
                                                               0);
            }
        });

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
                                            .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader); // simple request header only

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNotNull(allowHeadersHeader);
        assertTrue("extra-header-to-be-sent-1,extra-header-to-be-sent-2".equals(allowHeadersHeader) ||
                   "extra-header-to-be-sent-2,extra-header-to-be-sent-1".equals(allowHeadersHeader));

        String allowMethodHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNotNull(allowMethodHeader);
        Set<String> methods = new HashSet<>(Arrays.asList(StringUtils.split(allowMethodHeader, ",")));
        assertEquals(3, methods.size());
        assertTrue(methods.contains(HttpMethod.DELETE.name()));
        assertTrue(methods.contains(HttpMethod.PUT.name()));
        assertTrue(methods.contains(HttpMethod.OPTIONS.name())); // Always allowed

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void preflightMaxAgeLessThanZero() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context,
                                                               Sets.newHashSet("https://example1.com"),
                                                               Sets.newHashSet("extra-header-to-be-read-1",
                                                                               "extra-header-to-be-read-2"),
                                                               Sets.newHashSet("extra-header-to-be-sent-1",
                                                                               "extra-header-to-be-sent-2"),
                                                               false,
                                                               Sets.newHashSet(HttpMethod.DELETE, HttpMethod.PUT),
                                                               -123);
            }
        });

        getRouter().OPTIONS("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
                                            .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNull(allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader); // simple request header only

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNotNull(allowHeadersHeader);
        assertTrue("extra-header-to-be-sent-1,extra-header-to-be-sent-2".equals(allowHeadersHeader) ||
                   "extra-header-to-be-sent-2,extra-header-to-be-sent-1".equals(allowHeadersHeader));

        String allowMethodHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNotNull(allowMethodHeader);
        Set<String> methods = new HashSet<>(Arrays.asList(StringUtils.split(allowMethodHeader, ",")));
        assertEquals(3, methods.size());
        assertTrue(methods.contains(HttpMethod.DELETE.name()));
        assertTrue(methods.contains(HttpMethod.PUT.name()));
        assertTrue(methods.contains(HttpMethod.OPTIONS.name())); // Always allowed

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void wildcardNotValidWhenThereAreCookies() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context);
            }
        });

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
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
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

    /**
     * An "OPTIONS" request is made by the browser for Cors checking 
     * and it should be handled even if there is no OPTIONS route.
     */
    @Test
    public void preflightNoOptionsMethod() throws Exception {

        // Cors filter
        getRouter().ALL().pos(-10).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context);
            }
        });

        getRouter().DELETE("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
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
        for (HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    /**
     * By default the position "-1" will work to define a Cors filter
     * because it applies to all route types.
     * 
     * But if we add the Cors filter only for the "Normal"
     * route type, it may not work.
     */
    @Test
    public void corsOnlyOnFoundRoutingTypeFails() throws Exception {

        // Cors filter
        getRouter().ALL("/*{path}").pos(-1).found().handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context);
            }
        });

        getRouter().DELETE("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
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
    }

    @Test
    public void corsOnlyOnNotFoundRoutingTypeSuccess() throws Exception {

        // Cors filter
        getRouter().ALL("/*{path}").notFound().pos(-1).handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                CorsBeforeFilterTest.this.spincastFilters.cors(context);
            }
        });

        getRouter().DELETE("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                fail();
            }
        });

        HttpResponse response = OPTIONS("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                            .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                            .addHeaderValue(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT")
                                            .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNotNull(allowCredentialsHeader);
        assertEquals("true", allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNull(exposeHeadersHeader);

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNotNull(allowHeadersHeader);
        assertEquals("", allowHeadersHeader);

        String allowMethodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNotNull(allowMethodsHeader);
        Set<String> methods = new HashSet<>(Arrays.asList(StringUtils.split(allowMethodsHeader, ",")));
        for (HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

}
