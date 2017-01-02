package org.spincast.defaults.testing.tests.utils;

import org.spincast.core.websocket.WebsocketContextBase;
import org.spincast.core.websocket.WebsocketContextBaseDeps;
import org.spincast.core.websocket.WebsocketPeerManager;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class WebsocketContextTestingDefault extends WebsocketContextBase<WebsocketContextTesting>
                                                   implements WebsocketContextTesting {

    @AssistedInject
    public WebsocketContextTestingDefault(@Assisted("endpointId") String endpointId,
                                          @Assisted("peerId") String peerId,
                                          @Assisted WebsocketPeerManager peerManager,
                                          WebsocketContextBaseDeps<WebsocketContextTesting> deps) {
        super(endpointId,
              peerId,
              peerManager,
              deps);
    }

    @Override
    public String test2() {
        return "Stromgol";
    }
}