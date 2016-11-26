package org.spincast.tests.staticresources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.routing.SpincastRouterConfig;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.SpincastTestConfig;

import com.google.inject.Inject;
import com.google.inject.Module;

public class StaticResourcesDynamicDebugEnvironmentTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Inject
    protected SpincastRouterConfig spincastRouterConfig;

    protected SpincastRouterConfig getSpincastRouterConfig() {
        return this.spincastRouterConfig;
    }

    public static class TestConfig extends SpincastTestConfig {

        /**
         * Run in "debug" mode.
         */
        @Override
        public boolean isDebugEnabled() {
            return true;
        }

        /**
         * Disable writing of dynamic resources on disk
         */
        @Override
        public boolean isDisableWriteToDiskDynamicStaticResource() {
            return true;
        }
    }

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule(getMainArgsToUse()) {

            @Override
            protected Class<? extends SpincastConfig> getSpincastConfigClass() {
                return TestConfig.class;
            }
        };
    }

    @Test
    public void dynamicFileSavedByMainHandler() throws Exception {

        String generatedCssFilePath = createTestingFilePath("generated.css");
        final File generatedCssFile = new File(generatedCssFilePath);
        if(generatedCssFile.isFile()) {
            generatedCssFile.delete();
        }

        final int[] nbrTimeCalled = new int[]{0};
        final String content1 = "body{ font-size:12px;}";

        getRouter().file("/generated.css").pathAbsolute(generatedCssFilePath).save(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {

                try {
                    nbrTimeCalled[0]++;
                    context.response().sendCharacters(content1, "text/css");
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        HttpResponse response = GET("/generated.css").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content1, response.getContentAsString());

        assertEquals(1, nbrTimeCalled[0]);

        // Not written on disk!
        assertFalse(generatedCssFile.isFile());

        response = GET("/generated.css").send();

        assertEquals(HttpStatus.SC_OK, response.getStatus());
        assertEquals("text/css", response.getContentType());
        assertEquals(content1, response.getContentAsString());

        // Called again!
        assertEquals(2, nbrTimeCalled[0]);
        assertFalse(generatedCssFile.isFile());
    }

}
