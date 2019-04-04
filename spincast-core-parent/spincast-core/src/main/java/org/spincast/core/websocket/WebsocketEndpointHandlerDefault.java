package org.spincast.core.websocket;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.server.Server;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class WebsocketEndpointHandlerDefault<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                            implements WebsocketEndpointHandler {

    protected static final Logger logger = LoggerFactory.getLogger(WebsocketEndpointHandlerDefault.class);

    private final Server server;
    private final String endpointId;
    private final WebsocketController<R, W> controller;
    private WebsocketEndpointManager endpointManager;
    private final WebsocketContextFactory<W> websocketContextFactory;

    /**
     * Cache of peer contextes.
     */
    private final Map<String, W> peerContextesMap = new HashMap<String, W>();

    /**
     * Constructor
     */
    @AssistedInject
    public WebsocketEndpointHandlerDefault(@Assisted("endpointId") String endpointId,
                                           @Assisted WebsocketController<R, W> controller,
                                           WebsocketContextFactory<W> websocketContextFactory,
                                           Server server) {
        this.endpointId = endpointId;
        this.controller = controller;
        this.websocketContextFactory = websocketContextFactory;
        this.server = server;
    }

    protected String getEndpointId() {
        return this.endpointId;
    }

    protected WebsocketController<R, W> getController() {
        return this.controller;
    }

    protected Server getServer() {
        return this.server;
    }

    protected WebsocketEndpointManager getEndpointManager() {

        if(this.endpointManager == null) {
            this.endpointManager = getServer().getWebsocketEndpointManager(getEndpointId());
        }

        return this.endpointManager;
    }

    protected WebsocketContextFactory<W> getWebsocketContextFactory() {
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

            WebsocketPeerManager peerWriter = createWebsocketPeerManager(peerId);
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
            logger.error("The Websocket manager is null! Skipping message from peer '" + peerId + "': " + message);
            return;
        }

        getController().onPeerMessage(getWebsocketPeerContext(peerId), message);
    }

    @Override
    public void onPeerMessage(String peerId, byte[] message) {

        if(getEndpointManager() == null) {
            logger.error("The Websocket manager is null! Skipping bytes message from peer '" + peerId);
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
    protected WebsocketPeerManager createWebsocketPeerManager(final String peerId) {

        final WebsocketEndpointManager endpointManager = getEndpointManager();

        return new WebsocketPeerManager() {

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
