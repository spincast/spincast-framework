package org.spincast.plugins.undertow;

import org.spincast.core.websocket.IWebsocketEndpointManager;

import io.undertow.server.HttpServerExchange;

public interface IWebsocketEndpoint extends IWebsocketEndpointManager {

    public void handleConnectionRequest(HttpServerExchange exchange, String peerId);

}
