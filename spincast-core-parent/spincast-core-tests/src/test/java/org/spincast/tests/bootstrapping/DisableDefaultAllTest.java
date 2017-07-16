package org.spincast.tests.bootstrapping;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.guice.SpincastCorePlugin;
import org.spincast.core.server.Server;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.plugins.config.SpincastConfigPlugin;
import org.spincast.plugins.dictionary.SpincastDictionaryPlugin;
import org.spincast.plugins.httpcaching.SpincastHttpCachingPlugin;
import org.spincast.plugins.jacksonjson.SpincastJacksonJsonPlugin;
import org.spincast.plugins.jacksonxml.SpincastJacksonXmlPlugin;
import org.spincast.plugins.localeresolver.SpincastLocaleResolverPlugin;
import org.spincast.plugins.pebble.SpincastPebblePlugin;
import org.spincast.plugins.request.SpincastRequestPlugin;
import org.spincast.plugins.response.SpincastResponsePlugin;
import org.spincast.plugins.routing.SpincastRoutingPlugin;
import org.spincast.plugins.templatingaddon.SpincastTemplatingAddonPlugin;
import org.spincast.plugins.undertow.SpincastUndertowPlugin;
import org.spincast.plugins.variables.SpincastVariablesPlugin;

import com.google.inject.Inject;

public class DisableDefaultAllTest {

    private static boolean initCalled = false;

    @Test
    public void test() throws Exception {
        DisableDefaultAllTest.main(null);
    }

    public static void main(String[] args) {

        try {
            Spincast.configure()
                    .disableAllDefaultPlugins()
                    .init(args);
            fail();
        } catch (Exception ex) {
        }
        assertFalse(initCalled);

        //==========================================
        // Manually add all the default plugins
        //==========================================
        Spincast.configure()
                .disableAllDefaultPlugins()
                .plugin(new SpincastCorePlugin())
                .plugin(new SpincastRoutingPlugin())
                .plugin(new SpincastJacksonJsonPlugin())
                .plugin(new SpincastJacksonXmlPlugin())
                .plugin(new SpincastRequestPlugin())
                .plugin(new SpincastResponsePlugin())
                .plugin(new SpincastPebblePlugin())
                .plugin(new SpincastTemplatingAddonPlugin())
                .plugin(new SpincastVariablesPlugin())
                .plugin(new SpincastLocaleResolverPlugin())
                .plugin(new SpincastHttpCachingPlugin())
                .plugin(new SpincastConfigPlugin())
                .plugin(new SpincastDictionaryPlugin())
                .plugin(new SpincastUndertowPlugin())
                .init(args);
        assertTrue(initCalled);
    }

    @Inject
    protected void init(Server server) {
        initCalled = true;
    }
}
