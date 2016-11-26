package org.spincast.core.websocket;

/**
 * WebSocket endpoint handler.
 */
public interface WebsocketEndpointHandler {

    /**
     * Called when the connection is established with a peer.
     */
    public void onPeerConnected(String peerId);

    /**
     * A String message arrives from a peer.
     */
    public void onPeerMessage(String peerId, String message);

    /**
     * A bytes messages arrives from a peer.
     */
    public void onPeerMessage(String peerId, byte[] message);

    /**
     * A peer closed its connection.
     */
    public void onPeerClosed(String peerId);

    /**
     * The endpoint is actually closed by the server.
     */
    public void onEndpointClosed();

}
