package org.spincast.core.websocket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exceptions.SkipRemainingHandlersException;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.routing.IHandler;
import org.spincast.core.server.IServer;
import org.spincast.core.websocket.exceptions.WebsocketEndpointAlreadyManagedByAnotherControllerException;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * Route handler that manages the upgrade from 
 * a HTTP request to a WebSocket connection, once the 
 * potential "before" filters have been ran.
 */
public class WebsocketRouteHandler<R extends IRequestContext<?>, W extends IWebsocketContext<?>> implements IHandler<R> {

    protected final Logger logger = LoggerFactory.getLogger(WebsocketRouteHandler.class);

    private final IWebsocketRoute<R, W> websocketRoute;
    private final IServer server;
    private final IWebsocketEndpointHandlerFactory<R, W> websocketServerEndpointHandlerFactory;
    private final IWebsocketEndpointToControllerManager websocketEndpointToControllerManager;
    private final Map<String, Object> endpointCreationLock = new ConcurrentHashMap<String, Object>();
    private final Object endpointLockCreationLock = new Object();

    private final Map<String, IWebsocketEndpointHandler> serverEndpointHandlers =
            new HashMap<String, IWebsocketEndpointHandler>();

    @AssistedInject
    public WebsocketRouteHandler(@Assisted IWebsocketRoute<R, W> websocketRoute,
                                 IServer server,
                                 IWebsocketEndpointHandlerFactory<R, W> websocketServerEndpointHandlerFactory,
                                 IWebsocketEndpointToControllerManager websocketEndpointToControllerKeysMap) {
        this.websocketRoute = websocketRoute;
        this.server = server;
        this.websocketServerEndpointHandlerFactory = websocketServerEndpointHandlerFactory;
        this.websocketEndpointToControllerManager = websocketEndpointToControllerKeysMap;
    }

    protected IWebsocketRoute<R, W> getWebsocketRoute() {
        return this.websocketRoute;
    }

    protected IServer getServer() {
        return this.server;
    }

    protected Map<String, IWebsocketEndpointHandler> getServerEndpointHandlers() {
        return this.serverEndpointHandlers;
    }

    protected IWebsocketEndpointHandlerFactory<R, W> getWebsocketServerEndpointHandlerFactory() {
        return this.websocketServerEndpointHandlerFactory;
    }

    protected IWebsocketEndpointToControllerManager getWebsocketEndpointToControllerManager() {
        return this.websocketEndpointToControllerManager;
    }

    protected Object getEndpointCreationLock(String endpointId) {
        Object lock = this.endpointCreationLock.get(endpointId);
        if(lock == null) {
            synchronized(this.endpointLockCreationLock) {
                lock = this.endpointCreationLock.get(endpointId);
                if(lock == null) {
                    lock = new Object();
                    this.endpointCreationLock.put(endpointId, lock);
                }
            }
        }
        return lock;
    }

