package org.spincast.tests.varia;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.server.Server;
import org.spincast.core.websocket.DefaultWebsocketContext;

public class DefaultWebsocketControllerTest extends
                                            WebsocketControllerTestBase<DefaultRequestContext, DefaultWebsocketContext> {

    public DefaultWebsocketControllerTest(Server server) {
        super(server);
    }

    public DefaultWebsocketControllerTest(Server server, boolean randomEndpointId, boolean randomPeerId) {
        super(server, randomEndpointId, randomPeerId);
    }
}
