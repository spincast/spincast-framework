package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.ContentTypeDefaults;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.SpincastConfigTesting;
import org.spincast.testing.core.utils.SpincastConfigTestingDefault;
import org.spincast.testing.core.utils.SpincastTestUtils;

public class CustomSpincastConfigTest extends IntegrationTestNoAppDefaultContextsBase {

    @Override
    protected Class<? extends SpincastConfigTesting> getSpincastConfigTestingImplementation() {
        return CustomSpincastConfig.class;
    }

    public static class CustomSpincastConfig extends SpincastConfigTestingDefault implements SpincastConfigTesting {

        @Override
        public String getEnvironmentName() {
            return SpincastTestUtils.TEST_STRING;
        }
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
