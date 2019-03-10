package org.spincast.tests.websocket;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.server.Server;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.core.websocket.WebsocketConnectionConfig;
import org.spincast.core.websocket.WebsocketEndpointManager;
import org.spincast.plugins.httpclient.websocket.WebsocketClientWriter;
import org.spincast.shaded.org.apache.commons.lang3.RandomUtils;
import org.spincast.testing.defaults.NoAppWebsocketTestingBase;
import org.spincast.tests.varia.DefaultWebsocketControllerTest;
import org.spincast.tests.varia.WebsocketClientTest;

import com.google.common.collect.Sets;

public class LoadTest extends NoAppWebsocketTestingBase {

    protected final int nbrWebsocketControllers = 3;
    protected final int nbrEndpointByController = 3;
    protected final int nbrPeerByEndpoint = 15; // at least 4 for some tests

    protected final Map<String, Controller> controllers = new HashMap<String, Controller>();
    protected final Map<String, Controller> controllersByEndpointId = new HashMap<String, Controller>();

    protected final Map<String, Peer> peers = new HashMap<String, Peer>();
    protected final Map<String, Set<Peer>> peersByEndpointId = new HashMap<String, Set<Peer>>();

    protected int getNbrWebsocketControllers() {
        return this.nbrWebsocketControllers;
    }

    protected int getNbrEndpointByController() {
        return this.nbrEndpointByController;
    }

    protected int getNbrPeerByEndpoint() {
        return this.nbrPeerByEndpoint;
    }

    protected Map<String, Controller> getControllers() {
        return this.controllers;
    }

    protected Controller getController(String controllerId) {
        return getControllers().get(controllerId);
    }

    protected Map<String, Controller> getControllersByEndpointId() {
        return this.controllersByEndpointId;
    }

    protected Controller getControllerByEndpointId(String endpointId) {
        return getControllersByEndpointId().get(endpointId);
    }

    protected Map<String, Set<Peer>> getPeersByEndpointId() {
        return this.peersByEndpointId;
    }

    protected Set<Peer> getPeersByEndpointId(String endpointId) {

        Set<Peer> peers = getPeersByEndpointId().get(endpointId);
        if (peers == null) {
            peers = new HashSet<LoadTest.Peer>();
            getPeersByEndpointId().put(endpointId, peers);
        }
        return peers;
    }

    protected Map<String, Peer> getPeers() {
        return this.peers;
    }

    protected Peer getPeer(String peerId) {
        return getPeers().get(peerId);
    }

    protected class Controller extends DefaultWebsocketControllerTest {

        public Controller(Server server) {
            super(server);
        }

        @Override
        public void onPeerMessage(DefaultWebsocketContext context, String message) {

            //==========================================
            // Validates that this message has not already been received.
            //==========================================
            assertFalse(hasReceivedStringMessage(message));

            super.onPeerMessage(context, message);
        }
    }

    protected class Peer extends WebsocketClientTest {

        private final String controllerId;
        private final String endpointId;
        private WebsocketClientWriter writer;
        private final String peerId;

        public Peer(String controllerId, String endpointId, String peerId) {
            this.controllerId = controllerId;
            this.endpointId = endpointId;
            this.peerId = peerId;
        }

        public String getControllerId() {
            return this.controllerId;
        }

        public String getEndpointId() {
            return this.endpointId;
        }

        public String getPeerId() {
            return this.peerId;
        }

        public WebsocketClientWriter getWriter() {
            return this.writer;
        }

        public void setWriter(WebsocketClientWriter writer) {
            this.writer = writer;
        }

        @Override
        public void onEndpointMessage(String message) {

            //==========================================
            // Validates that this message has not already been received.
            //==========================================
            assertFalse(hasReceivedStringMessage(message));

            super.onEndpointMessage(message);
        }
    }

    protected String createControllerId(int controllerPos) {
        return "controller_" + controllerPos;
    }

    protected String createEndpointId(int controllerPos, int endpointPos) {
        return "endpoint_" + controllerPos + "_" + endpointPos;
    }

    protected String createPeerId(int controllerPos, int endpointPos, int peerPos) {
        return "peer_" + controllerPos + "_" + endpointPos + "_" + peerPos;
    }

