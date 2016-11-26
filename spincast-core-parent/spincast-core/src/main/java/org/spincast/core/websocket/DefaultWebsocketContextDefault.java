package org.spincast.core.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * The default WebSocket context to pass to a WebSocket controller
 * when an event arrives (a message is received from the peer, for example).
 */
public class DefaultWebsocketContextDefault extends WebsocketContextBase<DefaultWebsocketContext>
                                            implements DefaultWebsocketContext {

    protected final Logger logger = LoggerFactory.getLogger(DefaultWebsocketContextDefault.class);

    @AssistedInject
    public DefaultWebsocketContextDefault(@Assisted("endpointId") String endpointId,
                                          @Assisted("peerId") String peerId,
                                          @Assisted WebsocketPeerManager websocketWriter,
                                          WebsocketContextBaseDeps<DefaultWebsocketContext> deps) {
        super(endpointId,
              peerId,
              websocketWriter,
              deps);
    }

}
