package org.spincast.core.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * The default WebSocket context to pass to a WebSocket controller
 * when an event arrives (a message is received from the peer, for example).
 */
public class DefaultWebsocketContext extends WebsocketContextBase<IDefaultWebsocketContext>
                                     implements IDefaultWebsocketContext {

    protected final Logger logger = LoggerFactory.getLogger(DefaultWebsocketContext.class);

    @AssistedInject
    public DefaultWebsocketContext(@Assisted("endpointId") String endpointId,
                                   @Assisted("peerId") String peerId,
                                   @Assisted IWebsocketPeerManager websocketWriter,
                                   WebsocketContextBaseDeps<IDefaultWebsocketContext> deps) {
        super(endpointId,
              peerId,
              websocketWriter,
              deps);
    }

}
