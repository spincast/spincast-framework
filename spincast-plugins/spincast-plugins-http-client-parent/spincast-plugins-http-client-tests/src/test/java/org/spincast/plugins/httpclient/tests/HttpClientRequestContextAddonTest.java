package org.spincast.plugins.httpclient.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.exchange.RequestContextBaseDeps;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.httpclient.tests.HttpClientRequestContextAddonTest.ICustomRequestContext;
import org.spincast.plugins.httpclient.websocket.HttpClient;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.SpincastNoAppIntegrationTestBase;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Module;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class HttpClientRequestContextAddonTest extends
                                               SpincastNoAppIntegrationTestBase<ICustomRequestContext, DefaultWebsocketContext> {

    public static interface ICustomRequestContext extends RequestContext<ICustomRequestContext> {

        public HttpClient http();
    }

    public static class CustomRequestContext extends RequestContextBase<ICustomRequestContext>
                                             implements ICustomRequestContext {

        private final HttpClient spincastHttpClientFactory;

        @AssistedInject
        public CustomRequestContext(@Assisted Object exchange,
                                    RequestContextBaseDeps<ICustomRequestContext> requestContextBaseDeps,
                                    HttpClient spincastHttpClientFactory) {
            super(exchange, requestContextBaseDeps);
            this.spincastHttpClientFactory = spincastHttpClientFactory;
        }

        @Override
        public HttpClient http() {
            return this.spincastHttpClientFactory;
        }
    }

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected Class<? extends RequestContext<?>> getRequestContextImplementationClass() {
                return CustomRequestContext.class;
            }
        };
    }

    @Test
    public void httpClientAddon() throws Exception {

        getRouter().GET("/two").save(new Handler<ICustomRequestContext>() {

            @Override
            public void handle(ICustomRequestContext context) {

                String value = context.request().getHeaderFirst("test-header");
                assertNotNull(value);
                assertEquals("test-value", value);

                context.response().addHeaderValue("test-header2", "test-value2");
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        final String url2 = createTestUrl("/two");

        getRouter().GET("/").save(new Handler<ICustomRequestContext>() {

            @Override
            public void handle(ICustomRequestContext context) {

                //==========================================
                // Uses the addon!
                //==========================================
                HttpResponse response = context.http().GET(url2)
                                                .addHeaderValue("test-header", "test-value")
                                                .send();

                assertEquals(HttpStatus.SC_OK, response.getStatus());
                assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
                String value = response.getHeaderFirst("test-header2");
                assertNotNull(value);
                assertEquals("test-value2", value);

                context.response().sendPlainText(response.getContentAsString());
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

}
