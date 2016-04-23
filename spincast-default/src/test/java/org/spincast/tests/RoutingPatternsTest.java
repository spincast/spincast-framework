package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.DefaultRouteParamAliasesBinder;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestHttpResponse;

import com.google.inject.Inject;

public class RoutingPatternsTest extends DefaultIntegrationTestingBase {

    @Inject
    protected DefaultRouteParamAliasesBinder<IDefaultRequestContext> defaultPredefinedRouteParamPatternsBinder;

    @Test
    public void paramNoPattern() throws Exception {

        getRouter().GET("/${param1}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(context.request().getPathParam("param1"));
            }
        });

        SpincastTestHttpResponse response = get("/one");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("one", response.getContent());
    }

    @Test
    public void paramNoPatternNoName() throws Exception {

        getRouter().GET("/${}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertEquals(0, context.request().getPathParams().size());
            }
        });

        SpincastTestHttpResponse response = get("/one123");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test
    public void paramPattern1() throws Exception {

        getRouter().GET("/${param1:a+}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(context.request().getPathParam("param1"));
            }
        });

        SpincastTestHttpResponse response = get("/a");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a", response.getContent());

        response = get("/aaaaa");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("aaaaa", response.getContent());

        response = get("/");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/a/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/aaaaa/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void paramPatternNumeric() throws Exception {

        getRouter().GET("/users/${param1:\\d+}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(context.request().getPathParam("param1"));
            }
        });

        SpincastTestHttpResponse response = get("/users/1");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1", response.getContent());

        response = get("/users/123");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("123", response.getContent());

        response = get("/users/a");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/users/a/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

    }

    @Test
    public void emptyPattern() throws Exception {

        getRouter().GET("/${param1:}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(context.request().getPathParam("param1"));
            }
        });

        SpincastTestHttpResponse response = get("/one");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("one", response.getContent());

        response = get("/b123");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("b123", response.getContent());
    }

    @Test
    public void paramPredefinedPatternUnclosed() throws Exception {

        try {
            String key = this.defaultPredefinedRouteParamPatternsBinder.getAlphaAliasKey();

            getRouter().GET("/${param1:<" + key + "}").save(new IHandler<IDefaultRequestContext>() {

                @Override
                public void handle(IDefaultRequestContext context) {
                }
            });
            fail();
        } catch(Exception ex) {
            System.out.println("");
        }
    }

    @Test
    public void paramPredefinedPatternNotFound() throws Exception {

        try {
            getRouter().GET("/${param1:<NOPE>}").save(new IHandler<IDefaultRequestContext>() {

                @Override
                public void handle(IDefaultRequestContext context) {
                }
            });
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void noPatternInSplat() throws Exception {

        try {
            String key = this.defaultPredefinedRouteParamPatternsBinder.getAlphaAliasKey();

            getRouter().GET("/*{param1:<" + key + ">}").save(new IHandler<IDefaultRequestContext>() {

                @Override
                public void handle(IDefaultRequestContext context) {
                }
            });
            fail();
        } catch(Exception ex) {
        }
    }

    @Test
    public void predefinedPatternAlpha() throws Exception {

        String key = this.defaultPredefinedRouteParamPatternsBinder.getAlphaAliasKey();

        getRouter().GET("/${param1:<" + key + ">}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(context.request().getPathParam("param1"));
            }
        });

        SpincastTestHttpResponse response = get("/a");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a", response.getContent());

        response = get("/A");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("A", response.getContent());

        response = get("/aaaaa");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("aaaaa", response.getContent());

        response = get("/AAAAA");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("AAAAA", response.getContent());

        response = get("/b");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("b", response.getContent());

        response = get("/");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/-");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/1-2_3");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/1");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/a/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/aaaaa/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void predefinedPatternNumeric() throws Exception {

        String key = this.defaultPredefinedRouteParamPatternsBinder.geNumericAliasKey();

        getRouter().GET("/${param1:<" + key + ">}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(context.request().getPathParam("param1"));
            }
        });

        SpincastTestHttpResponse response = get("/a");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/A");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/aaaaa");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/AAAAA");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/-");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/1-2_3");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/1");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1", response.getContent());

        response = get("/123");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("123", response.getContent());

        response = get("/a/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/aaaaa/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void predefinedPatternAlphaPlus() throws Exception {

        String key = this.defaultPredefinedRouteParamPatternsBinder.getAlphaPlusAliasKey();

        getRouter().GET("/${param1:<" + key + ">}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(context.request().getPathParam("param1"));
            }
        });

        SpincastTestHttpResponse response = get("/a");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a", response.getContent());

        response = get("/A");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("A", response.getContent());

        response = get("/aaaaa");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("aaaaa", response.getContent());

        response = get("/AAAAA");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("AAAAA", response.getContent());

        response = get("/b");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("b", response.getContent());

        response = get("/");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/-");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("-", response.getContent());

        response = get("/a-b_c");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a-b_c", response.getContent());

        response = get("/a-2_c");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/1");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/a/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/aaaaa/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void predefinedPatternNumericPlus() throws Exception {

        String key = this.defaultPredefinedRouteParamPatternsBinder.geNumericPlusAliasKey();

        getRouter().GET("/${param1:<" + key + ">}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(context.request().getPathParam("param1"));
            }
        });

        SpincastTestHttpResponse response = get("/a");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/A");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/aaaaa");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/AAAAA");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/-");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("-", response.getContent());

        response = get("/1-2_3");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1-2_3", response.getContent());

        response = get("/1");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1", response.getContent());

        response = get("/123");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("123", response.getContent());

        response = get("/a/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/aaaaa/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void predefinedPatternAlphaNumeric() throws Exception {

        String key = this.defaultPredefinedRouteParamPatternsBinder.getAlphaNumericAliasKey();

        getRouter().GET("/${param1:<" + key + ">}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(context.request().getPathParam("param1"));
            }
        });

        SpincastTestHttpResponse response = get("/a");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a", response.getContent());

        response = get("/A");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("A", response.getContent());

        response = get("/aaaaa");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("aaaaa", response.getContent());

        response = get("/AAAAA");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("AAAAA", response.getContent());

        response = get("/b");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("b", response.getContent());

        response = get("/");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/1");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1", response.getContent());

        response = get("/1234567890");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1234567890", response.getContent());

        response = get("/-");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/a-b_c");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/a-2_c");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/a/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/aaaaa/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void predefinedPatternAlphaNumericPlus() throws Exception {

        String key = this.defaultPredefinedRouteParamPatternsBinder.getAlphaNumericPlusAliasKey();

        getRouter().GET("/${param1:<" + key + ">}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(context.request().getPathParam("param1"));
            }
        });

        SpincastTestHttpResponse response = get("/a");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a", response.getContent());

        response = get("/A");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("A", response.getContent());

        response = get("/aaaaa");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("aaaaa", response.getContent());

        response = get("/AAAAA");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("AAAAA", response.getContent());

        response = get("/b");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("b", response.getContent());

        response = get("/");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/1");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1", response.getContent());

        response = get("/1234567890");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1234567890", response.getContent());

        response = get("/-");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("-", response.getContent());

        response = get("/a-b_c");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a-b_c", response.getContent());

        response = get("/a-2_c");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a-2_c", response.getContent());

        response = get("/a/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/aaaaa/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void predefinedPatternCustom() throws Exception {

        getRouter().addRouteParamPatternAlias("XX", "abc");

        getRouter().GET("/${param1:<XX>}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(context.request().getPathParam("param1"));
            }
        });

        SpincastTestHttpResponse response = get("/a");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/A");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/aaaaa");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/AAAAA");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/1");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/1234567890");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/-");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/a-b_c");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/a-2_c");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/a/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/aaaaa/b");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/abc");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("abc", response.getContent());

        response = get("/ABC");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void predefinedPatternCustom2() throws Exception {

        getRouter().addRouteParamPatternAlias("USERS", "user|users|usr");

        getRouter().GET("/${param1:<USERS>}/${userId}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(context.request().getPathParam("param1"));
            }
        });

        SpincastTestHttpResponse response = get("/a");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/abc");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/user");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/users");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/usr");
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = get("/user/123");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("user", response.getContent());

        response = get("/users/123");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("users", response.getContent());

        response = get("/usr/123");
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("usr", response.getContent());

    }

}