    /**
     * The handle() method for WebSocket handshaking, 
     * called by the front controller once the potential
     * "before" filters have been ran.
     */
    @Override
    public void handle(R context) {

        //==========================================
        // Calls the "Pre Connect" method on the app controller. 
        // This allows the controller to decide of the ids to use for the
        // endpoind and for the peer.
        //==========================================
        IWebsocketConnectionConfig connectionConfig =
                getWebsocketRoute().getWebsocketController().onPeerPreConnect(context);

        //==========================================
        // Controller refused to upgrade to WebSocket
        // and manages the request as it wants.
        //==========================================
        if(connectionConfig == null) {
            manageCancellationFromOnPeerPreConnect();
            return;
        }

        String endpointId = connectionConfig.getEndpointId();
        if(StringUtils.isBlank(endpointId)) {
            throw new RuntimeException("The WebSocket endpoint id can't be empty.");
        }

        String peerId = connectionConfig.getPeerId();
        if(StringUtils.isBlank(peerId)) {
            peerId = generatePeerId(context, endpointId);
        }

        if(!getWebsocketEndpointToControllerManager().isManagingEndpoint(endpointId,
                                                                         getWebsocketRoute().getWebsocketController())) {

            synchronized(getEndpointCreationLock(endpointId)) {

                if(!getWebsocketEndpointToControllerManager().isManagingEndpoint(endpointId,
                                                                                 getWebsocketRoute().getWebsocketController())) {

                    //==========================================
                    // Saves this endpoint-to-controller relation.
                    //==========================================
                    try {
                        getWebsocketEndpointToControllerManager().addEndpointController(endpointId,
                                                                                        getWebsocketRoute().getWebsocketController());
                    } catch(WebsocketEndpointAlreadyManagedByAnotherControllerException ex) {
                        throw new RuntimeException("The endpoint '" + endpointId +
                                                   "' is already managed by another controller : " +
                                                   ex.getCurrentManagingControllerKey() + ". It can't be managed by: " +
                                                   ex.getNewControllerKey());
                    }

                    IWebsocketEndpointManager websocketEndpointManager = getServer().getWebsocketEndpointManager(endpointId);
                    if(websocketEndpointManager != null) {
                        throw new RuntimeException("No existing controller was found to manage the WebSocket endpoint '" +
                                                   endpointId + "' " +
                                                   "but we found a " + IWebsocketEndpointManager.class.getSimpleName() +
                                                   " object in the server.");

                    }

                    //==========================================
                    // Creates an handler for the endpoint.
                    //==========================================
                    IWebsocketEndpointHandler websocketEndpointHandler =
                            createWebsocketEndpointHandler(endpointId, getWebsocketRoute().getWebsocketController());

                    //==========================================
                    // Creates the endpoint.
                    //==========================================
                    websocketEndpointManager = getServer().websocketCreateEndpoint(endpointId, websocketEndpointHandler);

                    //==========================================
                    // Set the endpoint manager on the controller.
                    // We call this method *synchronously* so we are sure the manager
                    // is available to the controller when the connection with
                    // the first peer is actually established.... The controller
                    // should not block here!
                    //==========================================
                    getWebsocketRoute().getWebsocketController().onEndpointReady(websocketEndpointManager);
                }
            }
        }

        //==========================================
        // Websocket handshake with the peer!
        //
        // The controller will receive a "onPeerConnected" event
        // when the connection with the peer is established.
        //==========================================
        getServer().websocketConnection(context.exchange(),
                                        endpointId,
                                        peerId);

        //==========================================
        // The Websocket connection is established and
        // the HTTP request is over.
        //==========================================
        throw new SkipRemainingHandlersException();
    }

    /**
     * Managed a <code>null</code> returned by the onPeerPreConnect()
     * method of the controller.
     */
    protected void manageCancellationFromOnPeerPreConnect() {

        //==========================================
        // By default, to have a standardized behavior,
        // we output the response as is. Therefore,
        // we garantee that the "after" filters are never
        // run when a WebSocket route is used.
        //==========================================
        throw new SkipRemainingHandlersException();
    }

    protected IWebsocketEndpointHandler createWebsocketEndpointHandler(final String endpointId,
                                                                       final IWebsocketController<R, W> controller) {

        final IWebsocketEndpointHandler controllerHandler =
                getWebsocketServerEndpointHandlerFactory().create(endpointId, controller);

        return new IWebsocketEndpointHandler() {

            @Override
            public void onPeerMessage(String peerId, byte[] message) {
                controllerHandler.onPeerMessage(peerId, message);
            }

            @Override
            public void onPeerMessage(String peerId, String message) {
                controllerHandler.onPeerMessage(peerId, message);
            }

            @Override
            public void onPeerConnected(String peerId) {
                controllerHandler.onPeerConnected(peerId);
            }

            @Override
            public void onPeerClosed(String peerId) {
                controllerHandler.onPeerClosed(peerId);
            }

            @Override
            public void onEndpointClosed() {

                synchronized(getEndpointCreationLock(endpointId)) {

                    //==========================================
                    // Removes the endpoint from the global 
                    // endpoint-to-controller manager
                    //==========================================
                    getWebsocketEndpointToControllerManager().removeEndpointController(endpointId);

                    controllerHandler.onEndpointClosed();
                }
            }
        };
    }

    /**
     * Generate a peer id when none is specified by the
     * controller.
     */
    protected String generatePeerId(R context, String endpointId) {
        return endpointId + "_" + UUID.randomUUID().toString();
    }

}
