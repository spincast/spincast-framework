package org.spincast.testing.defaults;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.exchange.DefaultRequestContextDefault;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.core.websocket.DefaultWebsocketContextDefault;
import org.spincast.core.websocket.WebsocketContext;

/**
 * Base class for testing without an App but when an HTTP
 * server is required.
 */
public abstract class NoAppStartHttpServerTestingBase extends
                                                      NoAppStartHttpServerCustomContextTypesTestingBase<DefaultRequestContext, DefaultWebsocketContext> {

    @Override
    protected Class<? extends RequestContext<?>> getRequestContextImplementationClass() {
        return DefaultRequestContextDefault.class;
    }

    @Override
    protected Class<? extends WebsocketContext<?>> getWebsocketContextImplementationClass() {
        return DefaultWebsocketContextDefault.class;
    }
}
