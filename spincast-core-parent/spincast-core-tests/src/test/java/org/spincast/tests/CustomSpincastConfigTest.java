package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.SpincastTestConfig;
import org.spincast.testing.core.utils.SpincastTestUtils;

import com.google.inject.Module;

public class CustomSpincastConfigTest extends SpincastDefaultNoAppIntegrationTestBase {

    public static class CustomSpincastConfig extends SpincastTestConfig {

        @Override
        public String getEnvironmentName() {
            return SpincastTestUtils.TEST_STRING;
        }
    }

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule() {

            @Override
            protected Class<? extends SpincastConfig> getSpincastConfigClass() {
                return CustomSpincastConfig.class;
            }
        };
    }

    @Test
    public void testNotFoundCustomMessage() throws Exception {

        final SpincastConfig spincastConfig = getInjector().getInstance(SpincastConfig.class);
        assertNotNull(spincastConfig);

        getRouter().GET("/one").save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(spincastConfig.getEnvironmentName());
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestUtils.TEST_STRING, response.getContentAsString());
    }

}