    protected void createControllers() {

        for (int i = 0; i < getNbrWebsocketControllers(); i++) {

            final int controllerPos = i + 1;
            Controller controller = new Controller(getServer()) {

                @Override
                public WebsocketConnectionConfig onPeerPreConnect(DefaultRequestContext context) {

                    //==========================================
                    // We specify the endpointId and peerId to use by coookie,
                    // so the calling code can control them.
                    //==========================================
                    final String endpointIdCookie = context.request().getCookieValue("endpointId");
                    assertNotNull(endpointIdCookie);

                    final String peerIdCookie = context.request().getCookieValue("peerId");
                    assertNotNull(peerIdCookie);

                    return new WebsocketConnectionConfig() {

                        @Override
                        public String getEndpointId() {
                            return endpointIdCookie;
                        }

                        @Override
                        public String getPeerId() {
                            return peerIdCookie;
                        }
                    };
                }

                @Override
                public void onPeerMessage(DefaultWebsocketContext context, String message) {
                    super.onPeerMessage(context, message);

                    if (message.startsWith("echoall_")) {
                        getEndpointManager(context.getEndpointId()).sendMessage("echo all " + message);
                    } else if (message.startsWith("echo_")) {
                        context.sendMessageToCurrentPeer("echo " + message);
                    } else if (message.startsWith("echoexcept_")) {
                        getEndpointManager(context.getEndpointId()).sendMessageExcept(context.getPeerId(),
                                                                                      "echo except " + message);
                    }
                }

            };
            getControllers().put(createControllerId(controllerPos), controller);
            getRouter().websocket("/ws" + controllerPos).handle(controller);
        }
    }

    protected void connectPeers() {

        //==========================================
        // For each controller
        //==========================================
        for (int controllerPos = 1; controllerPos <= getNbrWebsocketControllers(); controllerPos++) {

            String controllerId = createControllerId(controllerPos);
            Controller controller = getController(controllerId);
            assertNotNull(controller);

            //==========================================
            // The endpoints to create by controller
            //==========================================
            for (int endpointPos = 1; endpointPos <= getNbrEndpointByController(); endpointPos++) {

                String endpointId = createEndpointId(controllerPos, endpointPos);

                //==========================================
                // The peers to create for each endpoint
                //==========================================
                for (int peerPos = 1; peerPos <= getNbrPeerByEndpoint(); peerPos++) {

                    String peerId = createPeerId(controllerPos, endpointPos, peerPos);

                    Peer peer = new Peer(controllerId, endpointId, peerId);
                    WebsocketClientWriter writer =
                            websocket("/ws" + controllerPos).disableSslCertificateErrors()
                                                            .setCookie("endpointId", endpointId)
                                                            .setCookie("peerId", peerId)
                                                            .connect(peer);
                    assertNotNull(writer);
                    peer.setWriter(writer);

                    getPeers().put(peerId, peer);
                    getPeersByEndpointId(endpointId).add(peer);
                }

                getControllersByEndpointId().put(endpointId, controller);
            }
        }
    }

    protected Peer getRandomPeer() {
        int controllerPos = RandomUtils.nextInt(1, getNbrWebsocketControllers() + 1);
        int endpointPos = RandomUtils.nextInt(1, getNbrEndpointByController() + 1);
        int peerPos = RandomUtils.nextInt(1, getNbrPeerByEndpoint() + 1);

        String peerId = createPeerId(controllerPos, endpointPos, peerPos);

        return getPeer(peerId);
    }

    protected String getRandomEndpointId() {
        int controllerPos = RandomUtils.nextInt(1, getNbrWebsocketControllers() + 1);
        int endpointPos = RandomUtils.nextInt(1, getNbrEndpointByController() + 1);

        String endpointId = createEndpointId(controllerPos, endpointPos);

        return endpointId;
    }

    protected void validateMessageNotReceivedByEndpointsAndPeersExcept(String endpointIdExcept, HashSet<String> messages) {

        Map<String, Controller> controllers = getControllersByEndpointId();
        for (Entry<String, Controller> entry : controllers.entrySet()) {

            String endpointId = entry.getKey();
            if (endpointIdExcept.equals(endpointId)) {
                continue;
            }

            Controller controller = entry.getValue();
            assertTrue(Collections.disjoint(messages, controller.getStringMessageReceived(endpointId)));

            Set<Peer> peers = getPeersByEndpointId(endpointId);
            for (Peer peer : peers) {
                assertTrue(Collections.disjoint(messages, peer.getStringMessageReceived()));
            }
        }
    }

