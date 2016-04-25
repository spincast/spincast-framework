package org.spincast.tests.cors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.filters.ISpincastFilters;
import org.spincast.core.routing.HttpMethod;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.shaded.org.apache.http.impl.cookie.BasicClientCookie;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;

public class CorsDirectTest extends DefaultIntegrationTestingBase {

    @Inject
    protected ISpincastFilters<IDefaultRequestContext> spincastFilters;

    @Test
    public void corsFilterButNoCorsHeaders() throws Exception {

        // Cors filter
        getRouter().cors();

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        SpincastTestHttpResponse response = get("/");

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
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void sameOriginWithCorsHeaders() throws Exception {

        // Cors filter
        getRouter().cors();

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example1.com");

        SpincastTestHttpResponse response = get("/", headers);

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
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void corsFilterDefault() throws Exception {

        // Cors filter
        getRouter().cors();

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");

        SpincastTestHttpResponse response = get("/", headers);

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
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void corsFilterSpecificOriginOnlyValid() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("http://example1.com", "https://example1.com"));

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "http://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");

        SpincastTestHttpResponse response = get("/", headers);

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
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void corsFilterSpecificOriginOnlyInvalid() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("http://example3.com"));

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "http://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");

        SpincastTestHttpResponse response = get("/", headers);

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
        getRouter().cors(Sets.newHashSet("https://example1.com"));

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "http://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");

        SpincastTestHttpResponse response = get("/", headers);

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
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"));

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");

        SpincastTestHttpResponse response = get("/", headers);

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
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void noCookies() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         null,
                         false);

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");

        SpincastTestHttpResponse response = get("/", headers);

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
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void allowedMethodsNotIncludedInSimpleCorsRequests() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         Sets.newHashSet("extra-header-to-send-1",
                                         "extra-header-to-send-2"),
                         false,
                         Sets.newHashSet(HttpMethod.PATCH));

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");

        SpincastTestHttpResponse response = get("/", headers);

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
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void simpleRequestWithPost() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         null,
                         false,
                         Sets.newHashSet(HttpMethod.PATCH));

        getRouter().POST("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

        SpincastTestHttpResponse response = postWithHeaders("/", headers);

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
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void simpleRequestWithHead() throws Exception {

        final boolean[] inHandler = new boolean[]{false};

        // Cors filter
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         null,
                         false,
                         Sets.newHashSet(HttpMethod.PATCH));

        getRouter().HEAD("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                inHandler[0] = true;
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.HEAD, createTestUrl("/"), headers, null, null);

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
        assertEquals(null, response.getContent()); // no content on a HEAD request!
    }

    @Test
    public void preflightDefault() throws Exception {

        // Cors filter
        getRouter().cors();

        getRouter().OPTIONS("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("*", allowOriginHeader);

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
        for(HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContent());
    }

    @Test
    public void preflightNoMainHandler() throws Exception {

        // Cors filter
        getRouter().cors();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("*", allowOriginHeader);

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
        for(HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContent());
    }

    @Test
    public void preflightDefaultAllExtraHeadersToBeSentAreAllowed() throws Exception {

        // Cors filter
        getRouter().cors();

        getRouter().OPTIONS("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "extra-header-to-be-sent-3,extra-header-to-be-sent-4");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("*", allowOriginHeader);

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
        for(HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContent());
    }

    @Test
    public void preflightSpecificOriginValid() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("http://example1.com", "https://example1.com"));

        getRouter().OPTIONS("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

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
        for(HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContent());
    }

    @Test
    public void preflightSpecificOriginInvalid() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("http://example3.com"));

        getRouter().OPTIONS("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

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
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         Sets.newHashSet("extra-header-to-be-sent-1",
                                         "extra-header-to-be-sent-2"));

        getRouter().OPTIONS("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "extra-header-to-be-sent-1,extra-header-to-be-sent-2");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

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
        for(HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContent());
    }

    @Test
    public void preflightExtraHeadersToBeSentWildcard() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         Sets.newHashSet("extra-header-to-be-sent-1",
                                         "*"));

        getRouter().OPTIONS("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                    "extra-header-to-be-sent-1,extra-header-to-be-sent-2,extra-header-to-be-sent-3");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

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
        for(HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContent());
    }

    @Test
    public void preflightExtraHeadersToSentInvalid() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         Sets.newHashSet("extra-header-to-send-1", "extra-header-2"));

        getRouter().OPTIONS("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "extra-header-3");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

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
        assertEquals("", response.getContent());
    }

    @Test
    public void preflightRequestForExtraHeadersToBeSentButNoneAllowed() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("*"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         null);

        getRouter().OPTIONS("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "extra-header-to-be-sent-1");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

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
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         Sets.newHashSet("extra-header-to-be-sent-1",
                                         "extra-header-to-be-sent-2"),
                         false);

        getRouter().OPTIONS("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

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
        for(HttpMethod availableMethod : HttpMethod.values()) {
            assertTrue(methods.contains(availableMethod.name()));
        }

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNotNull(maxAgeHeader);
        assertEquals("86400", maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("", response.getContent());
    }

    @Test
    public void preflightHttpMethodsValid() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         Sets.newHashSet("extra-header-to-be-sent-1",
                                         "extra-header-to-be-sent-2"),
                         false,
                         Sets.newHashSet(HttpMethod.DELETE, HttpMethod.PUT));

        getRouter().OPTIONS("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

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
        assertEquals("", response.getContent());
    }

    @Test
    public void preflightHttpMethodsInvalid() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         Sets.newHashSet("extra-header-to-be-sent-1",
                                         "extra-header-to-be-sent-2"),
                         false,
                         Sets.newHashSet(HttpMethod.DELETE, HttpMethod.PUT));

        getRouter().OPTIONS("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,TRACE");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

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
        assertEquals("", response.getContent());
    }

    @Test
    public void preflightMaxAge() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         Sets.newHashSet("extra-header-to-be-sent-1",
                                         "extra-header-to-be-sent-2"),
                         false,
                         Sets.newHashSet(HttpMethod.DELETE, HttpMethod.PUT),
                         123);

        getRouter().OPTIONS("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

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
        assertEquals("", response.getContent());
    }

    @Test
    public void preflightMaxAgeZero() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         Sets.newHashSet("extra-header-to-be-sent-1",
                                         "extra-header-to-be-sent-2"),
                         false,
                         Sets.newHashSet(HttpMethod.DELETE, HttpMethod.PUT),
                         0);

        getRouter().OPTIONS("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

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
        assertEquals("", response.getContent());
    }

    @Test
    public void preflightMaxAgeLessThanZero() throws Exception {

        // Cors filter
        getRouter().cors(Sets.newHashSet("https://example1.com"),
                         Sets.newHashSet("extra-header-to-be-read-1",
                                         "extra-header-to-be-read-2"),
                         Sets.newHashSet("extra-header-to-be-sent-1",
                                         "extra-header-to-be-sent-2"),
                         false,
                         Sets.newHashSet(HttpMethod.DELETE, HttpMethod.PUT),
                         -123);

        getRouter().OPTIONS("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                fail();
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");
        headers.put(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE,PUT");

        SpincastTestHttpResponse response = methodWithUrl(HttpMethod.OPTIONS, createTestUrl("/"), headers, null, null);

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
        assertEquals("", response.getContent());
    }

    @Test
    public void wildcardNotValidWhenThereAreCookies() throws Exception {

        // Cors filter
        getRouter().cors();

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");

        BasicClientCookie cookie = new BasicClientCookie("name1", "value2");
        cookie.setDomain(getSpincastConfig().getServerHost());
        cookie.setPath("/");
        getCookieStore().addCookie(cookie);

        SpincastTestHttpResponse response = get("/", headers);

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
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void filterDirectPathValid() throws Exception {

        // Cors filter
        getRouter().cors("/");

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");

        SpincastTestHttpResponse response = get("/", headers);

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
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void filterDirectPathValidSplat() throws Exception {

        // Cors filter
        getRouter().cors("/*{path}");

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");

        SpincastTestHttpResponse response = get("/", headers);

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
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void filterDirectPathInvalid() throws Exception {

        // Cors filter
        getRouter().cors("/nope");

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");

        SpincastTestHttpResponse response = get("/", headers);

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
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

    @Test
    public void filterDirectPathInvalidDynamic() throws Exception {

        // Cors filter
        getRouter().cors("/${param}");

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ORIGIN, "https://example1.com");
        headers.put(HttpHeaders.HOST, "example2.com");

        SpincastTestHttpResponse response = get("/", headers);

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
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContent());
    }

}
