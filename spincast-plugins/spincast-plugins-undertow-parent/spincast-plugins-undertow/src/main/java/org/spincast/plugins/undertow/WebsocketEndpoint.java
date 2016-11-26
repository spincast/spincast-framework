package org.spincast.plugins.undertow;

import org.spincast.core.websocket.WebsocketEndpointManager;

import io.undertow.server.HttpServerExchange;

public interface WebsocketEndpoint extends WebsocketEndpointManager {

    public void handleConnectionRequest(HttpServerExchange exchange, String peerId);

}
