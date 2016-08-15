package org.spincast.plugins.undertow;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.websocket.IWebsocketEndpointHandler;
import org.spincast.plugins.undertow.config.ISpincastUndertowConfig;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.undertow.server.HttpServerExchange;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedBinaryMessage;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.CloseMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;

public class SpincastWebsocketEndpoint implements IWebsocketEndpoint {

    protected final Logger logger = LoggerFactory.getLogger(SpincastWebsocketEndpoint.class);

    public static final String EXCHANGE_VARIABLE_PEER_ID = SpincastWebsocketEndpoint.class.getName() + "_peerId";

    private final IUndertowWebsocketEndpointWriterFactory undertowWebsocketEndpointWriterFactory;
    private final String endpointId;

    //==========================================
    // ConcurrentHashMap : multiple threads may try to
    // use that Map concurrently...
    //==========================================
    private final Map<String, WebSocketChannel> webSocketChannelByPeerId = new ConcurrentHashMap<String, WebSocketChannel>();

    private final IWebsocketEndpointHandler eventsHandler;
    private final ISpincastUndertowConfig spincastUndertowConfig;
    private final ISpincastUndertowUtils spincastUndertowUtils;
    private IUndertowWebsocketEndpointWriter websocketWriter;
    private volatile Thread pingSenderThread = null;

    private volatile boolean endpointIsClosing = false;
    private volatile boolean endpointIsClosed = false;

    private WebSocketProtocolHandshakeHandler webSocketProtocolHandshakeHandler;
    private ExecutorService threadExecutorForAppEvents;

    private final Map<String, Object> peerIdCreationLocks = new ConcurrentHashMap<String, Object>();
    private final Object peerIdCreationLocksCreationLock = new Object();

    /**
     * Constructor
     */
    @AssistedInject
    public SpincastWebsocketEndpoint(@Assisted String endpointId,
                                     @Assisted IWebsocketEndpointHandler eventsHandler,
                                     IUndertowWebsocketEndpointWriterFactory undertowWebsocketEndpointWriterFactory,
                                     ISpincastUndertowConfig spincastUndertowConfig,
                                     ISpincastUndertowUtils spincastUndertowUtils) {
        this.endpointId = endpointId;
        this.eventsHandler = eventsHandler;
        this.undertowWebsocketEndpointWriterFactory = undertowWebsocketEndpointWriterFactory;
        this.spincastUndertowConfig = spincastUndertowConfig;
        this.spincastUndertowUtils = spincastUndertowUtils;
    }

    @Inject
    protected void init() {

        //==========================================
        // Do we send automatic pings?
        //==========================================
        if(getSpincastUndertowConfig().isWebsocketAutomaticPing()) {
            startSendingPings();
        }
    }

    protected Map<String, WebSocketChannel> getWebSocketChannelByPeerId() {
        return this.webSocketChannelByPeerId;
    }

    protected IWebsocketEndpointHandler getEventsHandler() {
        return this.eventsHandler;
    }

    protected IUndertowWebsocketEndpointWriterFactory getUndertowWebsocketEndpointWriterFactory() {
        return this.undertowWebsocketEndpointWriterFactory;
    }

    protected ISpincastUndertowConfig getSpincastUndertowConfig() {
        return this.spincastUndertowConfig;
    }

    protected ISpincastUndertowUtils getSpincastUndertowUtils() {
        return this.spincastUndertowUtils;
    }

    protected IUndertowWebsocketEndpointWriter getUndertowWebsocketWriter() {

        if(this.websocketWriter == null) {
            this.websocketWriter = getUndertowWebsocketEndpointWriterFactory().create(getWebSocketChannelByPeerId());
        }

        return this.websocketWriter;
    }

