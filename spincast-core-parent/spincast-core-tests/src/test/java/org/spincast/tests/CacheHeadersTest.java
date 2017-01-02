package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.ETag;
import org.spincast.core.routing.ETagFactory;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.commons.lang3.time.DateUtils;
import org.spincast.shaded.org.apache.http.HttpHeaders;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;

public class CacheHeadersTest extends IntegrationTestNoAppDefaultContextsBase {

    @Inject
    protected ETagFactory eTagFactory;

    protected ETagFactory getETagFactory() {
        return this.eTagFactory;
    }

    @Test
    public void eTagIfNoneMatchMatching() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag).lastModified(lastModified).validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, eTagHeader).send();

        assertEquals(HttpStatus.SC_NOT_MODIFIED, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("", response.getContentAsString());

        eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfNoneMatchNotMatching() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag).lastModified(lastModified).validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "\"nope\"").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfNoneMatchNotMatchingWeakComparison() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag).lastModified(lastModified).validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "\"nope\"").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfNoneMatchChanged() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            private int nbr = 1;

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = (this.nbr == 1) ? "123" : "456";
                this.nbr++;

                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag).lastModified(lastModified).validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, eTagHeader).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("456", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfNoneMatchWeakRequestStrongComparison() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag).lastModified(lastModified).validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "W/\"123\"").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfNoneMatchWeakRequestWeakComparison() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag, false, true).lastModified(lastModified)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "W/\"123\"").send();

        assertEquals(HttpStatus.SC_NOT_MODIFIED, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfNoneMatchWeakActualStrongComparison() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag, true).lastModified(lastModified)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "\"123\"").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertTrue(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfNoneMatchWeakActualWeakComparison() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag, true, true).lastModified(lastModified)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "\"123\"").send();

        assertEquals(HttpStatus.SC_NOT_MODIFIED, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertTrue(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfNoneMatchBothWeakStrongComparison() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag, true).lastModified(lastModified)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "W/\"123\"").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertTrue(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfNoneMatchBothWeakWeakComparison() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag, true, true).lastModified(lastModified)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "W/\"123\"").send();

        assertEquals(HttpStatus.SC_NOT_MODIFIED, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertTrue(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfMatchHeaderMatch() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag).lastModified(lastModified)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "\"123\"").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfMatchHeaderNoMatch() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag).lastModified(lastModified)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "\"nope\"").send();

        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void eTagIfMatchWildcardedResourceExist() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag).lastModified(lastModified)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "*").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfMatchWildcardedResourceDoesntExist() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().validate(false)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "*").send();

        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNull(eTagHeader);
    }

    @Test
    public void ifMatchResourceDoesntExist() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().validate(false)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "\"nope\"").send();

        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNull(eTagHeader);
    }

    @Test
    public void eTagIfNoneMatchWildcardedResourceExist() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag).lastModified(lastModified)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "*").send();

        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfNoneMatchWildcardedResourceDoesntExist() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = null;
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag).lastModified(lastModified)
                          .validate(false)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "*").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNull(eTagHeader);
    }

    @Test
    public void eTagIfMatchMultipleValuesMatch() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag).lastModified(lastModified)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "\"111\",\"123\"").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfMatchMultipleValuesNoMatch() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag).lastModified(lastModified)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "\"111\",\"222\"").send();
        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void eTagIfMatchMultipleValuesMatch2() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                Date lastModified = null;
                if(context.cacheHeaders().eTag(eTag).lastModified(lastModified)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        List<String> values = new ArrayList<String>();
        values.add("\"111\",\"222\"");
        values.add("\"123\"");
        HttpResponse response = GET("/").addHeaderValues(HttpHeaders.IF_MATCH, values).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfMatchMultipleValuesNoMatch2() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                if(context.cacheHeaders().eTag(eTag).validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        List<String> values = new ArrayList<String>();
        values.add("\"111\",\"222\"");
        values.add("\"333\"");
        HttpResponse response = GET("/").addHeaderValues(HttpHeaders.IF_MATCH, values).send();

        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void eTagIfNoneMatchMultipleValuesMatch() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                if(context.cacheHeaders().eTag(eTag).validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "\"111\",\"123\"").send();

        assertEquals(HttpStatus.SC_NOT_MODIFIED, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfNoneMatchMultipleValuesNoMatch() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                if(context.cacheHeaders().eTag(eTag).validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "\"111\",\"222\"").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfNoneMatchMultipleValuesMatch2() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                if(context.cacheHeaders().eTag(eTag).validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        List<String> values = new ArrayList<String>();
        values.add("\"111\",\"222\"");
        values.add("\"123\"");
        HttpResponse response = GET("/").addHeaderValues(HttpHeaders.IF_NONE_MATCH, values).send();

        assertEquals(HttpStatus.SC_NOT_MODIFIED, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void eTagIfNoneMatchMultipleValuesNoMatch2() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                String eTag = "123";
                if(context.cacheHeaders().eTag(eTag).validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        List<String> values = new ArrayList<String>();
        values.add("\"111\",\"222\"");
        values.add("\"333\"");
        HttpResponse response = GET("/").addHeaderValues(HttpHeaders.IF_NONE_MATCH, values).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void multipleValues() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                List<ETag> eTags = context.request().getEtagsFromIfNoneMatchHeader();
                assertNotNull(eTags);
                assertEquals(5, eTags.size());

                Map<String, ETag> etagsByTag = new HashMap<>();
                for(ETag eTag : eTags) {
                    etagsByTag.put(eTag.getTag(), eTag);
                }

                ETag eTag = etagsByTag.get("111");
                assertNotNull(eTag);
                assertFalse(eTag.isWeak());
                assertFalse(eTag.isWildcard());

                eTag = etagsByTag.get("222");
                assertNotNull(eTag);
                assertTrue(eTag.isWeak());
                assertFalse(eTag.isWildcard());

                eTag = etagsByTag.get("333");
                assertNotNull(eTag);
                assertFalse(eTag.isWeak());
                assertFalse(eTag.isWildcard());

                eTag = etagsByTag.get("444");
                assertNotNull(eTag);
                assertTrue(eTag.isWeak());
                assertFalse(eTag.isWildcard());

                eTag = etagsByTag.get("*");
                assertNotNull(eTag);
                assertFalse(eTag.isWeak());
                assertTrue(eTag.isWildcard());

                context.response().sendPlainText("test");
            }
        });

        List<String> values = new ArrayList<String>();
        values.add("\"111\",W/\"222\"");
        values.add("\"333\"");
        HttpResponse response = GET("/").addHeaderValues(HttpHeaders.IF_NONE_MATCH, values)
                                        .addHeaderValue(HttpHeaders.IF_NONE_MATCH, "W/\"444\",*").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());
    }

    @Test
    public void lastModifiedNotModified() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().lastModified(date)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MODIFIED_SINCE, formatDate(date)).send();
        assertEquals(HttpStatus.SC_NOT_MODIFIED, response.getStatus());
        assertEquals("", response.getContentAsString());
        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date, parseDate(lastModified));
    }

    @Test
    public void lastModifiedModified() throws Exception {

        final Date oldDate = SpincastTestUtils.getTestDateNoTime();
        final Date newDate = DateUtils.addSeconds(oldDate, 2);

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().lastModified(newDate)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MODIFIED_SINCE, formatDate(oldDate)).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(newDate, parseDate(lastModified));
    }

    @Test
    public void lastModifiedInRequestOnly() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123")
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MODIFIED_SINCE, formatDate(date)).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNull(lastModified);
    }

    @Test
    public void lastModifiedNotInRequest() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().lastModified(date)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date, parseDate(lastModified));
    }

    @Test
    public void lastModifiedInvalidFormat() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().lastModified(date)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MODIFIED_SINCE, "nope").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date, parseDate(lastModified));
    }

    @Test
    public void ifUnmodifiedSinceAndResourceDoesntExist() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().validate(false)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_UNMODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNull(eTagHeader);
    }

    @Test
    public void ifUnmodifiedMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().lastModified(date)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_UNMODIFIED_SINCE, formatDate(date)).send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date, parseDate(lastModified));
    }

    @Test
    public void ifUnmodifiedNoMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();
        final Date date2 = DateUtils.addSeconds(date, 2);

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().lastModified(date2)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_UNMODIFIED_SINCE, formatDate(date)).send();
        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date2, parseDate(lastModified));
    }

    @Test
    public void ifNoneMatchMatchesAndIfUnmodifiedMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "\"123\"")
                                        .addHeaderValue(HttpHeaders.IF_UNMODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date, parseDate(lastModified));
    }

    @Test
    public void ifNoneMatchNoMatchesAndIfUnmodifiedMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "\"nope\"")
                                        .addHeaderValue(HttpHeaders.IF_UNMODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date, parseDate(lastModified));
    }

    @Test
    public void ifNoneMatchMatchesAndifUnmodifiedNoMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();
        final Date date2 = DateUtils.addSeconds(date, 2);

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date2)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "\"123\"")
                                        .addHeaderValue(HttpHeaders.IF_UNMODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date2, parseDate(lastModified));
    }

    @Test
    public void ifNoneMatchNoMatchesAndifUnmodifiedNoMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();
        final Date date2 = DateUtils.addSeconds(date, 2);

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date2)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "\"nope\"")
                                        .addHeaderValue(HttpHeaders.IF_UNMODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date2, parseDate(lastModified));
    }

    @Test
    public void ifMatchMatchesAndifUnmodifiedMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "\"123\"")
                                        .addHeaderValue(HttpHeaders.IF_UNMODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date, parseDate(lastModified));
    }

    @Test
    public void ifMatchNoMatchesAndifUnmodifiedMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "\"nope\"")
                                        .addHeaderValue(HttpHeaders.IF_UNMODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date, parseDate(lastModified));
    }

    @Test
    public void ifMatchMatchesAndifUnmodifiedNoMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();
        final Date date2 = DateUtils.addSeconds(date, 2);

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date2)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "\"123\"")
                                        .addHeaderValue(HttpHeaders.IF_UNMODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date2, parseDate(lastModified));
    }

    @Test
    public void ifMatchNoMatchesAndifUnmodifiedNoMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();
        final Date date2 = DateUtils.addSeconds(date, 2);

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date2)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "\"nope\"")
                                        .addHeaderValue(HttpHeaders.IF_UNMODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date2, parseDate(lastModified));
    }

    @Test
    public void ifNoneMatchMatchesAndifModifiedMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "\"123\"")
                                        .addHeaderValue(HttpHeaders.IF_MODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_NOT_MODIFIED, response.getStatus());
        assertEquals("", response.getContentAsString());
        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date, parseDate(lastModified));

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());
    }

    @Test
    public void ifNoneMatchNoMatchesAndifModifiedMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "\"nope\"")
                                        .addHeaderValue(HttpHeaders.IF_MODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date, parseDate(lastModified));
    }

    @Test
    public void ifNoneMatchMatchesAndifModifiedNoMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();
        final Date date2 = DateUtils.addSeconds(date, 2);

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date2)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "\"123\"")
                                        .addHeaderValue(HttpHeaders.IF_MODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date2, parseDate(lastModified));
    }

    @Test
    public void ifNoneMatchNoMatchesAndifModifiedNoMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();
        final Date date2 = DateUtils.addSeconds(date, 2);

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date2)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_NONE_MATCH, "\"nope\"")
                                        .addHeaderValue(HttpHeaders.IF_MODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date2, parseDate(lastModified));
    }

    @Test
    public void ifMatchMatchesAndifModifiedMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "\"123\"")
                                        .addHeaderValue(HttpHeaders.IF_MODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date, parseDate(lastModified));
    }

    @Test
    public void ifMatchNoMatchesAndifModifiedMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "\"nope\"")
                                        .addHeaderValue(HttpHeaders.IF_MODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date, parseDate(lastModified));
    }

    @Test
    public void ifMatchMatchesAndifModifiedNoMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();
        final Date date2 = DateUtils.addSeconds(date, 2);

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date2)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "\"123\"")
                                        .addHeaderValue(HttpHeaders.IF_MODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date2, parseDate(lastModified));
    }

    @Test
    public void ifMatchNoMatchesAndifModifiedNoMatches() throws Exception {

        final Date date = SpincastTestUtils.getTestDateNoTime();
        final Date date2 = DateUtils.addSeconds(date, 2);

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().eTag("123").lastModified(date2)
                          .validate(true)) {
                    return;
                }

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.IF_MATCH, "\"nope\"")
                                        .addHeaderValue(HttpHeaders.IF_MODIFIED_SINCE, formatDate(date)).send();

        assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
        assertEquals("", response.getContentAsString());

        String eTagHeader = response.getHeaderFirst(HttpHeaders.ETAG);
        assertNotNull(eTagHeader);
        ETag eTag = getETagFactory().deserializeHeaderValue(eTagHeader);

        assertEquals("123", eTag.getTag());
        assertFalse(eTag.isWeak());
        assertFalse(eTag.isWildcard());

        String lastModified = response.getHeaderFirst(HttpHeaders.LAST_MODIFIED);
        assertNotNull(lastModified);
        assertEquals(date2, parseDate(lastModified));
    }

    @Test
    public void noCache() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.cacheHeaders().noCache();

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        assertNull(response.getHeaderFirst(HttpHeaders.ETAG));
        assertNull(response.getHeaderFirst(HttpHeaders.LAST_MODIFIED));

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNotNull(pragma);
        assertEquals("no-cache", pragma);

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("no-cache, no-store, max-age=0, must-revalidate, proxy-revalidate", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);
        Date expiresDate = parseDate(expires);
        assertTrue((new Date()).getTime() > expiresDate.getTime());
    }

    @Test
    public void noCacheUsingCacheForZero() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.cacheHeaders().cache(0);

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        assertNull(response.getHeaderFirst(HttpHeaders.ETAG));
        assertNull(response.getHeaderFirst(HttpHeaders.LAST_MODIFIED));

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNotNull(pragma);
        assertEquals("no-cache", pragma);

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("no-cache, no-store, max-age=0, must-revalidate, proxy-revalidate", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);
        Date expiresDate = parseDate(expires);
        assertTrue((new Date()).getTime() > expiresDate.getTime());
    }

    @Test
    public void noCacheUsingCacheForLessThanZero() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                context.cacheHeaders().cache(-123);

                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        assertNull(response.getHeaderFirst(HttpHeaders.ETAG));
        assertNull(response.getHeaderFirst(HttpHeaders.LAST_MODIFIED));

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNotNull(pragma);
        assertEquals("no-cache", pragma);

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("no-cache, no-store, max-age=0, must-revalidate, proxy-revalidate", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);
        Date expiresDate = parseDate(expires);
        assertTrue((new Date()).getTime() > expiresDate.getTime());
    }

    @Test
    public void cacheForDefault() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().cache(123).validate(true)) {
                    return;
                }
                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("public, max-age=123", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheForPrivate() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().cache(123, true).validate(true)) {
                    return;
                }
                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("private, max-age=123", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

    @Test
    public void cacheForPrivateCdnSeconds() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                if(context.cacheHeaders().cache(123, true, 456).validate(true)) {
                    return;
                }
                context.response().sendPlainText("test");
            }
        });

        HttpResponse response = GET("/").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("test", response.getContentAsString());

        String cacheControl = response.getHeaderFirst(HttpHeaders.CACHE_CONTROL);
        assertNotNull(cacheControl);
        assertEquals("private, max-age=123, s-maxage=456", cacheControl);

        String expires = response.getHeaderFirst(HttpHeaders.EXPIRES);
        assertNotNull(expires);

        String pragma = response.getHeaderFirst(HttpHeaders.PRAGMA);
        assertNull(pragma);
    }

}
