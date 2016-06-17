package org.spincast.core.websocket;

import org.spincast.core.websocket.exceptions.WebsocketEndpointAlreadyManagedByAnotherControllerException;

public interface IWebsocketEndpointToControllerManager {

    /**
     * Adds a link between a WebSocket endpoint and a controller.
     * Only this controller will be able to manage the endpoint.
     * 
     * @throws 
     */
    public void addEndpointController(String endpointId,
                                      IWebsocketController<?, ?> controller) throws WebsocketEndpointAlreadyManagedByAnotherControllerException;

    /**
     * Removes the link between a WebSocket endpoint and a controller. 
     */
    public void removeEndpointController(String endpointId);

    /**
     * Is the specified controller currently the manager of the
     * endpoint?
     */
    public boolean isManagingEndpoint(String endpointId, IWebsocketController<?, ?> controller);

}
