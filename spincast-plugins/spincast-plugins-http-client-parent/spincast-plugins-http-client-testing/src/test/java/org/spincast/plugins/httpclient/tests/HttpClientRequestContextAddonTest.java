package org.spincast.plugins.httpclient.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.exchange.RequestContextBase;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.DefaultTestingModule;
import org.spincast.plugins.httpclient.IHttpClient;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.plugins.httpclient.tests.HttpClientRequestContextAddonTest.ICustomRequestContext;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.SpincastGuiceModuleBasedIntegrationTestBase;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Module;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class HttpClientRequestContextAddonTest extends SpincastGuiceModuleBasedIntegrationTestBase<ICustomRequestContext> {

    public static interface ICustomRequestContext extends IRequestContext<ICustomRequestContext> {

        public IHttpClient http();
    }

    public static class CustomRequestContext extends RequestContextBase<ICustomRequestContext>
                                             implements ICustomRequestContext {

        private final IHttpClient spincastHttpClientFactory;

        @AssistedInject
        public CustomRequestContext(@Assisted Object exchange,
                                    IHttpClient spincastHttpClientFactory) {
            super(exchange);
            this.spincastHttpClientFactory = spincastHttpClientFactory;
        }

        @Override
        public IHttpClient http() {
            return this.spincastHttpClientFactory;
        }
    }

    @Override
    public Module getTestingModule() {
        return new DefaultTestingModule() {

            @Override
            protected Class<? extends IRequestContext<?>> getRequestContextImplementationClass() {
                return CustomRequestContext.class;
            }
        };
    }

    @Test
    public void httpClientAddon() throws Exception {

        getRouter().GET("/two").save(new IHandler<ICustomRequestContext>() {

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

        getRouter().GET("/").save(new IHandler<ICustomRequestContext>() {

            @Override
            public void handle(ICustomRequestContext context) {

                //==========================================
                // Uses the addon!
                //==========================================
                IHttpResponse response = context.http().GET(url2)
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

        IHttpResponse response = GET("/").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

}
