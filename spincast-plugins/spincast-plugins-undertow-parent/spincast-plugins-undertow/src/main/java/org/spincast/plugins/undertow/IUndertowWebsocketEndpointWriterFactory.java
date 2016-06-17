package org.spincast.plugins.undertow;

import java.util.Map;

import io.undertow.websockets.core.WebSocketChannel;

public interface IUndertowWebsocketEndpointWriterFactory {

    public IUndertowWebsocketEndpointWriter create(Map<String, WebSocketChannel> channels);

}
