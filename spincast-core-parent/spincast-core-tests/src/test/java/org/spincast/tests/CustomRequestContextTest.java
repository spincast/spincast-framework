package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.IntegrationTestNoAppBase;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.tests.CustomRequestContextTest.CustomRequestContext;

import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class CustomRequestContextTest extends
                                      IntegrationTestNoAppBase<CustomRequestContext, DefaultWebsocketContext> {

    @Override
    protected Injector createInjector() {
        return Spincast.configure()
                       .requestContextImplementationClass(CustomRequestContextDefault.class)
                       .init();
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
            response().sendPlainText(SpincastTestUtils.TEST_STRING);
        }
    }

    @Test
    public void customRequestContext() throws Exception {

        getRouter().GET("/").save(new Handler<CustomRequestContext>() {

            @Override
            public void handle(CustomRequestContext context) {
                context.customMethod();
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

}
