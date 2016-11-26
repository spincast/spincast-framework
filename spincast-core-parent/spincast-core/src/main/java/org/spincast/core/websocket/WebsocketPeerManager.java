package org.spincast.core.websocket;

/**
 * Component to manage a specific peer.
 */
public interface WebsocketPeerManager {

    /**
     * Sends a String message to the peer.
     */
    public void sendMessage(String message);

    /**
     * Sends a byte array message to the peer.
     */
    public void sendMessage(byte[] bytes);

    /**
     * Closes the connection with the peer.
     */
    public void closeConnection();

}
