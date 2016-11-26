package org.spincast.plugins.undertow;

import org.spincast.core.websocket.WebsocketEndpointHandler;

/**
 * Factory to create Websocket endpoints.
 */
public interface WebsocketEndpointFactory {

    public WebsocketEndpoint create(String enpointId, WebsocketEndpointHandler endpointHandler);
}
