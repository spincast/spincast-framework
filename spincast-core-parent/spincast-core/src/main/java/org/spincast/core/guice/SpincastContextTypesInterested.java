package org.spincast.core.guice;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;

/**
 * Interface for Guice modules that are interested in
 * the Request Context type and the Websocket Context type.
 */
public interface SpincastContextTypesInterested {

    /**
     * The implementation class to use for RequestContext.
     */
    public void setRequestContextImplementationClass(Class<? extends RequestContext<?>> requestContextImplementationClass);

    /**
     * The implementation class to use for WebsocletContext.
     */
    public void setWebsocketContextImplementationClass(Class<? extends WebsocketContext<?>> websocketContextImplementationClass);
}
