package org.spincast.core.websocket;

public interface WebsocketConnectionConfig {

    /**
     * The endpoint id to use. If the same id is used for many
     * peers, the endpoint will be shared.
     */
    public String getEndpointId();

    /**
     * The id to attribute to the connecting peer.
     * If <code>null</code>, a random id will be generated.
     * 
     * Must be unique for the endpoint!
     */
    public String getPeerId();

}
