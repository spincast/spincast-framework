package org.spincast.core.websocket;

import com.google.inject.assistedinject.Assisted;

public interface IWebsocketContextFactory<W extends IWebsocketContext<?>> {

    public W create(@Assisted("endpointId") String endpointId,
                    @Assisted("peerId") String peerId,
                    IWebsocketPeerManager websocketWriter);
}
