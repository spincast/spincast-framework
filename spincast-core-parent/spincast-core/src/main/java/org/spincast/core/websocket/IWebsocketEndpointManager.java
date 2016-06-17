package org.spincast.core.websocket;

import java.util.Set;

/**
 * Manager for a WebSocket endpoint. Extend IWebsocketEndpointWriter
 * since it can write on the endpoint, but also adds some managements
 * features.
 */
public interface IWebsocketEndpointManager extends IWebsocketEndpointWriter {

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
     */
    public void closeEndpoint();

    /**
     * Closes the entire WebSocket endpoint. 
     * All peer connections of this endpoint will be
     * closed and the endpoint will be removed.
     */
    public void closeEndpoint(int closingCode, String closingReason);
}
