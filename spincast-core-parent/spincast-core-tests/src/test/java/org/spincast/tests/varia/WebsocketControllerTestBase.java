package org.spincast.tests.varia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.server.Server;
import org.spincast.core.websocket.WebsocketConnectionConfig;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.websocket.WebsocketController;
import org.spincast.core.websocket.WebsocketEndpointManager;
import org.spincast.testing.core.utils.SpincastTestUtils;
import org.spincast.testing.core.utils.TrueChecker;

public abstract class WebsocketControllerTestBase<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                                 implements WebsocketController<R, W> {

    private final Server server;
    private final boolean randomEndpointId;
    private final boolean randomPeerId;

    private final Map<String, WebsocketEndpointManager> endpointManagers = new HashMap<String, WebsocketEndpointManager>();

    private final Map<String, Map<String, List<String>>> stringMessageReceived = new HashMap<String, Map<String, List<String>>>();
    private final Map<String, Map<String, List<byte[]>>> bytesMessageReceived = new HashMap<String, Map<String, List<byte[]>>>();
    private final Map<String, List<String>> onPeerConnectedReceived = new HashMap<String, List<String>>();

    protected Map<String, WebsocketEndpointManager> getEndpointManagers() {
        return this.endpointManagers;
    }

    public WebsocketControllerTestBase(Server server) {
        this(server, false, false);
    }

    public WebsocketControllerTestBase(Server server, boolean randomEndpointId, boolean randomPeerId) {
        this.server = server;
        this.randomEndpointId = randomEndpointId;
        this.randomPeerId = randomPeerId;
    }

    protected Server getServer() {
        return this.server;
    }

    protected boolean isRandomEndpointId() {
        return this.randomEndpointId;
    }

    protected boolean isRandomPeerId() {
        return this.randomPeerId;
    }

    @Override
    public WebsocketConnectionConfig onPeerPreConnect(R context) {

        return new WebsocketConnectionConfig() {

            @Override
            public String getEndpointId() {
                if (isRandomEndpointId()) {
                    return "Endpoint-" + UUID.randomUUID().toString();
                }
                return "endpoint1";
            }

            @Override
            public String getPeerId() {
                if (isRandomPeerId()) {
                    return "Peer-" + UUID.randomUUID().toString();
                }
                return "peer1";
            }
        };
    }

    protected Map<String, List<String>> getStringMessagesPerPeerId(String endpointId) {
        Map<String, List<String>> messagesByPeers = this.stringMessageReceived.get(endpointId);
        if (messagesByPeers == null) {
            messagesByPeers = new HashMap<String, List<String>>();
            this.stringMessageReceived.put(endpointId, messagesByPeers);
        }

        return messagesByPeers;
    }

    public List<String> getStringMessageReceived(String endpointId) {

        Map<String, List<String>> messagesByPeers = getStringMessagesPerPeerId(endpointId);

        List<String> allEndpointMessages = new ArrayList<String>();
        for (List<String> peerMessages : messagesByPeers.values()) {
            allEndpointMessages.addAll(peerMessages);
        }

        return allEndpointMessages;
    }

    public List<String> getStringMessageReceived(String endpointId, String peerId) {

        Map<String, List<String>> messagesByPeers = getStringMessagesPerPeerId(endpointId);

        List<String> messages = messagesByPeers.get(peerId);
        if (messages == null) {
            messages = new ArrayList<String>();
            messagesByPeers.put(peerId, messages);
        }
        return messages;
    }

