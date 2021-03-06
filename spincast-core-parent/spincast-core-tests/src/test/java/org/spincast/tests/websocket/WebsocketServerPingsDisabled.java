package org.spincast.tests.websocket;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.httpclient.websocket.WebsocketClientWriter;
import org.spincast.plugins.undertow.config.SpincastUndertowConfig;
import org.spincast.plugins.undertow.config.SpincastUndertowConfigDefault;
import org.spincast.testing.defaults.NoAppWebsocketTestingBase;
import org.spincast.tests.varia.DefaultWebsocketControllerTest;
import org.spincast.tests.varia.WebsocketClientTest;

import com.google.inject.Module;

public class WebsocketServerPingsDisabled extends NoAppWebsocketTestingBase {

    @Override
    protected Module getExtraOverridingModule3() {
        return new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(SpincastUndertowConfig.class).toInstance(new SpincastUndertowConfigDefault() {

                    //==========================================
                    // Disable server pings
                    //==========================================
                    @Override
                    public boolean isWebsocketAutomaticPing() {
                        return false;
                    }
                });
            }
        };
    }

    @Test
    public void serverShouldNotDetectClosedPeerSincePingsAreDisabled() throws Exception {

        final DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer());
        getRouter().websocket("/ws").handle(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        WebsocketClientWriter writer = websocket("/ws").disableSslCertificateErrors().ping(0).connect(client);
        assertNotNull(writer);
        assertTrue(controller.isEndpointOpen("endpoint1"));

        writer.closeConnection();

        assertFalse(controller.waitForEndpointClosed("endpoint1", 3000));
    }

}
