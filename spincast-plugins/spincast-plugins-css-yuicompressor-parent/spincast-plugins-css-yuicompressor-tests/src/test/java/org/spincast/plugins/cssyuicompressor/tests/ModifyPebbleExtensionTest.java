package org.spincast.plugins.cssyuicompressor.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.routing.Router;
import org.spincast.core.server.Server;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.cssyuicompressor.SpincastCssYuiCompressorManager;
import org.spincast.plugins.cssyuicompressor.SpincastCssYuiCompressorPebbleExtension;
import org.spincast.plugins.cssyuicompressor.SpincastCssYuiCompressorPebbleExtensionDefault;
import org.spincast.plugins.cssyuicompressor.config.SpincastCssYuiCompressorConfig;
import org.spincast.plugins.cssyuicompressor.tests.utils.CssYuiCompressorTestBase;
import org.spincast.plugins.httpclient.HttpClient;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class ModifyPebbleExtensionTest extends CssYuiCompressorTestBase {


    public static class TestSpincastCssYuiCompressorPebbleExtension extends SpincastCssYuiCompressorPebbleExtensionDefault {

        @Inject
        public TestSpincastCssYuiCompressorPebbleExtension(SpincastCssYuiCompressorConfig spincastCssYuiCompressorConfig,
                                                           SpincastConfig spincastConfig,
                                                           SpincastUtils spincastUtils,
                                                           Router<?, ?> router,
                                                           Server server,
                                                           HttpClient httpClient,
                                                           SpincastCssYuiCompressorManager spincastCssYuiCompressorManager) {
            super(spincastCssYuiCompressorConfig,
                  spincastConfig,
                  spincastUtils,
                  router,
                  server,
                  httpClient,
                  spincastCssYuiCompressorManager);
        }

        @Override
        protected Object bundlingOutput(String path) {
            return "toto";
        }
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastCssYuiCompressorPebbleExtension.class).to(TestSpincastCssYuiCompressorPebbleExtension.class)
                                                                   .in(Scopes.SINGLETON);
            }
        });
    }

    @Test
    public void test() throws Exception {

        getRouter().file("/a.css").classpath("/spincast/plugins/cssyuicompressor/tests/a.css").handle();
        getRouter().file("/b.css").classpath("/spincast/plugins/cssyuicompressor/tests/b.css").handle();

        String html = "{{ cssBundle('/a.css', '/b.css') }}";

        String output = getTemplatingEngine().evaluate(html);
        assertNotNull(output);
        assertEquals("toto", output);
    }

}


