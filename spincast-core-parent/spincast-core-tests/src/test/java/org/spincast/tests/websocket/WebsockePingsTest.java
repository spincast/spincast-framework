package org.spincast.tests.websocket;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.spincast.defaults.testing.WebsocketIntegrationTestNoAppDefaultContextsBase;
import org.spincast.plugins.httpclient.websocket.WebsocketClientWriter;
import org.spincast.plugins.undertow.ClosedEventSentCallback;
import org.spincast.plugins.undertow.SpincastUndertowWebsocketEndpointWriter;
import org.spincast.plugins.undertow.UndertowWebsocketEndpointWriter;
import org.spincast.plugins.undertow.UndertowWebsocketEndpointWriterFactory;
import org.spincast.plugins.undertow.config.SpincastUndertowConfig;
import org.spincast.plugins.undertow.config.SpincastUndertowConfigDefault;
import org.spincast.tests.varia.DefaultWebsocketControllerTest;
import org.spincast.tests.varia.WebsocketClientTest;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import io.undertow.websockets.core.WebSocketChannel;

public class WebsockePingsTest extends WebsocketIntegrationTestNoAppDefaultContextsBase {

    @Override
    protected Module getGuiceTweakerOverridingModule() {

        return new AbstractModule() {

            @Override
            protected void configure() {

                install(new FactoryModuleBuilder().implement(UndertowWebsocketEndpointWriter.class,
                                                             SpincastUndertowWebsocketEndpointWriterTest.class)
                                                  .build(UndertowWebsocketEndpointWriterFactory.class));

                bind(SpincastUndertowConfig.class).toInstance(new SpincastUndertowConfigDefault() {

                    //==========================================
                    // Server pings every seconds
                    //==========================================
                    @Override
                    public int getWebsocketAutomaticPingIntervalSeconds() {
                        return 1;
                    }
                });
            }
        };
    }

    public static class SpincastUndertowWebsocketEndpointWriterTest extends SpincastUndertowWebsocketEndpointWriter {

        @AssistedInject
        public SpincastUndertowWebsocketEndpointWriterTest(@Assisted Map<String, WebSocketChannel> channels,
                                                           SpincastUndertowConfig spincastUndertowConfig) {
            super(channels, spincastUndertowConfig);
        }

        @Override
        public void sendClosingConnection(int closingCode,
                                          String closingReason,
                                          Set<String> peerIds,
                                          ClosedEventSentCallback callback) {
            // We disable the "Endpoint closed" events normally 
            // sent to the peers!
            callback.done();
        }
    }

    @Test
    public void pings() throws Exception {

        final DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer());
        getRouter().websocket("/ws").save(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        //==========================================
        // Pings every second
        //==========================================
        WebsocketClientWriter writer = websocket("/ws").ping(1).connect(client);
        assertNotNull(writer);
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 1));

        controller.getEndpointManager("endpoint1").closeEndpoint();

        assertTrue(client.waitForConnectionClosed(3000));
        assertTrue(controller.waitNrbPeerConnectedMax("endpoint1", 0));
    }

    @Test
    public void noPings() throws Exception {

        final DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer());
        getRouter().websocket("/ws").save(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        //==========================================
        // No pings == no closed connection detection!
        //==========================================
        WebsocketClientWriter writer = websocket("/ws").ping(0).connect(client);
        assertNotNull(writer);
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 1));

        controller.getEndpointManager("endpoint1").closeEndpoint();

        assertFalse(client.waitForConnectionClosed(3000));
    }

    @Test
    public void serverShouldDetectClosedPeerViaPings() throws Exception {

        final DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer());
        getRouter().websocket("/ws").save(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        WebsocketClientWriter writer = websocket("/ws").ping(0).connect(client);
        assertNotNull(writer);
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 1));

        writer.closeConnection();

        assertTrue(controller.waitNrbPeerConnectedMax("endpoint1", 0));
    }

}
