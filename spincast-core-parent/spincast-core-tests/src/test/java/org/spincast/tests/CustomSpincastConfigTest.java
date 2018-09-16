package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.guice.TestingMode;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.NoAppStartHttpServerTestingBase;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.testing.core.utils.SpincastTestingUtils;

import com.google.inject.Inject;

public class CustomSpincastConfigTest extends NoAppStartHttpServerTestingBase {

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass2() {
        return CustomSpincastConfig.class;
    }

    public static class CustomSpincastConfig extends SpincastConfigTestingDefault {

        /**
         * Constructor
         */
        @Inject
        protected CustomSpincastConfig(SpincastConfigPluginConfig spincastConfigPluginConfig, @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public String getEnvironmentName() {
            return SpincastTestingUtils.TEST_STRING;
        }
    }

    @Test
    public void testNotFoundCustomMessage() throws Exception {

        final SpincastConfig spincastConfig = getInjector().getInstance(SpincastConfig.class);
        assertNotNull(spincastConfig);

        getRouter().GET("/one").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                context.response().sendPlainText(spincastConfig.getEnvironmentName());
            }
        });

        HttpResponse response = GET("/one").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals(ContentTypeDefaults.TEXT.getMainVariationWithUtf8Charset(), response.getContentType());
        assertEquals(SpincastTestingUtils.TEST_STRING, response.getContentAsString());
    }

}