    /**
     * Gets the creation lock for a peer id.
     */
    protected Object getNewPeerIdLock(String peerId) {
        Object lock = this.peerIdCreationLocks.get(peerId);
        if(lock == null) {
            synchronized(this.peerIdCreationLocksCreationLock) {
                lock = this.peerIdCreationLocks.get(peerId);
                if(lock == null) {
                    lock = new Object();
                    this.peerIdCreationLocks.put(peerId, lock);
                }
            }
        }
        return lock;
    }

    @Override
    public String getEndpointId() {
        return this.endpointId;
    }

    @Override
    public Set<String> getPeersIds() {
        return Collections.unmodifiableSet(getWebSocketChannelByPeerId().keySet());
    }

    @Override
    public void closePeer(String peerId) {
        closePeer(peerId,
                  getSpincastUndertowConfig().getWebsocketDefaultClosingCode(),
                  getSpincastUndertowConfig().getWebsocketDefaultClosingReason());
    }

    @Override
    public void closePeer(final String peerId, int closingCode, String closingReason) {

        validateWebsocketClosingCode(closingCode);

        try {

            IClosedEventSentCallback callback = new IClosedEventSentCallback() {

                @Override
                public void done() {
                    removePeerChannelAndSendPeerClosedAppEvent(peerId);
                }
            };

            //==========================================
            // Try to send a "closing connection" message
            // to the peer before closing its connection...
            //==========================================
            getUndertowWebsocketWriter().sendClosingConnection(closingCode,
                                                               closingReason,
                                                               Sets.newHashSet(peerId),
                                                               callback);

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void removePeerChannelAndSendPeerClosedAppEvent(String peerId) {
        try {
            removePeerChannel(peerId);
            sendPeerClosedAppEvent(peerId);
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void removePeerChannel(String peerId) {
        try {
            WebSocketChannel webSocketChannel = getWebSocketChannelByPeerId().get(peerId);
            if(webSocketChannel != null) {
                if(webSocketChannel.isOpen()) {
                    webSocketChannel.close();
                }

                getWebSocketChannelByPeerId().remove(peerId);

                Set<WebSocketChannel> peerConnections = getWebSocketProtocolHandshakeHandler().getPeerConnections();
                if(peerConnections != null) {
                    peerConnections.remove(webSocketChannel);
                }
            }
        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Called when a write operation detected a closed connection
     * on some peers.
     */
    protected void managePeersWriteConnectionClosed(Set<String> peerIds) {

        if(peerIds == null || peerIds.size() == 0) {
            return;
        }

        for(String peerId : peerIds) {
            removePeerChannelAndSendPeerClosedAppEvent(peerId);
        }
    }

    @Override
    public boolean isClosing() {
        return this.endpointIsClosing;
    }

    @Override
    public boolean isClosed() {
        return this.endpointIsClosed;
    }

    @Override
    public void closeEndpoint() {
        closeEndpoint(true);
    }

    @Override
    public void closeEndpoint(boolean sendClosingMessageToPeers) {
        int closingCode = getSpincastUndertowConfig().getWebsocketDefaultClosingCode();
        String closingReason = getSpincastUndertowConfig().getWebsocketDefaultClosingReason();
        closeEndpoint(closingCode, closingReason, sendClosingMessageToPeers);
    }

    @Override
    public void closeEndpoint(int closingCode, String closingReason) {
        closeEndpoint(closingCode, closingReason, true);
    }

    protected synchronized void closeEndpoint(int closingCode, String closingReason, boolean sendClosingMessageToPeers) {

        validateWebsocketClosingCode(closingCode);

        if(closingReason == null) {
            closingReason = "";
        }

        if(this.endpointIsClosing) {
            this.logger.info("Endpoint '" + getEndpointId() + "' is already closed or closing...");
            return;
        }
        this.endpointIsClosing = true;

        //==========================================
        // Try to send a "closing connection" message
        // to the peers before closing their connection?
        //==========================================
        IClosedEventSentCallback callback = new IClosedEventSentCallback() {

            @Override
            public void done() {

                for(String peerId : getWebSocketChannelByPeerId().keySet()) {
                    try {
                        removePeerChannel(peerId);
                    } catch(Exception ex) {
                        SpincastWebsocketEndpoint.this.logger.error("Error closing peer '" + peerId + "' on endpoint '" +
                                                                    getEndpointId() + "': " +
                                                                    ex.getMessage());
                    }
                }

                //==========================================
                // Now that a potential "closing" message has
                // been sent and that the peers have been removed,
                // the endpoint is really closed.
                //==========================================
                SpincastWebsocketEndpoint.this.endpointIsClosed = true;

                //==========================================
                // We alert the event handler that the endpoint 
                // is now closed.
                //==========================================
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        getEventsHandler().onEndpointClosed();
                    }
                };
                sendAppEventInNewThread(runnable);
            }
        };

        if(sendClosingMessageToPeers) {
            getUndertowWebsocketWriter().sendClosingConnection(closingCode,
                                                               closingReason,
                                                               getWebSocketChannelByPeerId().keySet(),
                                                               callback);
        } else {
            callback.done();
        }
    }

    /**
     * Is the Websocket closing code valid?
     * 
     * @throws an expcetion is the code is not valid.
     */
    protected void validateWebsocketClosingCode(int closingCode) {
        if(!CloseMessage.isValid(closingCode)) {
            throw new RuntimeException("The Websocket endpoint closing code '" + closingCode + "' is not valid. " +
                                       "Please look at http://tools.ietf.org/html/rfc6455#section-7.4");
        }
    }

    /**
     * Starts sending automatic pings to the peers.
     */
    protected void startSendingPings() {

        this.pingSenderThread = new Thread(new Runnable() {

            @Override
            public void run() {

                while(true) {

                    try {
                        Thread.sleep(getSpincastUndertowConfig().getWebsocketAutomaticPingIntervalSeconds() * 1000);
                    } catch(Exception ex) {
                        SpincastWebsocketEndpoint.this.logger.warn("Exception sleeping the thread: " +
                                                                   ex.getMessage());
                    }

                    //==========================================
                    // If the endpoint is closed or if 
                    // startSendingPings() has been called again,
                    // we stop the current ping sending thread.
                    //==========================================
                    if(SpincastWebsocketEndpoint.this.endpointIsClosing ||
                       SpincastWebsocketEndpoint.this.pingSenderThread == null ||
                       SpincastWebsocketEndpoint.this.pingSenderThread != Thread.currentThread()) {
                        break;
                    }

                    getUndertowWebsocketWriter().sendPings(new IWebsocketPeersWriteCallback() {

                        @Override
                        public void connectionClosed(Set<String> peerids) {
                            managePeersWriteConnectionClosed(peerids);
                        }
                    });
                }
            }
        });
        this.pingSenderThread.start();
    }

    protected void stopSendingPings() {
        this.pingSenderThread = null;
    }

    @Override
    public void sendMessage(String message) {
        sendMessage(getPeersIds(), message);
    }

    @Override
    public void sendMessage(String peerId, String message) {
        sendMessage(Sets.newHashSet(peerId), message);
    }

    @Override
    public void sendMessageExcept(String peerId, String message) {
        Set<String> peerIds = new HashSet<>(getPeersIds());
        peerIds.remove(peerId);
        sendMessage(peerIds, message);
    }

    @Override
    public void sendMessageExcept(Set<String> peerIdsToRemove, String message) {
        Set<String> peerIds = new HashSet<>(getPeersIds());
        peerIds.removeAll(peerIdsToRemove);
        sendMessage(peerIds, message);
    }

    @Override
    public void sendMessage(Set<String> peerIds, String message) {

        if(this.endpointIsClosing) {
            this.logger.warn("Endpoint '" + getEndpointId() + "' is closed or closing...");
            return;
        }

        getUndertowWebsocketWriter().sendMessage(peerIds, message, new IWebsocketPeersWriteCallback() {

            @Override
            public void connectionClosed(Set<String> peerIds) {
                managePeersWriteConnectionClosed(peerIds);
            }
        });
    }

    @Override
    public void sendMessage(byte[] message) {
        sendMessage(getPeersIds(), message);
    }

    @Override
    public void sendMessage(String peerId, byte[] message) {
        sendMessage(Sets.newHashSet(peerId), message);
    }

    @Override
    public void sendMessageExcept(String peerId, byte[] message) {
        Set<String> peerIds = new HashSet<>(getPeersIds());
        peerIds.remove(peerId);
        sendMessage(peerIds, message);
    }

    @Override
    public void sendMessageExcept(Set<String> peerIdsToRemove, byte[] message) {
        Set<String> peerIds = new HashSet<>(getPeersIds());
        peerIds.removeAll(peerIdsToRemove);
        sendMessage(peerIds, message);
    }

    @Override
    public void sendMessage(Set<String> peerIds, byte[] message) {

        if(this.endpointIsClosing) {
            this.logger.warn("Endpoint '" + getEndpointId() + "' is closed or closing...");
            return;
        }

        getUndertowWebsocketWriter().sendMessage(peerIds, message, new IWebsocketPeersWriteCallback() {

            @Override
            public void connectionClosed(Set<String> peerIds) {
                managePeersWriteConnectionClosed(peerIds);
            }
        });
    }

    @Override
    public void handleConnectionRequest(HttpServerExchange exchange,
                                        final String peerId) {

        if(this.endpointIsClosing) {
            this.logger.warn("Endpoint '" + getEndpointId() + "' is closed or closing...");
            return;
        }

        try {

            //==========================================
            // Validates that this peer id is not already used
            //==========================================
            if(getWebSocketChannelByPeerId().containsKey(peerId)) {
                throw new RuntimeException("The Websocket endpoint '" + this.endpointId + "' is already used by a peer with " +
                                           "id '" + peerId + "'! Close the existing peer if you want to reuse this id.");
            }

            //==========================================
            // Saves the peer id on the exchange object itself
            // so we can get it back in the callback called
            // by Undertow when the connection is
            // established.
            //==========================================
            getSpincastUndertowUtils().getRequestCustomVariables(exchange).put(EXCHANGE_VARIABLE_PEER_ID, peerId);

            //==========================================
            // And then starts the actual Websocket handshake.
            //==========================================
            getWebSocketProtocolHandshakeHandler().handleRequest(exchange);

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * The handler to use for the Websocket connection.
     */
    protected WebSocketProtocolHandshakeHandler getWebSocketProtocolHandshakeHandler() {

        if(this.webSocketProtocolHandshakeHandler == null) {

            this.webSocketProtocolHandshakeHandler =
                    new WebSocketProtocolHandshakeHandler(new WebSocketConnectionCallback() {

                        @Override
                        public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {

                            //==========================================
                            // Get back the peer id that we saved on the exchange
                            // object.
                            //==========================================
                            final String peerId = getSpincastUndertowUtils().getRequestCustomVariables(exchange)
                                                                            .get(EXCHANGE_VARIABLE_PEER_ID);

                            if(SpincastWebsocketEndpoint.this.endpointIsClosing) {
                                SpincastWebsocketEndpoint.this.logger.warn("The endpoint is closed or closing, the peer '" +
                                                                           peerId +
                                                                           "' onConnect() won't be handled.");
                                return;
                            }

                            //==========================================
                            // A given peer id can be used for one
                            // connection only.
                            //==========================================
                            boolean peerIdAlreadyUsed = false;
                            if(!getWebSocketChannelByPeerId().containsKey(peerId)) {
                                Object newPeerIdLock = getNewPeerIdLock(peerId);
                                synchronized(newPeerIdLock) {
                                    if(!getWebSocketChannelByPeerId().containsKey(peerId)) {
                                        getWebSocketChannelByPeerId().put(peerId, channel);
                                    } else {
                                        peerIdAlreadyUsed = true;
                                    }
                                }
                            } else {
                                peerIdAlreadyUsed = true;
                            }

                            //==========================================
                            // Peer id already used. 
                            // We close the new connection.
                            //==========================================
                            if(peerIdAlreadyUsed) {
                                SpincastWebsocketEndpoint.this.logger.warn("The Websocket endpoint '" + getEndpointId() +
                                                                           "' is already used by a peer with " +
                                                                           "id '" + peerId +
                                                                           "'! The new connection will be closed.");
                                try {
                                    WebSockets.sendClose(CloseMessage.UNEXPECTED_ERROR, "Duplicate peer id", channel, null);
                                    if(channel.isOpen()) {
                                        channel.close();
                                    }
                                } catch(Exception ex) {
                                    SpincastWebsocketEndpoint.this.logger.error("Error closing the duplicate '" + peerId +
                                                                                "' peer's Websocket connection: " +
                                                                                ex.getMessage());
                                }
                                return;
                            }

                            //==========================================
                            // Add event handler to the channel.
                            //==========================================
                            channel.getReceiveSetter().set(new AbstractReceiveListener() {

                                @Override
                                protected void onFullTextMessage(final WebSocketChannel channel,
                                                                 BufferedTextMessage bufferedTextMessage) throws IOException {
                                    String message = bufferedTextMessage.getData();

                                    if(SpincastWebsocketEndpoint.this.endpointIsClosing) {
                                        SpincastWebsocketEndpoint.this.logger.warn("The endpoint is closed or closing, the received message from peer '" +
                                                                                   peerId + "' won't be handled: " + message);
                                        return;
                                    }

                                    //==========================================
                                    // Event : String message
                                    //==========================================
                                    sendOnStringMessageAppEvent(peerId, message);
                                }

                                @Override
                                protected void onFullBinaryMessage(final WebSocketChannel channel,
                                                                   BufferedBinaryMessage message) throws IOException {

                                    if(SpincastWebsocketEndpoint.this.endpointIsClosing) {
                                        SpincastWebsocketEndpoint.this.logger.warn("The endpoint is closed or closing, the received bytes message from peer '" +
                                                                                   peerId + "' won't be handled");
                                        return;
                                    }

                                    ByteBuffer[] byteBuffersArray = message.getData().getResource();
                                    ByteBuffer byteBuffer = WebSockets.mergeBuffers(byteBuffersArray);

                                    //==========================================
                                    // Event : Bytes message
                                    //==========================================
                                    sendOnBytesMessageAppEvent(peerId, byteBuffer.array());
                                }

                                @Override
                                protected void onCloseMessage(CloseMessage cm, WebSocketChannel channel) {

                                    if(SpincastWebsocketEndpoint.this.endpointIsClosing) {
                                        // nothing to do.
                                        return;
                                    }

                                    try {
                                        //==========================================
                                        // Remove peer +
                                        // Event : Peer closed
                                        //==========================================
                                        removePeerChannelAndSendPeerClosedAppEvent(peerId);
                                    } catch(Exception ex) {
                                        SpincastWebsocketEndpoint.this.logger.error("Error closing peer '" + peerId +
                                                                                    "' on endpoint '" +
                                                                                    getEndpointId() + "': " +
                                                                                    ex.getMessage());
                                    }
                                }
                            });

                            channel.resumeReceives();

                            //==========================================
                            // Event : Peer connected
                            //==========================================
                            sendOnPeerConnectedAppEvent(peerId);
                        }
                    });
        }

        return this.webSocketProtocolHandshakeHandler;
    }

    /**
     * Sends a "Peer connected " event to the app.
     */
    protected void sendOnPeerConnectedAppEvent(final String peerId) {

        if(this.endpointIsClosing) {
            return;
        }

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                getEventsHandler().onPeerConnected(peerId);
            }
        };
        sendAppEventInNewThread(runnable);
    }

    /**
     * Sends a "String message" event to the app.
     */
    protected void sendOnStringMessageAppEvent(final String peerId,
                                               final String message) {

        if(this.endpointIsClosing) {
            return;
        }

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                getEventsHandler().onPeerMessage(peerId, message);
            }
        };
        sendAppEventInNewThread(runnable);
    }

    /**
     * Sends a "byte[] message" event to the app.
     */
    protected void sendOnBytesMessageAppEvent(final String peerId,
                                              final byte[] message) {

        if(this.endpointIsClosing) {
            return;
        }

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                getEventsHandler().onPeerMessage(peerId, message);
            }
        };
        sendAppEventInNewThread(runnable);
    }

