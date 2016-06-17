package org.spincast.website.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.core.websocket.IWebsocketConnectionConfig;
import org.spincast.core.websocket.IWebsocketController;
import org.spincast.core.websocket.IWebsocketEndpointManager;
import org.spincast.website.exchange.IAppRequestContext;

public class ShowcaseWebsocketEchoAllController implements IWebsocketController<IAppRequestContext, IDefaultWebsocketContext> {

    protected final Logger logger = LoggerFactory.getLogger(ShowcaseWebsocketEchoAllController.class);

    private IWebsocketEndpointManager endpointManager;

    protected IWebsocketEndpointManager getEndpointManager() {
        return this.endpointManager;
    }

    @Override
    public IWebsocketConnectionConfig onPeerPreConnect(IAppRequestContext context) {

        return new IWebsocketConnectionConfig() {

            @Override
            public String getEndpointId() {
                return "chatEndpoint";
            }

            @Override
            public String getPeerId() {
                return UUID.randomUUID().toString();
            }
        };
    }

    @Override
    public void onEndpointReady(IWebsocketEndpointManager endpointManager) {
        this.endpointManager = endpointManager;
    }

    @Override
    public void onPeerConnected(IDefaultWebsocketContext context) {
        this.logger.debug("Peer connected : " + context.getPeerId());
        context.sendMessageToCurrentPeer("Your generated peer id is " + context.getPeerId());
    }

    @Override
    public void onPeerMessage(IDefaultWebsocketContext context, String message) {
        this.logger.debug("message received from peer '" + context.getPeerId() + "': " + message);

        //==========================================
        // Echoes the message back to all peers.
        //==========================================
        getEndpointManager().sendMessage(context.getPeerId() + "' : \"" + message + "\"");
    }

    @Override
    public void onPeerMessage(IDefaultWebsocketContext context, byte[] message) {
        try {
            this.logger.debug("message received from peer '" + context.getPeerId() + "': " + new String(message, "UTF-8"));
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public void onPeerClosed(IDefaultWebsocketContext context) {
        this.logger.debug("Peer '" + context.getPeerId() + "' closed the connection.");
    }

    @Override
    public void onEndpointClosed(String endpointId) {
        this.logger.debug("Endpoint closed.");
    }

}
