package org.spincast.defaults.testing;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.exchange.DefaultRequestContextDefault;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.core.websocket.DefaultWebsocketContextDefault;
import org.spincast.core.websocket.WebsocketContext;

public abstract class NoAppTestingBase extends
                                       NoAppCustomContextTypesTestingBase<DefaultRequestContext, DefaultWebsocketContext> {

    @Override
    protected Class<? extends RequestContext<?>> getRequestContextImplementationClass() {
        return DefaultRequestContextDefault.class;
    }

    @Override
    protected Class<? extends WebsocketContext<?>> getWebsocketContextImplementationClass() {
        return DefaultWebsocketContextDefault.class;
    }

}
