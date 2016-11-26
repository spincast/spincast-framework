package org.spincast.plugins.undertow;

import java.util.Map;

import io.undertow.websockets.core.WebSocketChannel;

public interface UndertowWebsocketEndpointWriterFactory {

    public UndertowWebsocketEndpointWriter create(Map<String, WebSocketChannel> channels);

}
