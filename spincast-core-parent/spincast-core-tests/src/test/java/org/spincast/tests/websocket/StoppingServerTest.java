package org.spincast.tests.websocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.spincast.plugins.httpclient.websocket.IWebsocketClientWriter;
import org.spincast.tests.varia.WebsocketClientTest;
import org.spincast.tests.varia.DefaultWebsocketControllerTest;

public class StoppingServerTest extends DefaultWebsocketTestBase {

    //==========================================
    // By default we disable the "closed" events sent
    // to the peers so the tests are faster. This re-enable
    // them.
    //==========================================
    @Override
    protected boolean isUseTestServer() {
        return false;
    }

    //==========================================
    // We will stop the server by ourself.
    //==========================================
    @Override
    protected void stopServer() {
        // nothing
    }

    @Test
    public void closedEventSentToPeersWhenStoppingTheServer() throws Exception {

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer(), false, true);
        getRouter().websocket("/ws").save(controller);

        WebsocketClientTest client1 = new WebsocketClientTest();
        IWebsocketClientWriter writer1 = websocket("/ws").ping(0).connect(client1);
        assertNotNull(writer1);

        WebsocketClientTest client2 = new WebsocketClientTest();
        IWebsocketClientWriter writer2 = websocket("/ws").ping(0).connect(client2);
        assertNotNull(writer2);

        assertTrue(controller.waitNrbPeerConnected("endpoint1", 2));

        getServer().stop();

        assertTrue(client1.waitForConnectionClosed());
        assertEquals(1, client1.getConnectionClosedEvents().size());

        assertTrue(client2.waitForConnectionClosed());
        assertEquals(1, client2.getConnectionClosedEvents().size());

    }

}
