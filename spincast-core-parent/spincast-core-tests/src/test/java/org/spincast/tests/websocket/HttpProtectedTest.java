package org.spincast.tests.websocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.plugins.httpclient.websocket.WebsocketClientWriter;
import org.spincast.tests.varia.WebsocketClientTest;
import org.spincast.tests.varia.DefaultWebsocketControllerTest;
import org.xnio.http.UpgradeFailedException;

public class HttpProtectedTest extends SpincastDefaultWebsocketNoAppIntegrationTestBase {

    @Override
    public void beforeClass() {
        super.beforeClass();

        getRouter().httpAuth("/", "testRealm");
        getServer().addHttpAuthentication("testRealm", "Stromgol", "Laroche");
    }

    @Test
    public void noCredentialsGiven() throws Exception {

        final DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public void onPeerMessage(DefaultWebsocketContext context, String message) {
                super.onPeerMessage(context, message);
                context.sendMessageToCurrentPeer("Pong " + message);
            }
        };
        getRouter().websocket("/ws").save(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        try {
            @SuppressWarnings("unused")
            WebsocketClientWriter writer = websocket("/ws").connect(client);
            fail();
        } catch(Exception ex) {

            Throwable cause = ex.getCause();
            assertNotNull(cause);
            assertTrue(cause instanceof UpgradeFailedException);
        }
    }

    @Test
    public void badCredentialsGiven() throws Exception {

        final DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public void onPeerMessage(DefaultWebsocketContext context, String message) {
                super.onPeerMessage(context, message);
                context.sendMessageToCurrentPeer("Pong " + message);
            }
        };
        getRouter().websocket("/ws").save(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        try {
            @SuppressWarnings("unused")
            WebsocketClientWriter writer = websocket("/ws").setHttpAuthCredentials("Stromgol", "nope")
                                                            .connect(client);
            fail();
        } catch(Exception ex) {

            Throwable cause = ex.getCause();
            assertNotNull(cause);
            assertTrue(cause instanceof UpgradeFailedException);
        }
    }

    @Test
    public void credentialsGiven() throws Exception {

        final DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public void onPeerMessage(DefaultWebsocketContext context, String message) {
                super.onPeerMessage(context, message);
                context.sendMessageToCurrentPeer("Pong " + message);
            }
        };
        getRouter().websocket("/ws").save(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        WebsocketClientWriter writer = websocket("/ws").setHttpAuthCredentials("Stromgol", "Laroche")
                                                        .connect(client);
        assertNotNull(writer);

        writer.sendMessage("test1");
        assertTrue(controller.waitForStringMessageReceived("endpoint1", 1));

        assertTrue(client.waitForStringMessageReceived(1));
        assertTrue(client.isConnectionOpen());
        assertEquals("Pong test1", client.getStringMessageReceived().get(0));
    }

}
