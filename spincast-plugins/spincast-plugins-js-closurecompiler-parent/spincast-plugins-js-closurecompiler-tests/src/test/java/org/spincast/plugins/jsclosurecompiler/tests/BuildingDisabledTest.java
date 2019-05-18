package org.spincast.plugins.jsclosurecompiler.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.jsclosurecompiler.config.SpincastJsClosureCompilerConfig;
import org.spincast.plugins.jsclosurecompiler.config.SpincastJsClosureCompilerConfigDefault;
import org.spincast.plugins.jsclosurecompiler.tests.utils.JsClosureCompileTestBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class BuildingDisabledTest extends JsClosureCompileTestBase {

    public static class TestSpincastJsYuiCompressorConfig extends SpincastJsClosureCompilerConfigDefault {

        @Inject
        public TestSpincastJsYuiCompressorConfig(SpincastConfig spincastConfig) {
            super(spincastConfig);
        }

        @Override
        public boolean isJsBundlesDisabled() {
            return true;
        }
    }

    @Override
    protected Module getExtraOverridingModule() {
        return Modules.override(super.getExtraOverridingModule()).with(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastJsClosureCompilerConfig.class).to(TestSpincastJsYuiCompressorConfig.class)
                                                           .in(Scopes.SINGLETON);
            }
        });
    }

    @Test
    public void test() throws Exception {

        getRouter().file("/a.js").classpath("/spincast/plugins/jsclosurecompiler/tests/a.js").handle();
        getRouter().file("/b.js").classpath("/spincast/plugins/jsclosurecompiler/tests/b.js").handle();

        String html = "{{ jsBundle('/a.js', '/b.js') }}";

        String output = getTemplatingEngine().evaluate(html);
        assertEquals("<script src=\"/a.js\"></script>\n<script src=\"/b.js\"></script>\n", output);
    }

}


