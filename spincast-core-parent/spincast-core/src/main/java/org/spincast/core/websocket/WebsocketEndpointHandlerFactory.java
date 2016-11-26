package org.spincast.core.websocket;

import org.spincast.core.exchange.RequestContext;

import com.google.inject.assistedinject.Assisted;

public interface WebsocketEndpointHandlerFactory<R extends RequestContext<?>, W extends WebsocketContext<?>> {

    public WebsocketEndpointHandler create(@Assisted("endpointId") String endpointId,
                                           @Assisted WebsocketController<R, W> controller);

}
