package org.spincast.website.controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.websocket.IDefaultWebsocketContext;
import org.spincast.core.websocket.IWebsocketConnectionConfig;
import org.spincast.core.websocket.IWebsocketController;
import org.spincast.core.websocket.IWebsocketEndpointManager;
import org.spincast.shaded.org.apache.commons.io.IOUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.website.exchange.IAppRequestContext;

import com.google.inject.Inject;

/**
 * WebSockets demo controller
 */
public class WebsocketsDemoEchoAllController implements IWebsocketController<IAppRequestContext, IDefaultWebsocketContext> {

    protected final Logger logger = LoggerFactory.getLogger(WebsocketsDemoEchoAllController.class);

    private IWebsocketEndpointManager endpointManager;

    private static List<String> peerNamesAll;
    private volatile static int peerNamePos = 0;

    @Inject
    protected void init() {

        //==========================================
        // Load available peer names on startup
        //==========================================
        getPeerNamesAll();
    }

    protected static List<String> getPeerNamesAll() {

        if(peerNamesAll == null) {

            InputStream stream = null;
            InputStreamReader reader = null;
            BufferedReader bufReader = null;
            try {

                peerNamesAll = new ArrayList<>();

                stream = WebsocketsDemoEchoAllController.class.getResourceAsStream("/varia/peerNames.txt");
                reader = new InputStreamReader(stream, "UTF-8");
                bufReader = new BufferedReader(reader);

                String line = bufReader.readLine();
                while(line != null) {

                    line = line.trim();

                    if(StringUtils.isAlphanumeric(line) && line.length() >= 5) {
                        peerNamesAll.add(Character.toUpperCase(line.charAt(0)) + line.substring(1));
                    }
                    line = bufReader.readLine();
                }

                Collections.shuffle(peerNamesAll);

            } catch(Exception ex) {
                throw SpincastStatics.runtimize(ex);
            } finally {
                IOUtils.closeQuietly(bufReader);
                IOUtils.closeQuietly(reader);
                IOUtils.closeQuietly(stream);
            }
        }

        return peerNamesAll;
    }

    protected IWebsocketEndpointManager getEndpointManager() {
        return this.endpointManager;
    }

    @Override
    public IWebsocketConnectionConfig onPeerPreConnect(IAppRequestContext context) {

        if(getEndpointManager() != null && getEndpointManager().getPeersIds().size() > 100) {
            context.response().setStatusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);
            context.response().sendPlainText("Maximum number of peers reached.");
            return null;
        }

        return new IWebsocketConnectionConfig() {

            @Override
            public String getEndpointId() {
                return "chatEndpoint";
            }

            @Override
            public String getPeerId() {
                return generatePeerId();
            }
        };
    }

    protected synchronized String generatePeerId() {

        if(peerNamePos > getPeerNamesAll().size() - 1) {
            peerNamePos = 0;
        }

        String peerName = getPeerNamesAll().get(peerNamePos++);
        return peerName;
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
        getEndpointManager().sendMessage(context.getPeerId() + " : \"" + message + "\"");
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
