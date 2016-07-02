package org.spincast.tests.websocket;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.plugins.httpclient.websocket.IWebsocketClientWriter;
import org.spincast.plugins.undertow.config.ISpincastUndertowConfig;
import org.spincast.plugins.undertow.config.SpincastUndertowConfigDefault;
import org.spincast.tests.varia.WebsocketClientTest;
import org.spincast.tests.varia.DefaultWebsocketControllerTest;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class WebsocketServerPingsDisabled extends SpincastDefaultWebsocketNoAppIntegrationTestBase {

    @Override
    protected Module getOverridingModule() {

        return new AbstractModule() {

            @Override
            protected void configure() {
                bind(ISpincastUndertowConfig.class).toInstance(new SpincastUndertowConfigDefault() {

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
        getRouter().websocket("/ws").save(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        IWebsocketClientWriter writer = websocket("/ws").ping(0).connect(client);
        assertNotNull(writer);
        assertTrue(controller.isEndpointOpen("endpoint1"));

        writer.closeConnection();

        assertFalse(controller.waitForEndpointClosed("endpoint1", 3000));
    }

}
