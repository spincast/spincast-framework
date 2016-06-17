package org.spincast.core.websocket;

import java.util.Set;

/**
 * Component to send WebSocket messages on an endpoint.
 */
public interface IWebsocketEndpointWriter {

    /**
     * Sends a String message to all
     * peers of the endpoint.
     */
    public void sendMessage(String message);

    /**
     * Sends a String message to a specific
     * peer.
     */
    public void sendMessage(String peerId, String message);

    /**
     * Sends a String message to specific
     * peers.
     * 
     */
    public void sendMessage(Set<String> peerIds, String message);

    /**
     * Sends a String message to all
     * peers except the specified one.
     * 
     */
    public void sendMessageExcept(String peerId, String message);

    /**
     * Sends a String message to all
     * peers except the specified ones.
     */
    public void sendMessageExcept(Set<String> peerIds, String message);

    /**
     * Sends a byte array message to all
     * peers of the endpoint.
     */
    public void sendMessage(byte[] message);

    /**
     * Sends a byte array message to a specific
     * peer.
     * 
     */
    public void sendMessage(String peerId, byte[] message);

    /**
     * Sends a byte array message to specific
     * peers.
     * 
     */
    public void sendMessage(Set<String> peerIds, byte[] message);

    /**
     * Sends a byte array message to all
     * peers except the specified one.
     * 
     */
    public void sendMessageExcept(String peerId, byte[] message);

    /**
     * Sends a byte array message to all
     * peers except the specified ones.
     * 
     */
    public void sendMessageExcept(Set<String> peerIds, byte[] message);

}
