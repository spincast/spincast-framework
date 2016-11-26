package org.spincast.core.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.spincast.core.websocket.exceptions.WebsocketEndpointAlreadyManagedByAnotherControllerException;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

public class WebsocketEndpointToControllerManagerDefault implements WebsocketEndpointToControllerManager {

    private final Map<String, String> endpointToControllerMap = new HashMap<String, String>();

    private final Object mapLock = new Object();

    protected Map<String, String> getEndpointToControllerMap() {
        return this.endpointToControllerMap;
    }

    protected String createControllerKey(WebsocketController<?, ?> controller) {
        return controller.getClass().getName() + " [ " + controller.hashCode() + " ] ";
    }

    @Override
    public void addEndpointController(String endpointId,
                                      WebsocketController<?, ?> controller) throws WebsocketEndpointAlreadyManagedByAnotherControllerException {

        if(StringUtils.isBlank(endpointId)) {
            throw new RuntimeException("The endpointId can't be empty");
        }

        Objects.requireNonNull(controller, "The controller can't be NULL");

        String controllerKey = createControllerKey(controller);

        synchronized(this.mapLock) {
            String existingControllerKey = getEndpointToControllerMap().get(endpointId);
            if(existingControllerKey != null && !(existingControllerKey.equals(controllerKey))) {
                throw new WebsocketEndpointAlreadyManagedByAnotherControllerException(existingControllerKey, controllerKey);
            }
            getEndpointToControllerMap().put(endpointId, controllerKey);
        }
    }

    @Override
    public void removeEndpointController(String endpointId) {

        if(StringUtils.isBlank(endpointId)) {
            throw new RuntimeException("The endpointId can't be empty");
        }

        synchronized(this.mapLock) {
            getEndpointToControllerMap().remove(endpointId);
        }
    }

    @Override
    public boolean isManagingEndpoint(String endpointId, WebsocketController<?, ?> controller) {

        String controllerKey = getEndpointToControllerMap().get(endpointId);
        if(controllerKey == null) {
            return false;
        }

        String newControllerKey = createControllerKey(controller);
        return controllerKey.equals(newControllerKey);
    }

}
