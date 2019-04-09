package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.core.websocket.DefaultWebsocketContextDefault;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.defaults.NoAppStartHttpServerCustomContextTypesTestingBase;
import org.spincast.tests.CustomRequestContextTest.CustomRequestContext;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * <strong>Note</strong> that, by default, all routes are cleared
 * before each test! Even routes added by plugins...
 * To change this behavior, override the
 * {@link NoAppStartHttpServerCustomContextTypesTestingBase#clearRoutes() clearRoutes()}
 * method.
 */
public class CustomRequestContextTest extends
                                      NoAppStartHttpServerCustomContextTypesTestingBase<CustomRequestContext, DefaultWebsocketContext> {


    @Override
    protected Class<? extends RequestContext<?>> getRequestContextImplementationClass() {
        return CustomRequestContextDefault.class;
    }

    @Override
    protected Class<? extends WebsocketContext<?>> getWebsocketContextImplementationClass() {
        return DefaultWebsocketContextDefault.class;
    }

    public static interface CustomRequestContext extends RequestContext<CustomRequestContext> {

        public void customMethod();
    }

    public static class CustomRequestContextDefault extends RequestContextBase<CustomRequestContext>
                                                    implements CustomRequestContext {

        @AssistedInject
        public CustomRequestContextDefault(@Assisted Object exchange,
                                           RequestContextBaseDeps<CustomRequestContext> requestContextBaseDeps) {
            super(exchange, requestContextBaseDeps);
        }

        @Override
        public void customMethod() {
            response().sendPlainText(SpincastTestingUtils.TEST_STRING);
        }
    }

    @Test
    public void customRequestContext() throws Exception {

        getRouter().GET("/").handle(new Handler<CustomRequestContext>() {

            @Override
            public void handle(CustomRequestContext context) {
                context.customMethod();
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

}
