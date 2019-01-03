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
import org.spincast.core.websocket.DefaultWebsocketContextDefault;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.defaults.testing.NoAppStartHttpServerCustomContextTypesTestingBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.httpclient.tests.HttpClientRequestContextAddonTest.CustomRequestContext;
import org.spincast.plugins.httpclient.websocket.HttpClient;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class HttpClientRequestContextAddonTest extends
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

        public HttpClient http();
    }

    public static class CustomRequestContextDefault extends RequestContextBase<CustomRequestContext>
                                                    implements CustomRequestContext {

        private final HttpClient spincastHttpClientFactory;

        @AssistedInject
        public CustomRequestContextDefault(@Assisted Object exchange,
                                           RequestContextBaseDeps<CustomRequestContext> requestContextBaseDeps,
                                           HttpClient spincastHttpClientFactory) {
            super(exchange, requestContextBaseDeps);
            this.spincastHttpClientFactory = spincastHttpClientFactory;
        }

        @Override
        public HttpClient http() {
            return this.spincastHttpClientFactory;
        }
    }

    @Test
    public void httpClientAddon() throws Exception {

        getRouter().GET("/two").handle(new Handler<CustomRequestContext>() {

            @Override
            public void handle(CustomRequestContext context) {

                String value = context.request().getHeaderFirst("test-header");
                assertNotNull(value);
                assertEquals("test-value", value);

                context.response().addHeaderValue("test-header2", "test-value2");
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        final String url2 = createTestUrl("/two");

        getRouter().GET("/").handle(new Handler<CustomRequestContext>() {

            @Override
            public void handle(CustomRequestContext context) {

                //==========================================
                // Uses the addon!
                //==========================================
                HttpResponse response = context.http().GET(url2)
                                               .disableSslCertificateErrors()
                                               .addHeaderValue("test-header", "test-value")
                                               .send();

                assertEquals(HttpStatus.SC_OK, response.getStatus());
                assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
                String value = response.getHeaderFirst("test-header2");
                assertNotNull(value);
                assertEquals("test-value2", value);

                context.response().sendPlainText(response.getContentAsString());
            }
        });

        HttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }


}
