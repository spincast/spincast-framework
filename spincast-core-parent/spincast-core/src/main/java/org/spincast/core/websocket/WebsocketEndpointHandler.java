package org.spincast.core.websocket;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exchange.IRequestContext;
import org.spincast.core.server.IServer;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class WebsocketEndpointHandler<R extends IRequestContext<?>, W extends IWebsocketContext<?>>
                                     implements IWebsocketEndpointHandler {

    protected final Logger logger = LoggerFactory.getLogger(WebsocketEndpointHandler.class);

    private final IServer server;
    private final String endpointId;
    private final IWebsocketController<R, W> controller;
    private IWebsocketEndpointManager endpointManager;
    private final IWebsocketContextFactory<W> websocketContextFactory;

    /**
     * Cache of peer contextes.
     */
    private final Map<String, W> peerContextesMap = new HashMap<String, W>();

    /**
     * Constructor
     */
    @AssistedInject
    public WebsocketEndpointHandler(@Assisted("endpointId") String endpointId,
                                    @Assisted IWebsocketController<R, W> controller,
                                    IWebsocketContextFactory<W> websocketContextFactory,
                                    IServer server) {
        this.endpointId = endpointId;
        this.controller = controller;
        this.websocketContextFactory = websocketContextFactory;
        this.server = server;
    }

    protected String getEndpointId() {
        return this.endpointId;
    }

    protected IWebsocketController<R, W> getController() {
        return this.controller;
    }

    protected IServer getServer() {
        return this.server;
    }

    protected IWebsocketEndpointManager getEndpointManager() {

        if(this.endpointManager == null) {
            this.endpointManager = getServer().getWebsocketEndpointManager(getEndpointId());
        }

        return this.endpointManager;
    }

    protected IWebsocketContextFactory<W> getWebsocketContextFactory() {
        return this.websocketContextFactory;
    }

    protected Map<String, W> getPeerContextesMap() {
        return this.peerContextesMap;
    }

    protected W getWebsocketPeerContext(String peerId) {

        W peerContext = getPeerContextesMap().get(peerId);
        if(peerContext == null) {
            if(getEndpointManager() == null) {
                throw new RuntimeException("The endpoint manager is null, we can't create a Websocket context.");
            }

            IWebsocketPeerManager peerWriter = createWebsocketPeerManager(peerId);
            peerContext = getWebsocketContextFactory().create(getEndpointId(), peerId, peerWriter);
            getPeerContextesMap().put(peerId, peerContext);
        }

        return peerContext;
    }

    @Override
    public void onPeerConnected(String peerId) {
        getController().onPeerConnected(getWebsocketPeerContext(peerId));
    }

    @Override
    public void onPeerMessage(String peerId, String message) {

        if(getEndpointManager() == null) {
            this.logger.error("The Websocket manager is null! Skipping message from peer '" + peerId + "': " + message);
            return;
        }

        getController().onPeerMessage(getWebsocketPeerContext(peerId), message);
    }

    @Override
    public void onPeerMessage(String peerId, byte[] message) {

        if(getEndpointManager() == null) {
            this.logger.error("The Websocket manager is null! Skipping bytes message from peer '" + peerId);
            return;
        }

        getController().onPeerMessage(getWebsocketPeerContext(peerId), message);
    }

    @Override
    public void onEndpointClosed() {
        getController().onEndpointClosed(getEndpointId());
    }

    @Override
    public void onPeerClosed(String peerId) {

        try {
            getController().onPeerClosed(getWebsocketPeerContext(peerId));
        } finally {
            // Clears cache
            getPeerContextesMap().remove(peerId);
        }
    }

    /**
     * Creates a peer specific manager from the endpoint manager.
     */
    protected IWebsocketPeerManager createWebsocketPeerManager(final String peerId) {

        final IWebsocketEndpointManager endpointManager = getEndpointManager();

        return new IWebsocketPeerManager() {

            @Override
            public void sendMessage(String message) {
                endpointManager.sendMessage(peerId, message);
            }

            @Override
            public void sendMessage(byte[] bytes) {
                endpointManager.sendMessage(peerId, bytes);
            }

            @Override
            public void closeConnection() {

                try {
                    endpointManager.closePeer(peerId);
                } finally {
                    // Clears cache
                    getPeerContextesMap().remove(peerId);
                }
            }
        };
    }

}
