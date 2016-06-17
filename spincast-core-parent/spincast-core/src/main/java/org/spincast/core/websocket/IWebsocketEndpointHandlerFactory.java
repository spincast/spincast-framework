package org.spincast.core.websocket;

import org.spincast.core.exchange.IRequestContext;

import com.google.inject.assistedinject.Assisted;

public interface IWebsocketEndpointHandlerFactory<R extends IRequestContext<?>, W extends IWebsocketContext<?>> {

    public IWebsocketEndpointHandler create(@Assisted("endpointId") String endpointId,
                                            @Assisted IWebsocketController<R, W> controller);

}
