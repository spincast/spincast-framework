package org.spincast.core.websocket;

import com.google.inject.assistedinject.Assisted;

public interface WebsocketContextFactory<W extends WebsocketContext<?>> {

    public W create(@Assisted("endpointId") String endpointId,
                    @Assisted("peerId") String peerId,
                    WebsocketPeerManager websocketWriter);
}
