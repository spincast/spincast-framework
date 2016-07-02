package org.spincast.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.SpincastNoAppIntegrationTestBase;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.tests.CustomRequestContextTest.ICustomRequestContext;

import com.google.inject.Module;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class CustomRequestContextTest extends
                                      SpincastNoAppIntegrationTestBase<ICustomRequestContext, IDefaultWebsocketContext> {

    public static interface ICustomRequestContext extends IRequestContext<ICustomRequestContext> {

        public void customMethod();
    }

    public static class CustomRequestContext extends RequestContextBase<ICustomRequestContext>
                                             implements ICustomRequestContext {

        @AssistedInject
        public CustomRequestContext(@Assisted Object exchange,
                                    RequestContextBaseDeps<ICustomRequestContext> requestContextBaseDeps) {
            super(exchange, requestContextBaseDeps);
        }

        @Override
        public void customMethod() {
            response().sendPlainText(SpincastTestUtils.TEST_STRING);
        }
    }

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected Class<? extends IRequestContext<?>> getRequestContextImplementationClass() {
                return CustomRequestContext.class;
            }
        };
    }

    @Test
    public void customRequestContext() throws Exception {

        getRouter().GET("/").save(new IHandler<ICustomRequestContext>() {

            @Override
            public void handle(ICustomRequestContext context) {
                context.customMethod();
            }
        });

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

}
