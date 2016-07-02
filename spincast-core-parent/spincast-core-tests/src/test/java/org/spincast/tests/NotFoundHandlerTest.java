package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.config.ISpincastDictionary;
import org.spincast.core.config.SpincastConstants;
import org.spincast.core.exceptions.NotFoundException;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Inject;

public class NotFoundHandlerTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Inject
    protected ISpincastDictionary spincastDictionary;

    @Test
    public void notFoundDefault() throws Exception {

        IHttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(this.spincastDictionary.route_notFound_default_message(), response.getContentAsString());
    }

    @Test
    public void notFoundCustom() throws Exception {

        getRouter().ALL("/*{path}").notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                assertFalse(context.routing().isExceptionRoute());
                assertTrue(context.routing().isNotFoundRoute());

                Boolean isNotFoundRoute = context.variables().get(SpincastConstants.RequestScopedVariables.IS_NOT_FOUND_ROUTE,
                                                                  Boolean.class);
                assertNotNull(isNotFoundRoute);
                assertTrue(isNotFoundRoute);

                Boolean isException = context.variables().get(SpincastConstants.RequestScopedVariables.IS_EXCEPTION_HANDLING,
                                                              Boolean.class);
                assertNull(isException);

                context.response().sendHtml("custom404");
            }
        });

        IHttpResponse response = GET("/two").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("custom404", response.getContentAsString());
    }

    @Test
    public void notFoundCustomWithFilters() throws Exception {

        getRouter().before(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendHtml("111");
            }
        });
        getRouter().ALL("/nope").pos(-1).allRoutingTypes().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendHtml("nope");
            }
        });
        getRouter().ALL("/one").pos(-1).allRoutingTypes().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendHtml("222");
            }
        });

        getRouter().ALL("/*{any}").pos(1).allRoutingTypes().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendHtml("222");
            }
        });

        getRouter().ALL("/*{path}").notFound()
                   .before(
                           new IHandler<IDefaultRequestContext>() {

                               @Override
                               public void handle(IDefaultRequestContext context) {
                                   context.response().sendHtml("before");
                               }
                           })
                   .after(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           context.response().sendHtml("after");
                       }
                   })
                   .save(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           context.response().sendHtml("custom404");
                       }
                   });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("111222beforecustom404after222", response.getContentAsString());
    }

    @Test
    public void notFoundCustomChangeHttpStatus() throws Exception {

        getRouter().ALL("/*{path}").notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().setStatusCode(HttpStatus.SC_FORBIDDEN);
                context.response().sendHtml("custom404");
            }
        });

        IHttpResponse response = GET("/two").send();

        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("custom404", response.getContentAsString());
    }

    @Test
    public void notFoundCustomFirst() throws Exception {

        getRouter().GET("/*{path}").notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendHtml("custom404_1");
            }
        });
        getRouter().GET("/*{path}").notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendHtml("custom404_2");
            }
        });

        IHttpResponse response = GET("/two").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("custom404_1", response.getContentAsString());
    }

    @Test
    public void notFoundException() throws Exception {

        getRouter().GET("/${param}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                // Let's say we look at the "param" path param and
                // find it is not valid...
                throw new NotFoundException(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response = GET("/nopeParam").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void notFoundExceptionWithBeforeFiltersReset() throws Exception {

        // Apply to the original route AND to the Not Found route
        getRouter().GET("/*{any}").pos(-1).found().notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        // Apply to the original route only
        getRouter().GET("/one").pos(-1).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        getRouter().GET("/${param}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                throw new NotFoundException(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("A" + SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

    @Test
    public void notFoundExceptionCustomHandler() throws Exception {

        getRouter().ALL("/*{path}").notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendHtml("custom404");
            }
        });

        getRouter().GET("/${param}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                context.response().sendPlainText("A");

                // Let's say we look at the "param" path param and
                // find it is not valid...
                throw new NotFoundException(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("custom404", response.getContentAsString());
    }

    @Test
    public void notFoundExceptionCustomHandlerNoReset() throws Exception {

        getRouter().ALL("/*{path}").notFound().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendHtml("custom404");
            }
        });

        getRouter().GET("/${param}").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                context.response().sendPlainText("A");

                // Let's say we look at the "param" path param and
                // find it is not valid...
                throw new NotFoundException(SpincastTestUtils.TEST_STRING, false);
            }
        });

        IHttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.HTML.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals("Acustom404", response.getContentAsString());
    }

    @Test
    public void notFoundExceptionWithFiltersReset() throws Exception {

        // "before" filter A
        getRouter().GET("/*{any}").pos(-1).allRoutingTypes().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        // "before" filter B
        getRouter().GET("/*{any}").pos(-1).allRoutingTypes().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        // main handler  with inline "before" filter "C" and inline
        // "after" filter "D"
        getRouter().GET("/${param}")
                   .before(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           context.response().sendPlainText("C");
                       }
                   })
                   .after(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           context.response().sendPlainText("D");
                       }
                   })

                   .save(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {

                           // true => reset!
                           throw new NotFoundException(SpincastTestUtils.TEST_STRING, true);
                       }
                   });

        // "after" filter E
        getRouter().after(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("E");
            }
        });

        // "after" filter F
        getRouter().GET("/*{any}").pos(1).allRoutingTypes().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("F");
            }
        });

        IHttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());

        assertEquals("AB" + SpincastTestUtils.TEST_STRING + "EF", response.getContentAsString());
    }

    @Test
    public void notFoundExceptionWithFiltersNoReset() throws Exception {

        // "before" filter A
        getRouter().before(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("A");
            }
        });

        // "before" filter B
        getRouter().before(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        // main handler  with inline "before" filter "C" and inline
        // "after" filter "D"
        getRouter().GET("/${param}")

                   .before(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           context.response().sendPlainText("C");
                       }
                   })
                   .after(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           context.response().sendPlainText("D");
                       }
                   })

                   .save(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           throw new NotFoundException(SpincastTestUtils.TEST_STRING, false);
                       }
                   });

        // "after" filter E
        getRouter().after(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("E");
            }
        });

        // "after" filter F
        getRouter().after(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("F");
            }
        });

        IHttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());

        //==========================================
        // The before filters and ran two times, since we
        // restart the routing process when a NotFoundException
        // is thrown.
        //
        // Also the inline "D" after filter is not run since it is
        // not part of the Not Found handler. But the "C" before filter
        // was already applied.
        //==========================================
        assertEquals("ABCAB" + SpincastTestUtils.TEST_STRING + "EF", response.getContentAsString());
    }

    @Test
    public void notFoundExceptionWithFiltersNoResetButSkipOneBefore() throws Exception {

        // "before" filter A
        getRouter().GET("/*{any}").pos(-1).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                // Not applied when the current route is a Not Found one!
                if(context.routing().isNotFoundRoute()) {
                    return;
                }

                context.response().sendPlainText("A");
            }
        });

        // "before" filter B
        getRouter().before(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("B");
            }
        });

        // main handler  with inline "before" filter "C" and inline
        // "after" filter "D"
        getRouter().GET("/${param}")

                   .before(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           context.response().sendPlainText("C");
                       }
                   })

                   .after(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           context.response().sendPlainText("D");
                       }
                   })

                   .save(new IHandler<IDefaultRequestContext>() {

                       @Override
                       public void handle(IDefaultRequestContext context) {
                           throw new NotFoundException(SpincastTestUtils.TEST_STRING, false);
                       }
                   });

        // "after" filter E
        getRouter().GET("/*{any}").pos(1).allRoutingTypes().save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("E");
            }
        });

        // "after" filter F
        getRouter().after(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText("F");
            }
        });

        IHttpResponse response = GET("/nope").send();

        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());

        //==========================================
        // The "A" before filter doesn't run when the current 
        // route is a Not Found one.
        //==========================================
        assertEquals("ABCB" + SpincastTestUtils.TEST_STRING + "EF", response.getContentAsString());
    }

}
