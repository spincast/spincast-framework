package org.spincast.tests.websocket;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.json.JsonObject;
import org.spincast.core.routing.Handler;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.core.websocket.WebsocketConnectionConfig;
import org.spincast.defaults.testing.NoAppWebsocketTestingBase;
import org.spincast.plugins.httpclient.websocket.WebsocketClientWriter;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.core.utils.TrueChecker;
import org.spincast.tests.varia.DefaultWebsocketControllerTest;
import org.spincast.tests.varia.WebsocketClientTest;

public class WebsocketDefaultTest extends NoAppWebsocketTestingBase {

    @Test
    public void handshakeWithHeadersAndCookies() throws Exception {

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                //==========================================
                // Validate cookies
                //==========================================
                String cookie = context.request().getCookieValue("username");
                assertNotNull(cookie);
                assertEquals("Stromgol", cookie);

                cookie = context.request().getCookieValue("cookie2");
                assertNotNull(cookie);
                assertEquals("val2", cookie);

                //==========================================
                // Validate custom headers
                //==========================================
                String customHeader = context.request().getHeaderFirst("customHeader");
                assertNotNull(customHeader);
                assertEquals("test1", customHeader);

                customHeader = context.request().getHeaderFirst("customHeader2");
                assertNotNull(customHeader);
                assertEquals("test2", customHeader);

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return "peer1";
                    }
                };
            }

            @Override
            public void onPeerMessage(DefaultWebsocketContext context, String message) {
                super.onPeerMessage(context, message);
                context.sendMessageToCurrentPeer("Pong " + message);
            }
        };
        getRouter().websocket("/ws").handle(controller);

        //==========================================
        // The endpoint is created when the controller
        // specifiy it for the first time.
        //==========================================
        assertFalse(controller.isEndpointOpen("endpoint1"));

        WebsocketClientTest client = new WebsocketClientTest();
        WebsocketClientWriter writer = websocket("/ws").disableSslCertificateErrors()
                                                       .setCookie("username", "Stromgol")
                                                       .setCookie("cookie2", "val2")
                                                       .addHeaderValue("customHeader", "test1")
                                                       .addHeaderValue("customHeader2", "test2")
                                                       .connect(client);
        assertNotNull(writer);

        //==========================================
        // Endpoint now open
        //==========================================
        assertTrue(controller.isEndpointOpen("endpoint1"));

        String message = UUID.randomUUID().toString();
        writer.sendMessage(message);

        assertTrue(controller.waitForStringMessageReceived("endpoint1", 1));
        assertTrue(controller.isEndpointOpen("endpoint1"));

        assertTrue(client.waitForStringMessageReceived(1));
        assertEquals("Pong " + message, client.getStringMessageReceived().get(0));

        String message2 = UUID.randomUUID().toString();
        writer.sendMessage(message2);

        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer1", 2));
        assertTrue(controller.isEndpointOpen("endpoint1"));

        assertTrue(client.waitForStringMessageReceived(2));
        assertEquals("Pong " + message2, client.getStringMessageReceived().get(1));
    }

    @Test
    public void closingTheEndpointShouldSendClosedEventToPeer() throws Exception {

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer());
        getRouter().websocket("/ws").handle(controller);

        WebsocketClientTest client = new WebsocketClientTest();
        WebsocketClientWriter writer = websocket("/ws").disableSslCertificateErrors().connect(client);
        assertNotNull(writer);
        assertTrue(controller.waitPeerConnected("endpoint1", "peer1"));

        controller.getEndpointManager("endpoint1").closeEndpoint();

        assertTrue(controller.waitForEndpointClosed("endpoint1"));
        assertTrue(client.waitForConnectionClosed());
    }

    @Test
    public void closingTheLastPeerDoesntCloseTheEndpoint() throws Exception {

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer());
        getRouter().websocket("/ws").handle(controller);

        assertFalse(controller.isEndpointOpen("endpoint1"));

        WebsocketClientTest client1 = new WebsocketClientTest();
        WebsocketClientWriter writer1 = websocket("/ws").disableSslCertificateErrors().connect(client1);
        assertNotNull(writer1);

        assertTrue(controller.isEndpointOpen("endpoint1"));

        assertTrue(controller.waitNrbPeerConnected("endpoint1", 1));
        assertTrue(controller.waitPeerConnected("endpoint1", "peer1"));

        // peer #1 closes its connection
        writer1.closeConnection();

        assertTrue(controller.waitNrbPeerConnectedMax("endpoint1", 0, 3000000));

        // The endpoint stays open!
        assertFalse(controller.waitForEndpointClosed("endpoint1"));
    }

    @Test
    public void usingWebsocketContextAddons() throws Exception {

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public void onPeerMessage(DefaultWebsocketContext context, String message) {

                assertEquals("endpoint1", context.getEndpointId());
                assertEquals("peer1", context.getPeerId());

                Dictionary dictionary = context.guice().getInstance(Dictionary.class);
                assertNotNull(dictionary);

                SpincastConfig config = context.get(SpincastConfig.class);
                assertNotNull(config);
                assertEquals(config.getDefaultLocale().toString(), context.getLocaleToUse().toString());

                JsonObject obj = context.json().create();
                assertNotNull(obj);

                obj = context.xml().fromXml("<test></test>");
                assertNotNull(obj);

                String placeholder = context.templating().createPlaceholder("name");
                assertNotNull(placeholder);

                String result =
                        context.templating().evaluate("Hi " + placeholder + "!", SpincastStatics.params("name", "Stromgol"));
                assertNotNull(result);
                assertEquals("Hi Stromgol!", result);

                // Only if there are no exceptions:
                super.onPeerMessage(context, message);

            }
        };
        getRouter().websocket("/ws").handle(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        WebsocketClientWriter writer = websocket("/ws").disableSslCertificateErrors().connect(client);
        assertNotNull(writer);
        assertTrue(controller.isEndpointOpen("endpoint1"));

        assertTrue(controller.waitNrbPeerConnected("endpoint1", 1));

        writer.sendMessage("test");
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer1", "test"));
    }

    @Test
    public void createThenDeleteRoute() throws Exception {

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer());
        getRouter().websocket("/ws").id("test").handle(controller);

        WebsocketClientTest client = new WebsocketClientTest();
        WebsocketClientWriter writer = websocket("/ws").disableSslCertificateErrors().connect(client);
        assertNotNull(writer);
        assertTrue(controller.isEndpointOpen("endpoint1"));

        writer.sendMessage("message");
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer1", "message"));

        getRouter().removeRoute("test");

        //==========================================
        // Cannot connect anymore, the route has been removed!
        //==========================================
        try {
            @SuppressWarnings("unused")
            WebsocketClientWriter writer2 = websocket("/ws").disableSslCertificateErrors().connect(client);
            fail();
        } catch (Exception ex) {
        }

        //==========================================
        // The existing endpoint is still open though!
        //==========================================
        assertTrue(controller.isEndpointOpen("endpoint1"));
        writer.sendMessage("message2");
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer1", "message2"));

        //==========================================
        // Closes the endpoint!
        //==========================================
        controller.getEndpointManager("endpoint1").closeEndpoint();
        assertTrue(controller.waitForEndpointClosed("endpoint1"));

        assertTrue(client.waitForConnectionClosed());
    }

    @Test
    public void customEndpointClosingCode() throws Exception {

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer());
        getRouter().websocket("/ws").handle(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        WebsocketClientWriter writer = websocket("/ws").disableSslCertificateErrors().connect(client);
        assertNotNull(writer);
        assertTrue(controller.isEndpointOpen("endpoint1"));

        assertTrue("Peers number : " + controller.getEndpointManager("endpoint1").getPeersIds().size(),
                   controller.waitNrbPeerConnected("endpoint1", 1));

        controller.getEndpointManager("endpoint1").closeEndpoint(1000, "some reason");

        assertTrue(client.waitForConnectionClosed());

        assertEquals(1, client.getConnectionClosedEvents().size());
        assertEquals(new Integer(1000), client.getConnectionClosedEvents().get(0).getKey());
        assertEquals("some reason", client.getConnectionClosedEvents().get(0).getValue());
    }

    @Test
    public void invalidCustomEndpointClosingCode() throws Exception {

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer());
        getRouter().websocket("/ws").handle(controller);

        WebsocketClientTest client = new WebsocketClientTest();

        WebsocketClientWriter writer = websocket("/ws").disableSslCertificateErrors().connect(client);
        assertNotNull(writer);
        assertTrue(controller.isEndpointOpen("endpoint1"));

        try {
            controller.getEndpointManager("endpoint1").closeEndpoint(123, "some reason");
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void multiplePeersOpenCloseEvents() throws Exception {

        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }
        };
        getRouter().websocket("/ws").handle(controller);

        assertFalse(controller.isEndpointOpen("endpoint1"));

        peerIdToUse[0] = "peer1";
        WebsocketClientTest client1 = new WebsocketClientTest();
        WebsocketClientWriter writer1 = websocket("/ws").disableSslCertificateErrors().connect(client1);
        assertNotNull(writer1);
        assertTrue(controller.isEndpointOpen("endpoint1"));

        assertTrue(controller.waitPeerConnected("endpoint1", "peer1"));
        assertEquals(1, controller.getEndpointManager("endpoint1").getPeersIds().size());

        peerIdToUse[0] = "peer2";
        WebsocketClientTest client2 = new WebsocketClientTest();
        WebsocketClientWriter writer2 = websocket("/ws").disableSslCertificateErrors().connect(client2);
        assertNotNull(writer2);
        assertTrue(controller.isEndpointOpen("endpoint1"));

        assertTrue(controller.waitPeerConnected("endpoint1", "peer2"));
        assertEquals(2, controller.getEndpointManager("endpoint1").getPeersIds().size());

        peerIdToUse[0] = "peer3";
        WebsocketClientTest client3 = new WebsocketClientTest();
        WebsocketClientWriter writer3 = websocket("/ws").disableSslCertificateErrors().connect(client3);
        assertNotNull(writer3);
        assertTrue(controller.isEndpointOpen("endpoint1"));

        assertTrue(controller.waitPeerConnected("endpoint1", "peer3"));
        assertEquals(3, controller.getEndpointManager("endpoint1").getPeersIds().size());

        // peer #1 closes its connection
        writer1.closeConnection();
        assertTrue(controller.waitNrbPeerConnectedMax("endpoint1", 2));
        assertTrue(controller.isEndpointOpen("endpoint1"));

        // Controller closes peer #2
        controller.getEndpointManager("endpoint1").closePeer("peer2");
        assertTrue(controller.waitNrbPeerConnectedMax("endpoint1", 1));
        assertTrue(controller.isEndpointOpen("endpoint1"));

        // Last peer #3 closes its connection
        writer3.closeConnection();

        // No more peers connected
        assertTrue(controller.waitNrbPeerConnectedMax("endpoint1", 0));
    }

    @Test
    public void multiplePeersSendMessagesEvents() throws Exception {

        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }
        };
        getRouter().websocket("/ws").handle(controller);

        assertNull(controller.getEndpointManager("endpoint1"));

        peerIdToUse[0] = "peer1";
        WebsocketClientTest client1 = new WebsocketClientTest();
        WebsocketClientWriter writer1 = websocket("/ws").disableSslCertificateErrors().connect(client1);
        assertNotNull(writer1);

        peerIdToUse[0] = "peer2";
        WebsocketClientTest client2 = new WebsocketClientTest();
        WebsocketClientWriter writer2 = websocket("/ws").disableSslCertificateErrors().connect(client2);
        assertNotNull(writer2);

        peerIdToUse[0] = "peer3";
        WebsocketClientTest client3 = new WebsocketClientTest();
        WebsocketClientWriter writer3 = websocket("/ws").disableSslCertificateErrors().connect(client3);
        assertNotNull(writer3);

        assertTrue(controller.isEndpointOpen("endpoint1"));
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 3));

        // The endpoint sends a message
        controller.getEndpointManager("endpoint1").sendMessage(SpincastTestingUtils.TEST_STRING);

        // All peers receive it
        assertTrue(client1.waitForStringMessageReceived(1));
        assertEquals(SpincastTestingUtils.TEST_STRING, client1.getStringMessageReceived().get(0));

        assertTrue(client2.waitForStringMessageReceived(1));
        assertEquals(SpincastTestingUtils.TEST_STRING, client2.getStringMessageReceived().get(0));

        assertTrue(client3.waitForStringMessageReceived(1));
        assertEquals(SpincastTestingUtils.TEST_STRING, client3.getStringMessageReceived().get(0));

        // The endpoint sends a message to a specific peer only
        controller.getEndpointManager("endpoint1").sendMessage("peer2", "Hi peer2!");

        assertTrue(client1.waitForStringMessageReceived(1));

        assertTrue(client2.waitForStringMessageReceived(2));
        assertEquals("Hi peer2!", client2.getStringMessageReceived().get(1));

        assertTrue(client3.waitForStringMessageReceived(1));
    }

    @Test
    public void echoToAll() throws Exception {

        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }

            @Override
            public void onPeerMessage(DefaultWebsocketContext context, String message) {
                super.onPeerMessage(context, message);

                // Echo the message back!
                getEndpointManager("endpoint1").sendMessage(message);
            }

        };
        getRouter().websocket("/ws").handle(controller);

        assertNull(controller.getEndpointManager("endpoint1"));

        peerIdToUse[0] = "peer1";
        WebsocketClientTest client1 = new WebsocketClientTest();
        WebsocketClientWriter writer1 = websocket("/ws").disableSslCertificateErrors().connect(client1);
        assertNotNull(writer1);

        peerIdToUse[0] = "peer2";
        WebsocketClientTest client2 = new WebsocketClientTest();
        WebsocketClientWriter writer2 = websocket("/ws").disableSslCertificateErrors().connect(client2);
        assertNotNull(writer2);

        peerIdToUse[0] = "peer3";
        WebsocketClientTest client3 = new WebsocketClientTest();
        WebsocketClientWriter writer3 = websocket("/ws").disableSslCertificateErrors().connect(client3);
        assertNotNull(writer3);

        assertTrue(controller.isEndpointOpen("endpoint1"));
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 3));
        assertEquals(3, controller.getEndpointManager("endpoint1").getPeersIds().size());

        // Peer #2 sends a message
        writer2.sendMessage(SpincastTestingUtils.TEST_STRING);

        // The endpoint receives it
        assertTrue(controller.waitForStringMessageReceived("endpoint1", 1));
        assertEquals(SpincastTestingUtils.TEST_STRING, controller.getStringMessageReceived("endpoint1").get(0));

        // All peers receive it as an echo, even Peer #2 which is the 
        // original sender.
        assertTrue(client1.waitForStringMessageReceived(1));
        assertEquals(SpincastTestingUtils.TEST_STRING, client1.getStringMessageReceived().get(0));

        assertTrue(client2.waitForStringMessageReceived(1));
        assertEquals(SpincastTestingUtils.TEST_STRING, client2.getStringMessageReceived().get(0));

        assertTrue(client3.waitForStringMessageReceived(1));
        assertEquals(SpincastTestingUtils.TEST_STRING, client3.getStringMessageReceived().get(0));
    }

    @Test
    public void echoToAllExceptTheSender() throws Exception {

        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }

            @Override
            public void onPeerMessage(DefaultWebsocketContext context, String message) {
                super.onPeerMessage(context, message);

                // Echo the message back, except to the sender
                getEndpointManager("endpoint1").sendMessageExcept(context.getPeerId(), message);
            }

        };
        getRouter().websocket("/ws").handle(controller);

        assertNull(controller.getEndpointManager("endpoint1"));

        peerIdToUse[0] = "peer1";
        WebsocketClientTest client1 = new WebsocketClientTest();
        WebsocketClientWriter writer1 = websocket("/ws").disableSslCertificateErrors().connect(client1);
        assertNotNull(writer1);

        peerIdToUse[0] = "peer2";
        WebsocketClientTest client2 = new WebsocketClientTest();
        WebsocketClientWriter writer2 = websocket("/ws").disableSslCertificateErrors().connect(client2);
        assertNotNull(writer2);

        peerIdToUse[0] = "peer3";
        WebsocketClientTest client3 = new WebsocketClientTest();
        WebsocketClientWriter writer3 = websocket("/ws").disableSslCertificateErrors().connect(client3);
        assertNotNull(writer3);

        assertTrue(controller.isEndpointOpen("endpoint1"));

        assertTrue(controller.waitNrbPeerConnected("endpoint1", 3));
        assertEquals(3, controller.getEndpointManager("endpoint1").getPeersIds().size());

        // Peer #2 sends a message
        writer2.sendMessage(SpincastTestingUtils.TEST_STRING);

        // The endpoint receives it
        assertTrue(controller.waitForStringMessageReceived("endpoint1", 1));
        assertEquals(SpincastTestingUtils.TEST_STRING, controller.getStringMessageReceived("endpoint1").get(0));

        // All peers receive it as an echo, except peer #2 which is the
        // original sender.
        assertTrue(client1.waitForStringMessageReceived(1));
        assertEquals(SpincastTestingUtils.TEST_STRING, client1.getStringMessageReceived().get(0));

        assertTrue(client2.waitForStringMessageReceived(0));

        assertTrue(client3.waitForStringMessageReceived(1));
        assertEquals(SpincastTestingUtils.TEST_STRING, client3.getStringMessageReceived().get(0));
    }

    @Test
    public void echoToTheSenderOnly() throws Exception {

        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }

            @Override
            public void onPeerMessage(DefaultWebsocketContext context, String message) {
                super.onPeerMessage(context, message);

                // Echo the message back to the sender.
                context.sendMessageToCurrentPeer(message);
            }

        };
        getRouter().websocket("/ws").handle(controller);

        assertNull(controller.getEndpointManager("endpoint1"));

        peerIdToUse[0] = "peer1";
        WebsocketClientTest client1 = new WebsocketClientTest();
        WebsocketClientWriter writer1 = websocket("/ws").disableSslCertificateErrors().connect(client1);
        assertNotNull(writer1);

        peerIdToUse[0] = "peer2";
        WebsocketClientTest client2 = new WebsocketClientTest();
        WebsocketClientWriter writer2 = websocket("/ws").disableSslCertificateErrors().connect(client2);
        assertNotNull(writer2);

        peerIdToUse[0] = "peer3";
        WebsocketClientTest client3 = new WebsocketClientTest();
        WebsocketClientWriter writer3 = websocket("/ws").disableSslCertificateErrors().connect(client3);
        assertNotNull(writer3);

        assertTrue(controller.isEndpointOpen("endpoint1"));
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 3));
        assertEquals(3, controller.getEndpointManager("endpoint1").getPeersIds().size());

        // Peer #2 sends a message
        writer2.sendMessage(SpincastTestingUtils.TEST_STRING);

        // The endpoint receives it
        assertTrue(controller.waitForStringMessageReceived("endpoint1", 1));
        assertEquals(SpincastTestingUtils.TEST_STRING, controller.getStringMessageReceived("endpoint1").get(0));

        // Only peer #2 receives it as an echo

        assertTrue(client1.waitForStringMessageReceived(0));

        assertTrue(client2.waitForStringMessageReceived(1));
        assertEquals(SpincastTestingUtils.TEST_STRING, client2.getStringMessageReceived().get(0));

        assertTrue(client3.waitForStringMessageReceived(0));
    }

    @Test
    public void multiplePeersSendBytesMessagesEvents() throws Exception {

        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }
        };
        getRouter().websocket("/ws").handle(controller);

        assertNull(controller.getEndpointManager("endpoint1"));

        peerIdToUse[0] = "peer1";
        WebsocketClientTest client1 = new WebsocketClientTest();
        WebsocketClientWriter writer1 = websocket("/ws").disableSslCertificateErrors().connect(client1);
        assertNotNull(writer1);

        peerIdToUse[0] = "peer2";
        WebsocketClientTest client2 = new WebsocketClientTest();
        WebsocketClientWriter writer2 = websocket("/ws").disableSslCertificateErrors().connect(client2);
        assertNotNull(writer2);

        peerIdToUse[0] = "peer3";
        WebsocketClientTest client3 = new WebsocketClientTest();
        WebsocketClientWriter writer3 = websocket("/ws").disableSslCertificateErrors().connect(client3);
        assertNotNull(writer3);

        assertTrue(controller.isEndpointOpen("endpoint1"));
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 3));
        assertEquals(3, controller.getEndpointManager("endpoint1").getPeersIds().size());

        // The endpoint sends a message
        controller.getEndpointManager("endpoint1").sendMessage(SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"));

        // All peers receive it
        assertTrue(client1.waitForBytesMessageReceived(1));
        assertArrayEquals("Received bytes: " + client1.getBytesMessageReceived().get(0).toString(),
                          SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"),
                          client1.getBytesMessageReceived().get(0));

        assertTrue(client2.waitForBytesMessageReceived(1));
        assertArrayEquals("Received bytes: " + client1.getBytesMessageReceived().get(0).toString(),
                          SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"),
                          client2.getBytesMessageReceived().get(0));

        assertTrue(client3.waitForBytesMessageReceived(1));
        assertArrayEquals("Received bytes: " + client1.getBytesMessageReceived().get(0).toString(),
                          SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"),
                          client3.getBytesMessageReceived().get(0));

        // The endpoint sends a message to a specific peer only
        controller.getEndpointManager("endpoint1").sendMessage("peer2", "Hi peer2!".getBytes("UTF-8"));

        assertTrue(client1.waitForBytesMessageReceived(1));

        assertTrue(client2.waitForBytesMessageReceived(2));
        assertArrayEquals("Hi peer2!".getBytes("UTF-8"), client2.getBytesMessageReceived().get(1));

        assertTrue(client3.waitForBytesMessageReceived(1));
    }

    @Test
    public void echoBytesToAll() throws Exception {

        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }

            @Override
            public void onPeerMessage(DefaultWebsocketContext context, byte[] message) {
                super.onPeerMessage(context, message);

                // Echo the message back!
                getEndpointManager("endpoint1").sendMessage(message);
            }

        };
        getRouter().websocket("/ws").handle(controller);

        assertNull(controller.getEndpointManager("endpoint1"));

        peerIdToUse[0] = "peer1";
        WebsocketClientTest client1 = new WebsocketClientTest();
        WebsocketClientWriter writer1 = websocket("/ws").disableSslCertificateErrors().connect(client1);
        assertNotNull(writer1);

        peerIdToUse[0] = "peer2";
        WebsocketClientTest client2 = new WebsocketClientTest();
        WebsocketClientWriter writer2 = websocket("/ws").disableSslCertificateErrors().connect(client2);
        assertNotNull(writer2);

        peerIdToUse[0] = "peer3";
        WebsocketClientTest client3 = new WebsocketClientTest();
        WebsocketClientWriter writer3 = websocket("/ws").disableSslCertificateErrors().connect(client3);
        assertNotNull(writer3);

        assertTrue(controller.isEndpointOpen("endpoint1"));
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 3));
        assertEquals(3, controller.getEndpointManager("endpoint1").getPeersIds().size());

        // Peer #2 sends a message
        writer2.sendMessage(SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"));

        // The endpoint receives it
        assertTrue(controller.waitForBytesMessageReceived("endpoint1", 1));
        assertArrayEquals(SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"),
                          controller.getBytesMessageReceived("endpoint1").get(0));

        // All peers receive it as an echo, even Peer #2 which is the 
        // original sender.
        assertTrue(client1.waitForBytesMessageReceived(1));
        assertArrayEquals(SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"), client1.getBytesMessageReceived().get(0));

        assertTrue(client2.waitForBytesMessageReceived(1));
        assertArrayEquals(SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"), client2.getBytesMessageReceived().get(0));

        assertTrue(client3.waitForBytesMessageReceived(1));
        assertArrayEquals(SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"), client3.getBytesMessageReceived().get(0));
    }

    @Test
    public void echoBytesToAllExceptTheSender() throws Exception {

        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }

            @Override
            public void onPeerMessage(DefaultWebsocketContext context, byte[] message) {
                super.onPeerMessage(context, message);

                // Echo the message back, except to the sender
                getEndpointManager("endpoint1").sendMessageExcept(context.getPeerId(), message);
            }
        };
        getRouter().websocket("/ws").handle(controller);

        assertNull(controller.getEndpointManager("endpoint1"));

        peerIdToUse[0] = "peer1";
        WebsocketClientTest client1 = new WebsocketClientTest();
        WebsocketClientWriter writer1 = websocket("/ws").disableSslCertificateErrors().connect(client1);
        assertNotNull(writer1);

        peerIdToUse[0] = "peer2";
        WebsocketClientTest client2 = new WebsocketClientTest();
        WebsocketClientWriter writer2 = websocket("/ws").disableSslCertificateErrors().connect(client2);
        assertNotNull(writer2);

        peerIdToUse[0] = "peer3";
        WebsocketClientTest client3 = new WebsocketClientTest();
        WebsocketClientWriter writer3 = websocket("/ws").disableSslCertificateErrors().connect(client3);
        assertNotNull(writer3);

        assertTrue(controller.isEndpointOpen("endpoint1"));
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 3));
        assertEquals(3, controller.getEndpointManager("endpoint1").getPeersIds().size());

        // Peer #2 sends a message
        writer2.sendMessage(SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"));

        // The endpoint receives it
        assertTrue(controller.waitForBytesMessageReceived("endpoint1", 1));
        assertArrayEquals(SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"),
                          controller.getBytesMessageReceived("endpoint1").get(0));

        // All peers receive it as an echo, except peer #2 which is the
        // original sender.
        assertTrue(client1.waitForBytesMessageReceived(1));
        assertArrayEquals(SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"), client1.getBytesMessageReceived().get(0));

        assertTrue(client2.waitForBytesMessageReceived(0));

        assertTrue(client3.waitForBytesMessageReceived(1));
        assertArrayEquals(SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"), client3.getBytesMessageReceived().get(0));
    }

    @Test
    public void echoBytesToTheSenderOnly() throws Exception {

        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }

            @Override
            public void onPeerMessage(DefaultWebsocketContext context, byte[] message) {
                super.onPeerMessage(context, message);

                // Echo the message back to the sender.
                context.sendMessageToCurrentPeer(message);
            }

        };
        getRouter().websocket("/ws").handle(controller);

        assertNull(controller.getEndpointManager("endpoint1"));

        peerIdToUse[0] = "peer1";
        WebsocketClientTest client1 = new WebsocketClientTest();
        WebsocketClientWriter writer1 = websocket("/ws").disableSslCertificateErrors().connect(client1);
        assertNotNull(writer1);

        peerIdToUse[0] = "peer2";
        WebsocketClientTest client2 = new WebsocketClientTest();
        WebsocketClientWriter writer2 = websocket("/ws").disableSslCertificateErrors().connect(client2);
        assertNotNull(writer2);

        peerIdToUse[0] = "peer3";
        WebsocketClientTest client3 = new WebsocketClientTest();
        WebsocketClientWriter writer3 = websocket("/ws").disableSslCertificateErrors().connect(client3);
        assertNotNull(writer3);

        assertTrue(controller.isEndpointOpen("endpoint1"));
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 3));
        assertEquals(3, controller.getEndpointManager("endpoint1").getPeersIds().size());

        // Peer #2 sends a message
        writer2.sendMessage(SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"));

        // The endpoint receives it
        assertTrue(controller.waitForBytesMessageReceived("endpoint1", 1));
        assertArrayEquals(SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"),
                          controller.getBytesMessageReceived("endpoint1").get(0));

        // Only peer #2 receives it as an echo
        assertTrue(client1.waitForBytesMessageReceived(0));

        assertTrue(client2.waitForBytesMessageReceived(1));
        assertArrayEquals(SpincastTestingUtils.TEST_STRING.getBytes("UTF-8"), client2.getBytesMessageReceived().get(0));

        assertTrue(client3.waitForBytesMessageReceived(0));
    }

    @Test
    public void peerIdAlreadyExists() throws Exception {

        final String[] peerIdToUse = new String[]{"peer1"};

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }

            @Override
            public void onPeerMessage(DefaultWebsocketContext context, byte[] message) {
                super.onPeerMessage(context, message);

                // Echo the message back to the sender.
                context.sendMessageToCurrentPeer(message);
            }

        };
        getRouter().websocket("/ws").handle(controller);

        assertNull(controller.getEndpointManager("endpoint1"));

        peerIdToUse[0] = "peer1";
        WebsocketClientTest client1 = new WebsocketClientTest();
        WebsocketClientWriter writer1 = websocket("/ws").disableSslCertificateErrors().connect(client1);
        assertNotNull(writer1);

        peerIdToUse[0] = "peer2";
        WebsocketClientTest client2 = new WebsocketClientTest();
        WebsocketClientWriter writer2 = websocket("/ws").disableSslCertificateErrors().connect(client2);
        assertNotNull(writer2);

        //==========================================
        // "peer1" id again: invalid
        //==========================================
        peerIdToUse[0] = "peer1";
        WebsocketClientTest client3 = new WebsocketClientTest();

        try {
            @SuppressWarnings("unused")
            WebsocketClientWriter writer3 = websocket("/ws").disableSslCertificateErrors().connect(client3);
            fail();
        } catch (Exception ex) {
        }

        //==========================================
        // Close "peer1"
        //==========================================
        controller.getEndpointManager("endpoint1").closePeer("peer1");

        //assertTrue(controller.waitNrbPeerConnected("endpoint1", 1));
        SpincastTestingUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return getServer().getWebsocketEndpointManager("endpoint1").getPeersIds().size() == 1;
            }
        }, 5000);

        //==========================================
        // Now "peer1" id is free again
        //==========================================
        peerIdToUse[0] = "peer1";
        WebsocketClientTest client4 = new WebsocketClientTest();
        WebsocketClientWriter writer4 = websocket("/ws").disableSslCertificateErrors().connect(client4);
        assertNotNull(writer4);
        assertTrue(controller.waitNrbPeerConnected("endpoint1", 2));
    }

    @Test
    public void messagesPerPeers() throws Exception {

        final String[] peerIdToUse = new String[]{"peer1"};
        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return peerIdToUse[0];
                    }
                };
            }

        };
        getRouter().websocket("/ws").handle(controller);

        assertNull(controller.getEndpointManager("endpoint1"));

        peerIdToUse[0] = "peer1";
        WebsocketClientTest client1 = new WebsocketClientTest();
        WebsocketClientWriter writer1 = websocket("/ws").disableSslCertificateErrors().connect(client1);
        assertNotNull(writer1);

        peerIdToUse[0] = "peer2";
        WebsocketClientTest client2 = new WebsocketClientTest();
        WebsocketClientWriter writer2 = websocket("/ws").disableSslCertificateErrors().connect(client2);
        assertNotNull(writer2);

        peerIdToUse[0] = "peer3";
        WebsocketClientTest client3 = new WebsocketClientTest();
        WebsocketClientWriter writer3 = websocket("/ws").disableSslCertificateErrors().connect(client3);
        assertNotNull(writer3);

        assertTrue(controller.isEndpointOpen("endpoint1"));

        // Peer #2 sends a message
        writer2.sendMessage(SpincastTestingUtils.TEST_STRING);
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer1", 0));
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer2", 1));
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer3", 0));

        // Peer #2 sends another message
        writer2.sendMessage(SpincastTestingUtils.TEST_STRING);
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer1", 0));
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer2", 2));
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer3", 0));

        // Peer #1 sends a message
        writer1.sendMessage(SpincastTestingUtils.TEST_STRING);
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer1", 1));
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer2", 2));
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer3", 0));

        // Peer #3 sends a message
        writer3.sendMessage(SpincastTestingUtils.TEST_STRING);
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer1", 1));
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer2", 2));
        assertTrue(controller.waitForStringMessageReceived("endpoint1", "peer3", 1));

        assertTrue(controller.waitForStringMessageReceived("endpoint1", 4));

    }

    @Test
    public void skipFilters() throws Exception {

        final Set<String> inFilters = new HashSet<>();

        getRouter().ALL().pos(-10).id("myBeforeFilter").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                inFilters.add("before");
            }
        });

        getRouter().ALL().pos(-10).id("myBeforeFilter2").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                inFilters.add("before2");
            }
        });

        getRouter().ALL().pos(10).id("myAfterFilter").handle(new Handler<DefaultRequestContext>() {

            @Override
            public void handle(DefaultRequestContext context) {
                inFilters.add("after");
            }
        });

        DefaultWebsocketControllerTest controller = new DefaultWebsocketControllerTest(getServer()) {

            @Override
            public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                return new WebsocketConnectionConfig() {

                    @Override
                    public String getEndpointId() {
                        return "endpoint1";
                    }

                    @Override
                    public String getPeerId() {
                        return "peer1";
                    }
                };
            }

        };
        getRouter().websocket("/ws").skip("myBeforeFilter").handle(controller);

        assertNull(controller.getEndpointManager("endpoint1"));

        WebsocketClientTest client1 = new WebsocketClientTest();
        WebsocketClientWriter writer1 = websocket("/ws").disableSslCertificateErrors().connect(client1);
        assertNotNull(writer1);

        assertTrue(controller.waitNrbPeerConnected("endpoint1", 1));

        assertTrue(inFilters.size() == 1);
        assertTrue(inFilters.contains("before2"));
    }
}
