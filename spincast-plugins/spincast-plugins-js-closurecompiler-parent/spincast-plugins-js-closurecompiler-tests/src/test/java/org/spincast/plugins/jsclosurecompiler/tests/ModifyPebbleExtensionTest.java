package org.spincast.plugins.jsclosurecompiler.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.routing.Router;
import org.spincast.core.server.Server;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.httpclient.HttpClient;
import org.spincast.plugins.jsclosurecompiler.SpincastJsClosureCompilerManager;
import org.spincast.plugins.jsclosurecompiler.SpincastJsClosureCompilerPebbleExtension;
import org.spincast.plugins.jsclosurecompiler.SpincastJsClosureCompilerPebbleExtensionDefault;
import org.spincast.plugins.jsclosurecompiler.config.SpincastJsClosureCompilerConfig;
import org.spincast.plugins.jsclosurecompiler.tests.utils.JsClosureCompileTestBase;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

public class ModifyPebbleExtensionTest extends JsClosureCompileTestBase {


    public static class TestSpincastJsClosureCompilerPebbleExtension extends
                                                                     SpincastJsClosureCompilerPebbleExtensionDefault {


        @Inject
        public TestSpincastJsClosureCompilerPebbleExtension(SpincastJsClosureCompilerConfig spincastJsClosureCompilerConfig,
                                                            SpincastConfig spincastConfig,
                                                            SpincastUtils spincastUtils,
                                                            Router<?, ?> router,
                                                            Server server,
                                                            HttpClient httpClient,
                                                            SpincastJsClosureCompilerManager spincastJsClosureCompilerManager) {
            super(spincastJsClosureCompilerConfig,
                  spincastConfig,
                  spincastUtils,
                  router,
                  server,
                  httpClient,
                  spincastJsClosureCompilerManager);
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
                bind(SpincastJsClosureCompilerPebbleExtension.class).to(TestSpincastJsClosureCompilerPebbleExtension.class)
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
        assertNotNull(output);
        assertEquals("toto", output);
    }

}


