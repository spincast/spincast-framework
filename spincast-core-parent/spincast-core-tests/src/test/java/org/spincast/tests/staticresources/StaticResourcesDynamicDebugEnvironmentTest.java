package org.spincast.tests.staticresources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Test;
import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.tests.SpincastDefaultNoAppIntegrationTestBase;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.plugins.routing.ISpincastRouterConfig;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.SpincastTestConfig;

import com.google.inject.Inject;
import com.google.inject.Module;

public class StaticResourcesDynamicDebugEnvironmentTest extends SpincastDefaultNoAppIntegrationTestBase {

    @Inject
    protected ISpincastRouterConfig spincastRouterConfig;

    protected ISpincastRouterConfig getSpincastRouterConfig() {
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
            protected Class<? extends ISpincastConfig> getSpincastConfigClass() {
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

        getRouter().file("/generated.css").pathAbsolute(generatedCssFilePath).save(new IHandler<IDefaultRequestContext>() {

            @Override
            public void handle(IDefaultRequestContext context) {

                try {
                    nbrTimeCalled[0]++;
                    context.response().sendCharacters(content1, "text/css");
                } catch(Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }
        });

        IHttpResponse response = GET("/generated.css").send();

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
