package org.spincast.tests.websocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.websocket.IWebsocketConnectionConfig;
import org.spincast.plugins.httpclient.websocket.IWebsocketClientWriter;
import org.spincast.shaded.org.apache.commons.lang3.tuple.Pair;
import org.spincast.tests.varia.WebsocketClientTest;
import org.spincast.tests.varia.DefaultWebsocketControllerTest;

public class MultipleEndpointsTest extends SpincastDefaultWebsocketNoAppIntegrationTestBase {

    @Test
    public void onlyOneControllerCanManageAGivenEndpoint() throws Exception {

        final String[] endpointIdToUse = new String[]{"endpoint1"};
        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public IWebsocketConnectionConfig onPeerPreConnect(IDefaultRequestContext context) {

                return new IWebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return endpointIdToUse[0];
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }
        };
        getRouter().websocket("/ws1").save(controller);

        DefaultWebsocketControllerTest controller2 = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public IWebsocketConnectionConfig onPeerPreConnect(IDefaultRequestContext context) {

                return new IWebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return endpointIdToUse[0];
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }
        };
        getRouter().websocket("/ws2").save(controller2);

        endpointIdToUse[0] = "endpoint1";
        peerIdToUse[0] = "peer1";
        WebsocketClientTest client = new WebsocketClientTest();
        IWebsocketClientWriter writer = websocket("/ws1").connect(client);
        assertNotNull(writer);

        assertTrue(controller.isEndpointOpen("endpoint1"));
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 1));

        //==========================================
        // Controller #2 can't use "enpoint1" since it's
        // already managed by controller #1.
        //==========================================
        endpointIdToUse[0] = "endpoint1";
        peerIdToUse[0] = "peer2";
        WebsocketClientTest client2 = new WebsocketClientTest();

        try {

            @SuppressWarnings("unused")
            IWebsocketClientWriter writer2 = websocket("/ws2").connect(client2);
            fail();
        } catch(Exception ex) {
        }

        //==========================================
        // Controller #1 can add more peers to the endpoint
        // it manages.
        //==========================================
        endpointIdToUse[0] = "endpoint1";
        peerIdToUse[0] = "peer2";
        WebsocketClientTest client1b = new WebsocketClientTest();
        IWebsocketClientWriter writer1b = websocket("/ws1").connect(client1b);
        assertNotNull(writer1b);

        assertTrue(controller.isEndpointOpen("endpoint1"));
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 2));
    }

    @Test
    public void controller2CanReuseEndpointIdWhenClosedController1() throws Exception {

        final String[] endpointIdToUse = new String[]{"endpoint1"};
        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public IWebsocketConnectionConfig onPeerPreConnect(IDefaultRequestContext context) {

                return new IWebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return endpointIdToUse[0];
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }
        };
        getRouter().websocket("/ws1").save(controller);

        DefaultWebsocketControllerTest controller2 = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public IWebsocketConnectionConfig onPeerPreConnect(IDefaultRequestContext context) {

                return new IWebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return endpointIdToUse[0];
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }
        };
        getRouter().websocket("/ws2").save(controller2);

        endpointIdToUse[0] = "endpoint1";
        peerIdToUse[0] = "peer1";
        WebsocketClientTest client = new WebsocketClientTest();
        IWebsocketClientWriter writer = websocket("/ws1").ping(0).connect(client);
        assertNotNull(writer);

        assertTrue(controller.isEndpointOpen("endpoint1"));
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 1));

        //==========================================
        // Controller #2 can't use "enpoint1" since it's
        // adlready managed by controller #1.
        //==========================================
        endpointIdToUse[0] = "endpoint1";
        peerIdToUse[0] = "peer2";
        WebsocketClientTest client2 = new WebsocketClientTest();

        try {

            @SuppressWarnings("unused")
            IWebsocketClientWriter writer2 = websocket("/ws2").ping(0).connect(client2);
            fail();
        } catch(Exception ex) {
        }

        controller.getEndpointManager("endpoint1").sendMessage("test123");
        assertTrue(client.waitForStringMessageReceived(1));
        assertEquals("test123", client.getStringMessageReceived().get(0));

        //==========================================
        // Controller #1 closed endpoint #1
        //==========================================
        controller.getEndpointManager("endpoint1").closeEndpoint(1000, "some reason");

        assertTrue(controller.waitForEndpointClosed("endpoint1"));

        //==========================================
        // Client #1 isn't connected anymore but as received the
        // closed event.
        //==========================================
        assertTrue(client.waitForConnectionClosed());

        assertNotNull(client.getConnectionClosedEvents());
        assertEquals(1, client.getConnectionClosedEvents().size());
        Pair<Integer, String> closedEvent = client.getConnectionClosedEvents().get(0);
        assertNotNull(closedEvent);
        assertEquals(new Integer(1000), closedEvent.getKey());
        assertEquals("some reason", closedEvent.getValue());

        //==========================================
        // Controller #2 can now use the "endpoint1" id
        // since it's not used anymore.
        //==========================================
        endpointIdToUse[0] = "endpoint1";
        peerIdToUse[0] = "peer1";
        client2 = new WebsocketClientTest();
        IWebsocketClientWriter writer2 = websocket("/ws2").connect(client2);
        assertNotNull(writer2);

        assertTrue(controller2.isEndpointOpen("endpoint1"));
        assertTrue(controller2.waitNrbPeerConnected("endpoint1", 1));

        assertFalse(controller.isEndpointOpen("endpoint1"));
        assertNull(controller.getEndpointManager("endpoint1"));
    }

    @Test
    public void multipleEndpointsSendMessages() throws Exception {

        final String[] endpointIdToUse = new String[]{"endpoint1"};
        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public IWebsocketConnectionConfig onPeerPreConnect(IDefaultRequestContext context) {

                return new IWebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return endpointIdToUse[0];
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }
        };
        getRouter().websocket("/ws").save(controller);

        assertFalse(controller.isEndpointOpen("endpoint1"));
        assertFalse(controller.isEndpointOpen("endpoint2"));
        assertFalse(controller.isEndpointOpen("endpoint3"));

        endpointIdToUse[0] = "endpoint1";
        peerIdToUse[0] = "peer1a";
        WebsocketClientTest client1a = new WebsocketClientTest();
        IWebsocketClientWriter writer1a = websocket("/ws").connect(client1a);
        assertNotNull(writer1a);
        assertTrue(controller.isEndpointOpen("endpoint1"));
        assertFalse(controller.isEndpointOpen("endpoint2"));
        assertFalse(controller.isEndpointOpen("endpoint3"));
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 1));

        endpointIdToUse[0] = "endpoint1";
        peerIdToUse[0] = "peer1b";
        WebsocketClientTest client1b = new WebsocketClientTest();
        IWebsocketClientWriter writer1b = websocket("/ws").connect(client1b);
        assertNotNull(writer1b);
        assertTrue(controller.isEndpointOpen("endpoint1"));
        assertFalse(controller.isEndpointOpen("endpoint2"));
        assertFalse(controller.isEndpointOpen("endpoint3"));
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 2));

        endpointIdToUse[0] = "endpoint2";
        peerIdToUse[0] = "peer2a";
        WebsocketClientTest client2a = new WebsocketClientTest();
        IWebsocketClientWriter writer2a = websocket("/ws").connect(client2a);
        assertNotNull(writer2a);
        assertTrue(controller.isEndpointOpen("endpoint1"));
        assertTrue(controller.isEndpointOpen("endpoint2"));
        assertFalse(controller.isEndpointOpen("endpoint3"));
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 2));
        assertTrue(controller.waitNrbPeerConnected("endpoint2", 1));

        endpointIdToUse[0] = "endpoint3";
        peerIdToUse[0] = "peer3a";
        WebsocketClientTest client3a = new WebsocketClientTest();
        IWebsocketClientWriter writer3a = websocket("/ws").connect(client3a);
        assertNotNull(writer3a);
        assertTrue(controller.isEndpointOpen("endpoint1"));
        assertTrue(controller.isEndpointOpen("endpoint2"));
        assertTrue(controller.isEndpointOpen("endpoint3"));
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 2));
        assertTrue(controller.waitNrbPeerConnected("endpoint2", 1));
        assertTrue(controller.waitNrbPeerConnected("endpoint3", 1));

        assertEquals(0, client1a.getStringMessageReceived().size());
        assertEquals(0, client1b.getStringMessageReceived().size());
        assertEquals(0, client2a.getStringMessageReceived().size());
        assertEquals(0, client3a.getStringMessageReceived().size());

        //==========================================
        // Controller sends a message to endpoint1
        //==========================================
        controller.getEndpointManager("endpoint1").sendMessage("test1");
        assertTrue(client1a.waitForStringMessageReceived(1));
        assertEquals("test1", client1a.getStringMessageReceived().get(0));
        assertTrue(client1b.waitForStringMessageReceived(1));
        assertEquals("test1", client1b.getStringMessageReceived().get(0));
        assertEquals(0, client2a.getStringMessageReceived().size());
        assertEquals(0, client3a.getStringMessageReceived().size());

        //==========================================
        // Controller sends a message to endpoint2
        //==========================================
        controller.getEndpointManager("endpoint2").sendMessage("test2");
        assertEquals(1, client1a.getStringMessageReceived().size());
        assertEquals("test1", client1a.getStringMessageReceived().get(0));
        assertEquals(1, client1b.getStringMessageReceived().size());
        assertEquals("test1", client1b.getStringMessageReceived().get(0));
        assertTrue(client2a.waitForStringMessageReceived(1));
        assertEquals("test2", client2a.getStringMessageReceived().get(0));
        assertEquals(0, client3a.getStringMessageReceived().size());

        //==========================================
        // Controller sends a message to endpoint1 again
        //==========================================
        controller.getEndpointManager("endpoint1").sendMessage("test3");
        assertTrue(client1a.waitForStringMessageReceived(2));
        assertEquals("test1", client1a.getStringMessageReceived().get(0));
        assertEquals("test3", client1a.getStringMessageReceived().get(1));
        assertTrue(client1b.waitForStringMessageReceived(2));
        assertEquals("test1", client1b.getStringMessageReceived().get(0));
        assertEquals("test3", client1b.getStringMessageReceived().get(1));
        assertEquals(1, client2a.getStringMessageReceived().size());
        assertEquals("test2", client2a.getStringMessageReceived().get(0));
        assertEquals(0, client3a.getStringMessageReceived().size());

        //==========================================
        // Controller sends a message to endpoint3
        //==========================================
        controller.getEndpointManager("endpoint3").sendMessage("test4");
        assertEquals(2, client1a.getStringMessageReceived().size());
        assertEquals("test1", client1a.getStringMessageReceived().get(0));
        assertEquals("test3", client1a.getStringMessageReceived().get(1));
        assertEquals(2, client1b.getStringMessageReceived().size());
        assertEquals("test1", client1b.getStringMessageReceived().get(0));
        assertEquals("test3", client1b.getStringMessageReceived().get(1));
        assertEquals(1, client2a.getStringMessageReceived().size());
        assertEquals("test2", client2a.getStringMessageReceived().get(0));
        assertTrue(client3a.waitForStringMessageReceived(1));
        assertEquals("test4", client3a.getStringMessageReceived().get(0));

        //==========================================
        // Controller sends a message to peer1b only
        //==========================================
        controller.getEndpointManager("endpoint1").sendMessage("peer1b", "test5");
        assertEquals(2, client1a.getStringMessageReceived().size());
        assertEquals("test1", client1a.getStringMessageReceived().get(0));
        assertEquals("test3", client1a.getStringMessageReceived().get(1));
        assertTrue(client1b.waitForStringMessageReceived(3));
        assertEquals("test1", client1b.getStringMessageReceived().get(0));
        assertEquals("test3", client1b.getStringMessageReceived().get(1));
        assertEquals("test5", client1b.getStringMessageReceived().get(2));
        assertEquals(1, client2a.getStringMessageReceived().size());
        assertEquals("test2", client2a.getStringMessageReceived().get(0));
        assertEquals(1, client3a.getStringMessageReceived().size());
        assertEquals("test4", client3a.getStringMessageReceived().get(0));
    }

    @Test
    public void multipleEndpointsReceiveMessages() throws Exception {

        final String[] endpointIdToUse = new String[]{"endpoint1"};
        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public IWebsocketConnectionConfig onPeerPreConnect(IDefaultRequestContext context) {

                return new IWebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return endpointIdToUse[0];
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }
        };
        getRouter().websocket("/ws").save(controller);

        assertFalse(controller.isEndpointOpen("endpoint1"));
        assertFalse(controller.isEndpointOpen("endpoint2"));
        assertFalse(controller.isEndpointOpen("endpoint3"));

        endpointIdToUse[0] = "endpoint1";
        peerIdToUse[0] = "peer1a";
        WebsocketClientTest client1a = new WebsocketClientTest();
        IWebsocketClientWriter writer1a = websocket("/ws").connect(client1a);
        assertNotNull(writer1a);

        endpointIdToUse[0] = "endpoint1";
        peerIdToUse[0] = "peer1b";
        WebsocketClientTest client1b = new WebsocketClientTest();
        IWebsocketClientWriter writer1b = websocket("/ws").connect(client1b);
        assertNotNull(writer1b);

        endpointIdToUse[0] = "endpoint2";
        peerIdToUse[0] = "peer2a";
        WebsocketClientTest client2a = new WebsocketClientTest();
        IWebsocketClientWriter writer2a = websocket("/ws").connect(client2a);
        assertNotNull(writer2a);

        endpointIdToUse[0] = "endpoint3";
        peerIdToUse[0] = "peer3a";
        WebsocketClientTest client3a = new WebsocketClientTest();
        IWebsocketClientWriter writer3a = websocket("/ws").connect(client3a);
        assertNotNull(writer3a);

        assertEquals(0, controller.getStringMessageReceived("endpoint1").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint1", "peer1a").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint1", "peer1b").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint2").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint2", "peer2a").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint3").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint3", "peer3a").size());

        //==========================================
        // peer1a sends a message
        //==========================================
        writer1a.sendMessage("test1");

        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer1a", 1));
        assertEquals("test1", controller.getStringMessageReceived("endpoint1", "peer1a").get(0));
        assertEquals(0, controller.getStringMessageReceived("endpoint1", "peer1b").size());
        assertEquals(1, controller.getStringMessageReceived("endpoint1").size());

        assertEquals(0, controller.getStringMessageReceived("endpoint2", "peer2a").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint2").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint3", "peer3a").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint3").size());

        //==========================================
        // peer1a sends another message
        //==========================================
        writer1a.sendMessage("test2");

        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer1a", 2));
        assertEquals("test1", controller.getStringMessageReceived("endpoint1", "peer1a").get(0));
        assertEquals("test2", controller.getStringMessageReceived("endpoint1", "peer1a").get(1));
        assertEquals(0, controller.getStringMessageReceived("endpoint1", "peer1b").size());
        assertEquals(2, controller.getStringMessageReceived("endpoint1").size());

        assertEquals(0, controller.getStringMessageReceived("endpoint2", "peer2a").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint2").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint3", "peer3a").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint3").size());

        //==========================================
        // peer1b sends a message
        //==========================================
        writer1b.sendMessage("test3");

        assertEquals(2, controller.getStringMessageReceived("endpoint1", "peer1a").size());
        assertEquals("test1", controller.getStringMessageReceived("endpoint1", "peer1a").get(0));
        assertEquals("test2", controller.getStringMessageReceived("endpoint1", "peer1a").get(1));

        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer1b", 1));
        assertEquals("test3", controller.getStringMessageReceived("endpoint1", "peer1b").get(0));
        assertEquals(3, controller.getStringMessageReceived("endpoint1").size());

        assertEquals(0, controller.getStringMessageReceived("endpoint2", "peer2a").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint2").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint3", "peer3a").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint3").size());

        //==========================================
        // peer3a sends a message
        //==========================================
        writer3a.sendMessage("test4");

        assertEquals(2, controller.getStringMessageReceived("endpoint1", "peer1a").size());
        assertEquals("test1", controller.getStringMessageReceived("endpoint1", "peer1a").get(0));
        assertEquals("test2", controller.getStringMessageReceived("endpoint1", "peer1a").get(1));

        assertEquals(1, controller.getStringMessageReceived("endpoint1", "peer1b").size());
        assertEquals("test3", controller.getStringMessageReceived("endpoint1", "peer1b").get(0));
        assertEquals(3, controller.getStringMessageReceived("endpoint1").size());

        assertEquals(0, controller.getStringMessageReceived("endpoint2", "peer2a").size());
        assertEquals(0, controller.getStringMessageReceived("endpoint2").size());

        assertTrue(controller.waitForStringMessageReceived("endpoint3", "peer3a", 1));
        assertEquals("test4", controller.getStringMessageReceived("endpoint3", "peer3a").get(0));
        assertEquals(1, controller.getStringMessageReceived("endpoint3").size());

    }

}
