package org.spincast.tests.varia;

import org.spincast.core.exchange.IDefaultRequestContext;
import org.spincast.core.server.IServer;
import org.spincast.core.websocket.IDefaultWebsocketContext;

public class DefaultWebsocketControllerTest extends
                                            WebsocketControllerTestBase<IDefaultRequestContext, IDefaultWebsocketContext> {

    public DefaultWebsocketControllerTest(IServer server) {
        super(server);
    }

    public DefaultWebsocketControllerTest(IServer server, boolean randomEndpointId, boolean randomPeerId) {
        super(server, randomEndpointId, randomPeerId);
    }
}
