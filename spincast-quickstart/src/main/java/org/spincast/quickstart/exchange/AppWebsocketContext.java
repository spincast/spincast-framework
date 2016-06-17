package org.spincast.quickstart.exchange;

import org.spincast.core.websocket.IWebsocketPeerManager;
import org.spincast.core.websocket.WebsocketContextBase;
import org.spincast.core.websocket.WebsocketContextBaseDeps;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Implementation of our custom Websocket context type.
 */
public class AppWebsocketContext extends WebsocketContextBase<IAppWebsocketContext>
                                 implements IAppWebsocketContext {

    @AssistedInject
    public AppWebsocketContext(@Assisted("endpointId") String endpointId,
                               @Assisted("peerId") String peerId,
                               @Assisted IWebsocketPeerManager peerManager,
                               WebsocketContextBaseDeps<IAppWebsocketContext> deps) {
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