    public boolean hasReceivedStringMessage(String messageExpected) {
        if (messageExpected == null) {
            return false;
        }

        for (Map<String, List<String>> messagesByPeers : this.stringMessageReceived.values()) {
            if (messagesByPeers != null) {
                for (List<String> messages : messagesByPeers.values()) {
                    if (messages != null) {
                        for (String message : messages) {
                            if (messageExpected.equals(message)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    protected Map<String, List<byte[]>> getBytesMessagesPerPeerId(String endpointId) {
        Map<String, List<byte[]>> messagesByPeers = this.bytesMessageReceived.get(endpointId);
        if (messagesByPeers == null) {
            messagesByPeers = new HashMap<String, List<byte[]>>();
            this.bytesMessageReceived.put(endpointId, messagesByPeers);
        }

        return messagesByPeers;
    }

    public List<byte[]> getBytesMessageReceived(String endpointId) {

        Map<String, List<byte[]>> messagesByPeers = getBytesMessagesPerPeerId(endpointId);

        List<byte[]> allEndpointMessages = new ArrayList<byte[]>();
        for (List<byte[]> peerMessages : messagesByPeers.values()) {
            allEndpointMessages.addAll(peerMessages);
        }

        return allEndpointMessages;
    }

    public List<byte[]> getBytesMessageReceived(String endpointId, String peerId) {

        Map<String, List<byte[]>> messagesByPeers = getBytesMessagesPerPeerId(endpointId);

        List<byte[]> messages = messagesByPeers.get(peerId);
        if (messages == null) {
            messages = new ArrayList<byte[]>();
            messagesByPeers.put(peerId, messages);
        }
        return messages;
    }

    protected List<String> getPeerIdsConnectedFromEventsReceived(String endpointId) {
        List<String> peerIds = this.onPeerConnectedReceived.get(endpointId);
        if (peerIds == null) {
            peerIds = new ArrayList<String>();
            this.onPeerConnectedReceived.put(endpointId, peerIds);
        }
        return peerIds;
    }

    @Override
    public void onEndpointReady(WebsocketEndpointManager endpointManager) {
        getEndpointManagers().put(endpointManager.getEndpointId(), endpointManager);
    }

    @Override
    public void onPeerConnected(W context) {
        List<String> peerIds = this.onPeerConnectedReceived.get(context.getEndpointId());
        if (peerIds == null) {
            peerIds = new ArrayList<String>();
            this.onPeerConnectedReceived.put(context.getEndpointId(), peerIds);
        }
        peerIds.add(context.getPeerId());
    }

    @Override
    public void onPeerMessage(W context, String message) {
        getStringMessageReceived(context.getEndpointId(), context.getPeerId()).add(message);
    }

    @Override
    public void onPeerMessage(W context, byte[] message) {
        getBytesMessageReceived(context.getEndpointId(), context.getPeerId()).add(message);
    }

    @Override
    public void onPeerClosed(W context) {
        List<String> peerIds = this.onPeerConnectedReceived.get(context.getEndpointId());
        peerIds.remove(context.getPeerId());
    }

    @Override
    public void onEndpointClosed(String endpointId) {
        getEndpointManagers().remove(endpointId);
        this.onPeerConnectedReceived.clear();
    }

    public WebsocketEndpointManager getEndpointManager(String endpointId) {
        return getEndpointManagers().get(endpointId);
    }

    public boolean isEndpointOpen(String endpointId) {
        return getEndpointManager(endpointId) != null && !getEndpointManager(endpointId).isClosing();
    }

    public boolean waitForEndpointClosed(String endpointId) {
        return waitForEndpointClosed(endpointId, 5000);
    }

    public boolean waitForEndpointClosed(final String endpointId,
                                         int maxMillisecToWait) {

        return SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return !isEndpointOpen(endpointId);
            }

        }, maxMillisecToWait);
    }

    public boolean waitPeerConnected(String endpointId, String peerId) {
        return waitPeerConnected(endpointId, peerId, 5000);
    }

    public boolean waitPeerConnected(final String endpointId,
                                     final String peerId,
                                     int maxMillisecToWait) {

        return SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return isEndpointOpen(endpointId) && getEndpointManager(endpointId).getPeersIds().contains(peerId);
            }

        }, maxMillisecToWait);

    }

    public boolean waitNrbPeerConnected(String endpointId,
                                        int nbrPeers) {
        return waitNrbPeerConnected(endpointId, nbrPeers, 5000);
    }

    public boolean waitNrbPeerConnected(final String endpointId,
                                        final int nbrPeers,
                                        int maxMillisecToWait) {

        return SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {

                return getPeerIdsConnectedFromEventsReceived(endpointId).size() >= nbrPeers &&
                       getEndpointManager(endpointId) != null &&
                       getEndpointManager(endpointId).getPeersIds().size() >= nbrPeers;
            }

        }, maxMillisecToWait);
    }

    public boolean waitNrbPeerConnectedMax(String endpointId,
                                           int nbrPeersMax) {
        return waitNrbPeerConnectedMax(endpointId, nbrPeersMax, 5000);
    }

    public boolean waitNrbPeerConnectedMax(final String endpointId,
                                           final int nbrPeersMax,
                                           int maxMillisecToWait) {

        return SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return getEndpointManager(endpointId) == null ||
                       getEndpointManager(endpointId).getPeersIds().size() <= nbrPeersMax;
            }

        }, maxMillisecToWait);
    }

    public boolean waitForStringMessageReceived(String endpointId, int nbrExpected) {
        return waitForStringMessageReceived(endpointId, nbrExpected, 5000);
    }

    public boolean waitForStringMessageReceived(final String endpointId, final int nbrExpected, int maxMillisecToWait) {
        return SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return getStringMessageReceived(endpointId).size() >= nbrExpected;
            }

        }, maxMillisecToWait);
    }

    public boolean waitForStringMessageReceived(String endpointId, String peerId, int nbrExpected) {
        return waitForStringMessageReceived(endpointId, peerId, nbrExpected, 5000);
    }

    public boolean waitForStringMessageReceived(final String endpointId,
                                                final String peerId,
                                                final int nbrExpected,
                                                int maxMillisecToWait) {
        return SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return getStringMessageReceived(endpointId, peerId).size() >= nbrExpected;
            }

        }, maxMillisecToWait);
    }

    public boolean waitForStringMessageReceived(final String endpointId,
                                                final String peerId,
                                                String messageExpected) {
        return waitForStringMessageReceived(endpointId, peerId, messageExpected, 5000);
    }

    public boolean waitForStringMessageReceived(final String endpointId,
                                                final String peerId,
                                                final String messageExpected,
                                                int maxMillisecToWait) {
        return SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {

                List<String> stringMessageReceived = getStringMessageReceived(endpointId, peerId);

                if (stringMessageReceived.size() == 0) {
                    return false;
                }
                for (int i = stringMessageReceived.size() - 1; i >= 0; i--) {
                    if (messageExpected.equals(stringMessageReceived.get(i))) {
                        return true;
                    }
                }
                return false;
            }

        }, maxMillisecToWait);
    }

    public boolean waitForBytesMessageReceived(String endpointId, int nbrExpected) {
        return waitForBytesMessageReceived(endpointId, nbrExpected, 5000);
    }

    public boolean waitForBytesMessageReceived(final String endpointId, final int nbrExpected, int maxMillisecToWait) {
        return SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return getBytesMessageReceived(endpointId).size() >= nbrExpected;
            }

        }, maxMillisecToWait);
    }

    public boolean waitForBytesMessageReceived(String endpointId, String peerId, int nbrExpected) {
        return waitForBytesMessageReceived(endpointId, peerId, nbrExpected, 5000);
    }

    public boolean waitForBytesMessageReceived(final String endpointId,
                                               final String peerId,
                                               final int nbrExpected,
                                               int maxMillisecToWait) {
        return SpincastTestUtils.waitForTrue(new TrueChecker() {

            @Override
            public boolean check() {
                return getBytesMessageReceived(endpointId, peerId).size() >= nbrExpected;
            }

        }, maxMillisecToWait);
    }

}
