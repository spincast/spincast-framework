package org.spincast.quickstart.exchange;

import org.spincast.core.websocket.WebsocketContextBase;
import org.spincast.core.websocket.WebsocketContextBaseDeps;
import org.spincast.core.websocket.WebsocketPeerManager;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation of our custom Websocket Context type.
 */
public class AppWebsocketContextDefault extends WebsocketContextBase<AppWebsocketContext>
                                        implements AppWebsocketContext {

    @AssistedInject
    public AppWebsocketContextDefault(@Assisted("endpointId") String endpointId,
                                      @Assisted("peerId") String peerId,
                                      @Assisted WebsocketPeerManager peerManager,
                                      WebsocketContextBaseDeps<AppWebsocketContext> deps) {
        super(endpointId,
              peerId,
              peerManager,
              deps);
    }

    @Override
    public void helloCurrentPeer() {
        sendMessageToCurrentPeer("Hello!");
    }
}
