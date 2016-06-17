package org.spincast.plugins.undertow;

import org.spincast.core.websocket.IWebsocketEndpointHandler;

/**
 * Factory to create Websocket endpoints.
 */
public interface IWebsocketEndpointFactory {

    public IWebsocketEndpoint create(String enpointId, IWebsocketEndpointHandler endpointHandler);
}
