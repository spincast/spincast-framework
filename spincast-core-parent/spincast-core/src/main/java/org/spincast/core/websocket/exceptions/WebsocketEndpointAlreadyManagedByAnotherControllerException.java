package org.spincast.core.websocket.exceptions;

/**
 * Thrown if the WebSocket endpoint is already managed by another
 * controller.
 */
public class WebsocketEndpointAlreadyManagedByAnotherControllerException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String currentManagingControllerKey;
    private final String newControllerKey;

    public WebsocketEndpointAlreadyManagedByAnotherControllerException(String currentManagingControllerKey,
                                                                       String newControllerKey) {
        super();
        this.currentManagingControllerKey = currentManagingControllerKey;
        this.newControllerKey = newControllerKey;
    }

    public String getCurrentManagingControllerKey() {
        return this.currentManagingControllerKey;
    }

    public String getNewControllerKey() {
        return this.newControllerKey;
    }
}
