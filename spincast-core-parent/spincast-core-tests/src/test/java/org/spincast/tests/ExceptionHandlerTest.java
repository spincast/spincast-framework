package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.exceptions.CustomStatusCodeExceptionDefault;
import org.spincast.core.exceptions.PublicExceptionDefault;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.routing.RouteHandlerMatch;
import org.spincast.core.routing.RoutingResult;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;

public class ExceptionHandlerTest extends IntegrationTestNoAppDefaultContextsBase {

    @Inject
    protected SpincastDictionary spincastDictionary;

    @Test
    public void defaultExceptionHandler() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("this is a private message");
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(this.spincastDictionary.exception_default_message(), response.getContentAsString());
    }

    @Test
    public void accessExceptionFromRequestContext() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("Some exception message");
            }
        });

        getRouter().ALL("/*{path}").exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertTrue(context.routing().isExceptionRoute());
                assertFalse(context.routing().isNotFoundRoute());

                Boolean isException =
                        context.variables().get(SpincastConstants.RequestScopedVariables.IS_EXCEPTION_HANDLING, Boolean.class);
                assertNotNull(isException);
                assertTrue(isException);

                Boolean isNotFoundRoute =
                        context.variables().get(SpincastConstants.RequestScopedVariables.IS_NOT_FOUND_ROUTE, Boolean.class);
                assertTrue(isNotFoundRoute == null || !isNotFoundRoute);

                Object exceptionObj = context.variables().get(SpincastConstants.RequestScopedVariables.EXCEPTION);
                assertNotNull(exceptionObj);
                assertTrue(exceptionObj instanceof Exception);
                Exception exception = (Exception)exceptionObj;

                context.response().sendHtml("<b>Managed : " + exception.getMessage() + "</b>");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<b>Managed : Some exception message</b>", response.getContentAsString());
    }

    @Test
    public void publicException() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new PublicExceptionDefault(SpincastTestUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void publicExceptionWithCustomStatusCode() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new PublicExceptionDefault(SpincastTestUtils.TEST_STRING, HttpStatus.SC_INSUFFICIENT_SPACE_ON_RESOURCE);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INSUFFICIENT_SPACE_ON_RESOURCE, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void nonPublicExceptionWithCustomStatusCode() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new CustomStatusCodeExceptionDefault(SpincastTestUtils.TEST_STRING,
                                                           HttpStatus.SC_INSUFFICIENT_SPACE_ON_RESOURCE);
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INSUFFICIENT_SPACE_ON_RESOURCE, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());

        // Not equals
        assertFalse(response.getContentAsString().contains(SpincastTestUtils.TEST_STRING));
    }

    @Test
    public void customExceptionHandler() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("this is a private message");
            }
        });

        getRouter().ALL("/*{path}").exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Exception exception = context.variables().get(SpincastConstants.RequestScopedVariables.EXCEPTION,
                                                              Exception.class);
                assertNotNull(exception);
                assertEquals("this is a private message", exception.getMessage());

                context.response().setStatusCode(HttpStatus.SC_CONFLICT);
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_CONFLICT, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void customExceptionHandlerDefaultSyntax() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("this is a private message");
            }
        });

        getRouter().exception(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                Exception exception = context.variables().get(SpincastConstants.RequestScopedVariables.EXCEPTION,
                                                              Exception.class);
                assertNotNull(exception);
                assertEquals("this is a private message", exception.getMessage());

                context.response().setStatusCode(HttpStatus.SC_CONFLICT);
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_CONFLICT, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void customExceptionHandlerSpecificBeforeFilter() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("this is a private message");
            }
        });

        getRouter().ALL("/*{path}").exception()
                   .before(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("before");
                       }
                   }).save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
                       }
                   });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("before" + SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void customExceptionHandlerSpecificAfterFilter() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("this is a private message");
            }
        });

        getRouter().ALL("/*{path}").exception()
                   .after(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("after");
                       }
                   }).save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().setStatusCode(HttpStatus.SC_CONFLICT);
                           context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
                       }
                   });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_CONFLICT, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING + "after", response.getContentAsString());
    }

    @Test
    public void customExceptionHandlerAllFilterTypes() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("this is a private message");
            }
        });

        getRouter().ALL("/*{path}").exception()
                   .before(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("before");
                       }
                   }).after(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().sendPlainText("after");
                       }
                   }).save(new Handler<DefaultRequestContext>() {

                       @Override
                       public void handle(DefaultRequestContext context) {
                           context.response().setStatusCode(HttpStatus.SC_CONFLICT);
                           context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
                       }
                   });

        getRouter().before("/*{before}").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("111");
            }
        });

        getRouter().after("/*{after}").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText("222");
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_CONFLICT, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("111before" + SpincastTestUtils.TEST_STRING + "after222", response.getContentAsString());
    }

    @Test
    public void exceptionInCustomExceptionHandler() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("original message");
            }
        });

        getRouter().ALL("/*{path}").exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("custom handler exception");
            }
        });

        HttpResponse response = GET("/one").send();

        // Should have fallback to the default expcetion handler...
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(this.spincastDictionary.exception_default_message(), response.getContentAsString());
    }

    @Test
    public void publicExceptionExceptionInCustomExceptionHandler() throws Exception {

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new PublicExceptionDefault("original message");
            }
        });

        getRouter().ALL("/*{path}").exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("custom handler exception");
            }
        });

        HttpResponse response = GET("/one").send();

        // The original message should have been kept!
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("original message", response.getContentAsString());
    }

    @Test
    public void customExceptionHandlerMustHaveAccessToTheOriginalRouteInfos() throws Exception {

        getRouter().GET("/${first}/${second}").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("original message");
            }
        });

        getRouter().ALL("/*{path}").exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                @SuppressWarnings("unchecked")
                RoutingResult<DefaultRequestContext> originalRoutingResult =
                        (RoutingResult<DefaultRequestContext>)context.variables()
                                                                     .get(SpincastConstants.RequestScopedVariables.ORIGINAL_ROUTING_RESULT);

                assertNotNull(originalRoutingResult);
                RouteHandlerMatch<DefaultRequestContext> originalRouteHandlerMatch =
                        originalRoutingResult.getMainRouteHandlerMatch();
                assertNotNull(originalRouteHandlerMatch);
                assertNotNull(originalRouteHandlerMatch.getHandler());
                assertEquals("/${first}/${second}", originalRouteHandlerMatch.getSourceRoute().getPath());

                //==========================================
                // Access the path parameters from the original 
                // route
                //==========================================
                originalRouteHandlerMatch.getPathParams();
                assertEquals(2, originalRouteHandlerMatch.getPathParams().size());
                assertEquals("aaa", originalRouteHandlerMatch.getPathParams().get("first"));
                assertEquals("bbb", originalRouteHandlerMatch.getPathParams().get("second"));

                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/aaa/bbb").send();

        // Should have fallback to the default expcetion handler...
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void exceptionFromNotFoundRoute() throws Exception {

        getRouter().ALL("/*{path}").notFound().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("Some exception message");
            }
        });

        getRouter().ALL("/*{path}").exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                assertTrue(context.routing().isExceptionRoute());
                assertFalse(context.routing().isNotFoundRoute());

                Boolean isException =
                        context.variables().get(SpincastConstants.RequestScopedVariables.IS_EXCEPTION_HANDLING, Boolean.class);
                assertNotNull(isException);
                assertTrue(isException);

                Object exceptionObj = context.variables().get(SpincastConstants.RequestScopedVariables.EXCEPTION);
                assertNotNull(exceptionObj);
                assertTrue(exceptionObj instanceof Exception);
                Exception exception = (Exception)exceptionObj;

                context.response().sendHtml("<b>Managed : " + exception.getMessage() + "</b>");
            }
        });

        HttpResponse response = GET("/one").send();
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("<b>Managed : Some exception message</b>", response.getContentAsString());
    }

    @Test
    public void exceptionRouteFirstAddedOnly() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("test");
            }
        });

        getRouter().ALL("/*{path}").exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().setStatusCode(HttpStatus.SC_FORBIDDEN);
                context.response().sendHtml("ex_0");
            }
        });
        getRouter().ALL("/*{path}").exception().save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().setStatusCode(HttpStatus.SC_BAD_GATEWAY);
                context.response().sendHtml("ex_1");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ex_0", response.getContentAsString());
    }

    @Test
    public void exceptionShortcut() throws Exception {

        getRouter().GET("/").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                throw new RuntimeException("test");
            }
        });

        getRouter().exception(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().setStatusCode(HttpStatus.SC_FORBIDDEN);
                context.response().sendHtml("ex_0");
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("ex_0", response.getContentAsString());
    }

}