    @Test
    public void loadTest() throws Exception {

        //==========================================
        // Creates Websocket routes
        //==========================================
        createControllers();

        //==========================================
        // Connect peers
        //==========================================
        connectPeers();

        //==========================================
        // Validate all peers are connected
        //==========================================
        for (int controllerPos = 1; controllerPos <= getNbrWebsocketControllers(); controllerPos++) {
            Controller controller = getController(createControllerId(controllerPos));
            for (int endpointPos = 1; endpointPos <= getNbrEndpointByController(); endpointPos++) {
                assertTrue(controller.getEndpointManager(createEndpointId(controllerPos, endpointPos)).getPeersIds().size() +
                           " / " + getNbrPeerByEndpoint(),
                           controller.waitNrbPeerConnected(createEndpointId(controllerPos, endpointPos),
                                                           getNbrPeerByEndpoint(),
                                                           20000));
            }
        }

        //==========================================
        // Controller sends messages
        //==========================================
        for (int i = 0; i < 5; i++) {

            String endpointId = getRandomEndpointId();
            Controller controller = getControllerByEndpointId(endpointId);
            WebsocketEndpointManager endpointManager = controller.getEndpointManager(endpointId);
            List<Peer> peers = new ArrayList<>(getPeersByEndpointId(endpointId));

            //==========================================
            // Send message
            //==========================================
            String message = UUID.randomUUID().toString();
            endpointManager.sendMessage(message);

            for (Peer peer : peers) {
                assertTrue(peer.waitForStringMessageReceived(message));
            }

            Set<String> peerIdsTry = new HashSet<String>();
            int nbrPeersTry = RandomUtils.nextInt(1, peers.size() + 1);

            for (int j = 0; j < nbrPeersTry; j++) {
                peerIdsTry.add(peers.get(j).getPeerId());
            }

            //==========================================
            // Sends except
            //==========================================
            String message2 = UUID.randomUUID().toString();
            endpointManager.sendMessageExcept(peerIdsTry, message2);

            for (Peer peer : peers) {
                if (!peerIdsTry.contains(peer.getPeerId())) {
                    assertTrue(peer.waitForStringMessageReceived(message2));
                }
            }
            for (Peer peer : peers) {
                if (peerIdsTry.contains(peer.getPeerId())) {
                    assertFalse(peer.hasReceivedStringMessage(message2));
                }
            }

            //==========================================
            // Sends specific
            //==========================================
            String message3 = UUID.randomUUID().toString();
            endpointManager.sendMessage(peerIdsTry, message3);

            for (Peer peer : peers) {
                if (peerIdsTry.contains(peer.getPeerId())) {
                    assertTrue(peer.waitForStringMessageReceived(message3));
                }
            }
            for (Peer peer : peers) {
                if (!peerIdsTry.contains(peer.getPeerId())) {
                    assertFalse(peer.hasReceivedStringMessage(message3));
                }
            }

            validateMessageNotReceivedByEndpointsAndPeersExcept(endpointId, Sets.newHashSet(message, message2, message3));
        }

        //==========================================
        // Peers send messages
        //==========================================
        for (int i = 0; i < 5; i++) {

            Peer peer = getRandomPeer();

            //==========================================
            // Echo
            //==========================================
            String message = "echo_" + UUID.randomUUID().toString();
            peer.getWriter().sendMessage(message);

            Controller controller = getControllerByEndpointId(peer.getEndpointId());
            assertNotNull(controller);
            assertTrue(controller.waitForStringMessageReceived(peer.getEndpointId(), peer.getPeerId(), message));

            assertTrue(peer.waitForStringMessageReceived("echo " + message));

            //==========================================
            // Echo all
            //==========================================
            String message2 = "echoall_" + UUID.randomUUID().toString();
            peer.getWriter().sendMessage(message2);

            assertTrue(controller.waitForStringMessageReceived(peer.getEndpointId(), peer.getPeerId(), message2));

            Set<Peer> peers = getPeersByEndpointId(peer.getEndpointId());
            for (Peer peer2 : peers) {
                assertTrue(peer2.waitForStringMessageReceived("echo all " + message2));
            }

            //==========================================
            // Echo except
            //==========================================
            String message3 = "echoexcept_" + UUID.randomUUID().toString();
            peer.getWriter().sendMessage(message3);

            assertTrue(controller.waitForStringMessageReceived(peer.getEndpointId(), peer.getPeerId(), message3));

            peers = getPeersByEndpointId(peer.getEndpointId());

            for (Peer peer2 : peers) {
                if (!peer2.getPeerId().equals(peer.getPeerId())) {
                    assertTrue(peer2.waitForStringMessageReceived("echo except " + message3));
                }
            }

            for (Peer peer2 : peers) {
                if (peer2.getPeerId().equals(peer.getPeerId())) {
                    assertFalse(peer2.hasReceivedStringMessage("echo except " + message3));
                }
            }

            validateMessageNotReceivedByEndpointsAndPeersExcept(peer.getEndpointId(),
                                                                Sets.newHashSet(message, message2, message3));
        }

        //==========================================
        // Simultaneous messages sending
        //==========================================
        for (int i = 0; i < 5; i++) {

            String endpointId = getRandomEndpointId();
            Controller controller = getControllerByEndpointId(endpointId);
            final WebsocketEndpointManager endpointManager = controller.getEndpointManager(endpointId);
            List<Peer> peers = new ArrayList<>(getPeersByEndpointId(endpointId));

            final Peer peer1 = peers.get(0);
            final Peer peer2 = peers.get(1);
            final Peer peer3 = peers.get(2);
            final Peer peer4 = peers.get(3);

            final String message1 = UUID.randomUUID().toString();
            final String message2 = "echo_" + UUID.randomUUID().toString();
            final String message3 = "echoall_" + UUID.randomUUID().toString();
            final String message4 = "echoexcept_" + UUID.randomUUID().toString();
            final String message5 = UUID.randomUUID().toString();

            final String message2Echo = "echo " + message2;
            final String message3Echo = "echo all " + message3;
            final String message4Echo = "echo except " + message4;

            Thread t1 = new Thread(new Runnable() {

                @Override
                public void run() {
                    peer1.getWriter().sendMessage(message1);
                }
            });

            Thread t2 = new Thread(new Runnable() {

                @Override
                public void run() {
                    peer2.getWriter().sendMessage(message2);
                }
            });

            Thread t3 = new Thread(new Runnable() {

                @Override
                public void run() {
                    peer3.getWriter().sendMessage(message3);
                }
            });

            Thread t4 = new Thread(new Runnable() {

                @Override
                public void run() {
                    peer4.getWriter().sendMessage(message4);
                }
            });

            Thread t5 = new Thread(new Runnable() {

                @Override
                public void run() {
                    endpointManager.sendMessage(message5);
                }
            });

            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t5.start();

            //==========================================
            // Controller - received
            //==========================================
            assertTrue(controller.waitForStringMessageReceived(endpointId, peer1.getPeerId(), message1));
            assertTrue(controller.waitForStringMessageReceived(endpointId, peer2.getPeerId(), message2));
            assertTrue(controller.waitForStringMessageReceived(endpointId, peer3.getPeerId(), message3));
            assertTrue(controller.waitForStringMessageReceived(endpointId, peer4.getPeerId(), message4));

            //==========================================
            // Peer1 - received
            //==========================================
            assertTrue(peer1.waitForStringMessageReceived(message3Echo));
            assertTrue(peer1.waitForStringMessageReceived(message4Echo));
            assertTrue(peer1.waitForStringMessageReceived(message5));

            //==========================================
            // Peer2 - received
            //==========================================
            assertTrue(peer2.waitForStringMessageReceived(message2Echo));
            assertTrue(peer2.waitForStringMessageReceived(message3Echo));
            assertTrue(peer2.waitForStringMessageReceived(message4Echo));
            assertTrue(peer2.waitForStringMessageReceived(message5));

            //==========================================
            // Peer3 - received
            //==========================================
            assertTrue(peer3.waitForStringMessageReceived(message3Echo));
            assertTrue(peer3.waitForStringMessageReceived(message4Echo));
            assertTrue(peer3.waitForStringMessageReceived(message5));

            //==========================================
            // Peer4 - received
            //==========================================
            assertTrue(peer4.waitForStringMessageReceived(message3Echo));
            assertTrue(peer4.waitForStringMessageReceived(message5));

            //==========================================
            // All other endpoints' controllers and peers - not received
            //==========================================
            validateMessageNotReceivedByEndpointsAndPeersExcept(endpointId,
                                                                Sets.newHashSet(message1,
                                                                                message2Echo,
                                                                                message3Echo,
                                                                                message4Echo,
                                                                                message5));
            //==========================================
            // Controller - not received
            //==========================================
            assertFalse(controller.hasReceivedStringMessage(message5));

            //==========================================
            // Peer 1 - not received
            //==========================================
            assertFalse(peer1.hasReceivedStringMessage(message1));
            assertFalse(peer1.hasReceivedStringMessage(message2Echo));

            //==========================================
            // Peer 2 - not received
            //==========================================
            assertFalse(peer2.hasReceivedStringMessage(message1));

            //==========================================
            // Peer 3 - not received
            //==========================================
            assertFalse(peer3.hasReceivedStringMessage(message1));
            assertFalse(peer3.hasReceivedStringMessage(message2Echo));

            //==========================================
            // Peer 4 - not received
            //==========================================
            assertFalse(peer4.hasReceivedStringMessage(message1));
            assertFalse(peer4.hasReceivedStringMessage(message2Echo));
            assertFalse(peer4.hasReceivedStringMessage(message4Echo));
        }
    }
}
