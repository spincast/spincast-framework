package org.spincast.core.websocket;

import java.util.Set;

/**
 * Manager for a WebSocket endpoint. Extends WebsocketEndpointWriter
 * since it can write on the endpoint, but also adds some managements
 * features.
 */
public interface WebsocketEndpointManager extends WebsocketEndpointWriter {

    /**
     * The endpoint id
     */
    public String getEndpointId();

    /**
     * The connected peers' ids.
     */
    public Set<String> getPeersIds();

    /**
     * Closes a specific peer connection on the endpoint.
     * 
     */
    public void closePeer(String peerId);

    /**
     * Closes a specific peer connection on the endpoint.
     */
    public void closePeer(String peerId, int closingCode, String closingReason);

    /**
     * Closes the entire WebSocket endpoint. 
     * All peer connections of this endpoint will be
     * closed and the endpoint will be removed.
     * <p>
     * Will try to send a "closing" message to the peers
     * before closing their connection.
     * </p>
     */
    public void closeEndpoint();

    /**
     * Closes the entire WebSocket endpoint. 
     * All peer connections of this endpoint will be
     * closed and the endpoint will be removed.
     * 
     * @param sendClosingMessageToPeers if <code>true</code>,
     * Spincast will try to send a "closing" message to the peers
     * before closing their connections.
     */
    public void closeEndpoint(boolean sendClosingMessageToPeers);

    /**
     * Closes the entire WebSocket endpoint. 
     * All peer connections of this endpoint will be
     * closed and the endpoint will be removed.
     * <p>
     * Will try to send a "closing" message to the peers
     * before closing their connection.
     * </p>
     */
    public void closeEndpoint(int closingCode, String closingReason);

    /**
     * Is this endpoint closing?
     */
    public boolean isClosing();

    /**
     * Is this endpoint closed?
     */
    public boolean isClosed();
}
