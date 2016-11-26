package org.spincast.plugins.undertow;

import java.util.Set;

public interface UndertowWebsocketEndpointWriter {

    /**
     * Sends a <code>ping</code> message to all peers.
     * 
     */
    public void sendPings(WebsocketPeersWriteCallback callback);

    /**
     * Sends a String message to specific
     * peers.
     */
    public void sendMessage(Set<String> peerIds, String message, WebsocketPeersWriteCallback callback);

    /**
     * Sends a byte array message to specific
     * peers.
     * 
     * @throws PeersClosedWriteException if some peers connections were
     * found to be closed when trying to write.
     */
    public void sendMessage(Set<String> peerIds, byte[] bytes, WebsocketPeersWriteCallback callback);

    /**
     * Sends a "closing connection" message to specified peers.
     * 
     * No exception thrown if the message can't be sent.
     */
    public void sendClosingConnection(int closingCode, String closingReason, Set<String> peerIds,
                                      ClosedEventSentCallback callback);

}