    /**
     * Sends a "peer closed" event to the app.
     */
    protected void sendPeerClosedAppEvent(final String peerId) {

        if(this.endpointIsClosing) {
            return;
        }

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                getEventsHandler().onPeerClosed(peerId);
            }
        };
        sendAppEventInNewThread(runnable);
    }

    /**
     * Sends an event to the application in a separated thread.
     */
    protected void sendAppEventInNewThread(final Runnable runnable) {
        try {

            Callable<Void> callable = new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    runnable.run();
                    return null;
                }
            };

            Set<Callable<Void>> callables = new HashSet<>();
            callables.add(callable);

            getThreadExecutorForAppEvents().invokeAll(callables,
                                                      getThreadExecutorForAppEventsTimeoutAmount(),
                                                      getThreadExecutorForAppEventsTimeoutTimeUnit());
        } catch(InterruptedException ex) {
            this.logger.error("A Thread used for sending a Websocket event to the application took too long " +
                              "(max " + getThreadExecutorForAppEventsTimeoutAmount() + " " +
                              getThreadExecutorForAppEventsTimeoutTimeUnit().toString() + ")" +
                              "on endpoint " + getEndpointId() + ": " + ex.getMessage());
        } catch(Exception ex) {
            this.logger.error("A Thread used for sending a Websocket event to the application thrown an exception " +
                              "on endpoint " + getEndpointId() + ": " + ex.getMessage());
        }
    }

    /**
     * The timeout amount before cancelling a task when
     * sending events to the application. 
     */
    protected int getThreadExecutorForAppEventsTimeoutAmount() {
        return getSpincastUndertowConfig().getWebsocketThreadExecutorForAppEventsTimeoutAmount();
    }

    /**
     * The timeout unit before cancelling a task when
     * sending events to the application. 
     */
    protected TimeUnit getThreadExecutorForAppEventsTimeoutTimeUnit() {
        return getSpincastUndertowConfig().getWebsocketThreadExecutorForAppEventsTimeoutTimeUnit();
    }

    /**
     * The ExecutorService to use to
     * send events to the application.
     */
    protected ExecutorService getThreadExecutorForAppEvents() {

        if(this.threadExecutorForAppEvents == null) {
            ThreadFactory threadFactory = getThreadExecutorForAppEventsThreadThreadFactory();
            if(threadFactory != null) {
                this.threadExecutorForAppEvents =
                        Executors.newFixedThreadPool(getThreadExecutorForAppEventsThreadNumber(), threadFactory);
            } else {
                this.threadExecutorForAppEvents = Executors.newFixedThreadPool(getThreadExecutorForAppEventsThreadNumber());
            }
        }

        return this.threadExecutorForAppEvents;
    }

    /**
     * The maximum number of concurrent threads used when
     * sending events to the application. 
     */
    protected int getThreadExecutorForAppEventsThreadNumber() {
        return getSpincastUndertowConfig().getWebsocketThreadExecutorForAppEventsThreadNumber();
    }

    /**
     * The ThreadFactory to use for the Executor that
     * sends events to the application. 
     * 
     * @return the ThreadFactory to use or <code>null</code> 
     * to use the default one.
     */
    protected ThreadFactory getThreadExecutorForAppEventsThreadThreadFactory() {
        return getSpincastUndertowConfig().getWebsocketThreadExecutorForAppEventsThreadFactory();
    }

}
