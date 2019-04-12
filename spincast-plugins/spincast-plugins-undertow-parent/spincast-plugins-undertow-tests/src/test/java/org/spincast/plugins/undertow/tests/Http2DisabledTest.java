package org.spincast.plugins.undertow.tests;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.TestingMode;
import org.spincast.core.server.Server;
import org.spincast.plugins.config.SpincastConfigDefault;
import org.spincast.plugins.config.SpincastConfigPluginConfig;
import org.spincast.plugins.undertow.SpincastUndertowServer;
import org.spincast.testing.defaults.NoAppStartHttpServerTestingBase;
import org.xnio.OptionMap;

import com.google.inject.Inject;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;

public class Http2DisabledTest extends NoAppStartHttpServerTestingBase {

    public static class TestingSpincastConfig extends SpincastConfigDefault {

        @Inject
        public TestingSpincastConfig(SpincastConfigPluginConfig spincastConfigPluginConfig,
                                     @TestingMode boolean testingMode) {
            super(spincastConfigPluginConfig, testingMode);
        }

        @Override
        public boolean isEnableHttp2() {
            return false;
        }
    }

    @Override
    protected Class<? extends SpincastConfig> getTestingConfigImplementationClass2() {
        return TestingSpincastConfig.class;
    }

    @Test
    public void http2Disabled() throws Exception {

        Server server = getServer();
        assertTrue(server instanceof SpincastUndertowServer);
        SpincastUndertowServer spincastUndertowServer = (SpincastUndertowServer)server;

        Field undertowServerField = SpincastUndertowServer.class.getDeclaredField("undertowServer");
        undertowServerField.setAccessible(true);

        Undertow undertow = (Undertow)undertowServerField.get(spincastUndertowServer);
        Field serverOptionsField = Undertow.class.getDeclaredField("serverOptions");
        serverOptionsField.setAccessible(true);

        OptionMap optionMap = (OptionMap)serverOptionsField.get(undertow);
        Boolean http2Enabled = optionMap.get(UndertowOptions.ENABLE_HTTP2);
        assertNull(http2Enabled);
    }

}
