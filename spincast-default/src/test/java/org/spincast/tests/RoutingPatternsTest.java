package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.DefaultRouteParamAliasesBinder;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultIntegrationTestingBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;

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

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("one", response.getContentAsString());
    }

    @Test
    public void paramNoPatternNoName() throws Exception {

        getRouter().GET("/${}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertEquals(0, context.request().getPathParams().size());
            }
        });

        IHttpResponse response = GET("/one123").send();
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

        IHttpResponse response = GET("/a").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a", response.getContentAsString());

        response = GET("/aaaaa").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("aaaaa", response.getContentAsString());

        response = GET("/").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/b").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/a/b").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/aaaaa/b").send();
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

        IHttpResponse response = GET("/users/1").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1", response.getContentAsString());

        response = GET("/users/123").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("123", response.getContentAsString());

        response = GET("/users/a").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/users/a/b").send();
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

        IHttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("one", response.getContentAsString());

        response = GET("/b123").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("b123", response.getContentAsString());
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

        IHttpResponse response = GET("/a").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a", response.getContentAsString());

        response = GET("/A").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("A", response.getContentAsString());

        response = GET("/aaaaa").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("aaaaa", response.getContentAsString());

        response = GET("/AAAAA").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("AAAAA", response.getContentAsString());

        response = GET("/b").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("b", response.getContentAsString());

        response = GET("/").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/-").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/1-2_3").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/1").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/a/b").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/aaaaa/b").send();
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

        IHttpResponse response = GET("/a").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/A").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/aaaaa").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/AAAAA").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/b").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/-").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/1-2_3").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/1").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1", response.getContentAsString());

        response = GET("/123").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("123", response.getContentAsString());

        response = GET("/a/b").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/aaaaa/b").send();
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

        IHttpResponse response = GET("/a").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a", response.getContentAsString());

        response = GET("/A").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("A", response.getContentAsString());

        response = GET("/aaaaa").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("aaaaa", response.getContentAsString());

        response = GET("/AAAAA").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("AAAAA", response.getContentAsString());

        response = GET("/b").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("b", response.getContentAsString());

        response = GET("/").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/-").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("-", response.getContentAsString());

        response = GET("/a-b_c").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a-b_c", response.getContentAsString());

        response = GET("/a-2_c").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/1").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/a/b").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/aaaaa/b").send();
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

        IHttpResponse response = GET("/a").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/A").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/aaaaa").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/AAAAA").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/b").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/-").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("-", response.getContentAsString());

        response = GET("/1-2_3").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1-2_3", response.getContentAsString());

        response = GET("/1").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1", response.getContentAsString());

        response = GET("/123").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("123", response.getContentAsString());

        response = GET("/a/b").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/aaaaa/b").send();
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

        IHttpResponse response = GET("/a").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a", response.getContentAsString());

        response = GET("/A").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("A", response.getContentAsString());

        response = GET("/aaaaa").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("aaaaa", response.getContentAsString());

        response = GET("/AAAAA").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("AAAAA", response.getContentAsString());

        response = GET("/b").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("b", response.getContentAsString());

        response = GET("/").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/1").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1", response.getContentAsString());

        response = GET("/1234567890").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1234567890", response.getContentAsString());

        response = GET("/-").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/a-b_c").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/a-2_c").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/a/b").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/aaaaa/b").send();
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

        IHttpResponse response = GET("/a").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a", response.getContentAsString());

        response = GET("/A").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("A", response.getContentAsString());

        response = GET("/aaaaa").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("aaaaa", response.getContentAsString());

        response = GET("/AAAAA").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("AAAAA", response.getContentAsString());

        response = GET("/b").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("b", response.getContentAsString());

        response = GET("/").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/1").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1", response.getContentAsString());

        response = GET("/1234567890").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("1234567890", response.getContentAsString());

        response = GET("/-").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("-", response.getContentAsString());

        response = GET("/a-b_c").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a-b_c", response.getContentAsString());

        response = GET("/a-2_c").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("a-2_c", response.getContentAsString());

        response = GET("/a/b").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/aaaaa/b").send();
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

        IHttpResponse response = GET("/a").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/A").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/aaaaa").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/AAAAA").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/b").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/1").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/1234567890").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/-").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/a-b_c").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/a-2_c").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/a/b").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/aaaaa/b").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/abc").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("abc", response.getContentAsString());

        response = GET("/ABC").send();
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

        IHttpResponse response = GET("/a").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/abc").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/user").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/users").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/usr").send();
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

        response = GET("/user/123").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("user", response.getContentAsString());

        response = GET("/users/123").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("users", response.getContentAsString());

        response = GET("/usr/123").send();
        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("usr", response.getContentAsString());

    }

}
