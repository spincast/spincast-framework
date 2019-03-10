package org.spincast.tests.cors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.routing.SpincastRouterConfig;
import org.spincast.plugins.routing.SpincastRouterConfigDefault;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;

import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class ValidCustomFilterPositionTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected Module getExtraOverridingModule() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastRouterConfig.class).to(TestRoutingConfig.class).in(Scopes.SINGLETON);
            }
        };
    }

    protected static class TestRoutingConfig extends SpincastRouterConfigDefault {

        @Inject
        public TestRoutingConfig(SpincastConfig spincastConfig) {
            super(spincastConfig);
        }

        @Override
        public int getCorsFilterPosition() {
            return -1;
        }
    }

    @Test
    public void corsFilterDefault() throws Exception {

        // Cors filter
        getRouter().cors();

        getRouter().GET("/").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestingUtils.TEST_STRING);
            }
        });

        HttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
                                        .addHeaderValue(HttpHeaders.HOST, "example2.com")
                                        .send();

        String allowOriginHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertNotNull(allowOriginHeader);
        assertEquals("https://example1.com", allowOriginHeader);

        String allowCredentialsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertNotNull(allowCredentialsHeader);
        assertEquals("true", allowCredentialsHeader);

        String exposeHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
        assertNotNull(exposeHeadersHeader);
        assertEquals("", exposeHeadersHeader);

        String allowHeadersHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNull(allowHeadersHeader);

        String allowMethodsHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        assertNull(allowMethodsHeader);

        String maxAgeHeader = response.getHeaderFirst(HttpHeaders.ACCESS_CONTROL_MAX_AGE);
        assertNull(maxAgeHeader);

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

}
