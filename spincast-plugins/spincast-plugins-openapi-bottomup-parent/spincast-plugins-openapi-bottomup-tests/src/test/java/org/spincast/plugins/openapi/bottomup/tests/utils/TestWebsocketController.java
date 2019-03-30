package org.spincast.plugins.openapi.bottomup.tests.utils;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketConnectionConfig;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.core.websocket.WebsocketController;
import org.spincast.core.websocket.WebsocketEndpointManager;


public class TestWebsocketController<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                    implements WebsocketController<R, W> {

    @Override
    public WebsocketConnectionConfig onPeerPreConnect(R context) {
        return null;
    }

    @Override
    public void onEndpointReady(WebsocketEndpointManager endpointManager) {
    }

    @Override
    public void onPeerConnected(W context) {
    }

    @Override
    public void onPeerMessage(W context, String message) {
    }

    @Override
    public void onPeerMessage(W context, byte[] message) {
    }

    @Override
    public void onPeerClosed(W context) {
    }

    @Override
    public void onEndpointClosed(String endpointId) {
    }
}
