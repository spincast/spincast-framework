package org.spincast.plugins.cssyuicompressor.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.cssyuicompressor.config.SpincastCssYuiCompressorConfig;
import org.spincast.plugins.cssyuicompressor.config.SpincastCssYuiCompressorConfigDefault;
import org.spincast.plugins.cssyuicompressor.tests.utils.CssYuiCompressorTestBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class BuildingDisabledTest extends CssYuiCompressorTestBase {

    public static class TestSpincastCssYuiCompressorConfig extends SpincastCssYuiCompressorConfigDefault {

        @Inject
        public TestSpincastCssYuiCompressorConfig(SpincastConfig spincastConfig) {
            super(spincastConfig);
        }

        @Override
        public boolean isCssBundlesDisabled() {
            return true;
        }
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastCssYuiCompressorConfig.class).to(TestSpincastCssYuiCompressorConfig.class)
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
        assertEquals("<link rel=\"stylesheet\" href=\"/a.css\">\n<link rel=\"stylesheet\" href=\"/b.css\">\n", output);
    }

}


