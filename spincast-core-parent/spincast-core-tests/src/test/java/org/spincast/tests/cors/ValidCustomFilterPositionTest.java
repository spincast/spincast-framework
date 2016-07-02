package org.spincast.tests.cors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.plugins.routing.ISpincastRouterConfig;
import org.spincast.plugins.routing.SpincastRouterConfigDefault;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.common.net.HttpHeaders;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class ValidCustomFilterPositionTest extends SpincastDefaultNoAppIntegrationTestBase {

    protected static class TestRoutingConfig extends SpincastRouterConfigDefault {

        @Override
        public int getCorsFilterPosition() {
            return -1;
        }
    }

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected void configure() {
                super.configure();
                bind(ISpincastRouterConfig.class).to(TestRoutingConfig.class).in(Scopes.SINGLETON);
            }
        };
    }

    @Test
    public void corsFilterDefault() throws Exception {

        // Cors filter
        getRouter().cors();

        getRouter().GET("/").save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {
                context.response().sendPlainText(SpincastTestUtils.TEST_STRING);
            }
        });

        IHttpResponse response = GET("/").addHeaderValue(HttpHeaders.ORIGIN, "https://example1.com")
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
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

}
