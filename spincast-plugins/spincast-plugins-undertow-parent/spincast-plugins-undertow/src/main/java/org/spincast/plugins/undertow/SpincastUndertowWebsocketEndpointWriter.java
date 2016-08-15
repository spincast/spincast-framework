package org.spincast.plugins.undertow;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.undertow.config.ISpincastUndertowConfig;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.undertow.websockets.core.WebSocketCallback;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

public class SpincastUndertowWebsocketEndpointWriter implements IUndertowWebsocketEndpointWriter {

    protected final Logger logger = LoggerFactory.getLogger(SpincastUndertowWebsocketEndpointWriter.class);

    private final ISpincastUndertowConfig spincastUndertowConfig;
    private final Map<String, WebSocketChannel> channels;
    private byte[] pingBytes;

    /**
     * Interface to create a write executor.
     */
    protected static interface IWriteExecutor {

        public void write(WebSocketChannel channel, WebSocketCallback<Void> callback);

        public void writeErrors(Set<String> peerIds);
    }

    /**
     * Constructor
     */
    @AssistedInject
    public SpincastUndertowWebsocketEndpointWriter(@Assisted Map<String, WebSocketChannel> channels,
                                                   ISpincastUndertowConfig spincastUndertowConfig) {
        this.channels = channels;
        this.spincastUndertowConfig = spincastUndertowConfig;
    }

    protected Map<String, WebSocketChannel> getChannelsMap() {
        return this.channels;
    }

    protected ISpincastUndertowConfig getSpincastUndertowConfig() {
        return this.spincastUndertowConfig;
    }

    protected byte[] getPingBytes() {
        if(this.pingBytes == null) {
            try {
                this.pingBytes = getSpincastUndertowConfig().getWebsocketPingMessageString().getBytes("UTF-8");
            } catch(Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }
        return this.pingBytes;
    }

    @Override
    public void sendPings(final IWebsocketPeersWriteCallback callback) {

        write(getChannelsMap().keySet(), new IWriteExecutor() {

            @Override
            public void write(WebSocketChannel channel, WebSocketCallback<Void> writeCallback) {
                WebSockets.sendPing(ByteBuffer.wrap(getPingBytes()), channel, writeCallback);
            }

            @Override
            public void writeErrors(Set<String> peerIds) {
                callback.connectionClosed(peerIds);
            }
        });
    }

    @Override
    public void sendMessage(Set<String> peerIds,
                            final String message,
                            final IWebsocketPeersWriteCallback callback) {

        write(peerIds, new IWriteExecutor() {

            @Override
            public void write(WebSocketChannel channel, WebSocketCallback<Void> writeCallback) {
                WebSockets.sendText(message, channel, writeCallback);
            }

            @Override
            public void writeErrors(Set<String> peerIds) {
                callback.connectionClosed(peerIds);
            }
        });
    }

    @Override
    public void sendMessage(Set<String> peerIds,
                            final byte[] bytes,
                            final IWebsocketPeersWriteCallback callback) {

        write(peerIds, new IWriteExecutor() {

            @Override
            public void write(WebSocketChannel channel, WebSocketCallback<Void> writeCallback) {

                //==========================================
                // A new ByteBuffer must be created for each peer
                // otherwise it is emptied after the first peer!
                //==========================================
                final ByteBuffer buffer = ByteBuffer.wrap(bytes);
                WebSockets.sendBinary(buffer, channel, writeCallback);
            }

            @Override
            public void writeErrors(Set<String> peerIds) {
                callback.connectionClosed(peerIds);
            }
        });
    }

    @Override
    public void sendClosingConnection(final int closingCode,
                                      final String closingReason,
                                      Set<String> peerIds,
                                      final IClosedEventSentCallback callback) {

        if(peerIds == null || peerIds.size() == 0) {
            callback.done();
            return;
        }

        try {
            write(peerIds, new IWriteExecutor() {

                @Override
                public void write(WebSocketChannel channel, WebSocketCallback<Void> writeCallback) {
                    WebSockets.sendClose(closingCode, closingReason, channel, writeCallback);
                }

                @Override
                public void writeErrors(Set<String> peerIds) {
                    if(peerIds != null && peerIds.size() > 0) {
                        SpincastUndertowWebsocketEndpointWriter.this.logger.debug("Error sending 'Closed' messages to " +
                                                                                  peerIds.size() +
                                                                                  " peers.");
                    }

                    callback.done();
                }
            });
        } catch(Exception ex) {
            this.logger.error("Exception trying to send 'Closed' messages to peers : " + ex.getMessage());
            callback.done();
            return;
        }
    }

    /**
     * Calls the executor's write(...) method for each peers and, when all
     * asynchrounous calls are done, calls its writeErrors(...) with
     * the ids of the peers for which the write failed with an IOException. 
     */
    public void write(Set<String> peerIds,
                      final IWriteExecutor executor) {

        if(peerIds == null || peerIds.size() == 0) {
            executor.writeErrors(new HashSet<String>());
            return;
        }

        Map<String, WebSocketChannel> channelsMap = getChannelsMap();

        final Set<String> peerIdsRemaining = new HashSet<String>(peerIds);
        final Set<String> peerIdsWriteErrors = new HashSet<String>();

        for(final String peerId : peerIds) {

            WebSocketChannel channel = channelsMap.get(peerId);
            if(channel == null) {
                peerIdsRemaining.remove(peerId);
                if(peerIdsRemaining.size() == 0) {
                    executor.writeErrors(peerIdsWriteErrors);
                    return;
                }
                continue;
            }

            try {

                executor.write(channel, new WebSocketCallback<Void>() {

                    @Override
                    public void onError(WebSocketChannel channel, Void context, Throwable throwable) {

                        //==========================================
                        // Currently, we only keep the IOExceptions, they indicate
                        // that the connection is not alive anymore.
                        //==========================================
                        if(throwable instanceof IOException || !channel.isOpen()) {
                            peerIdsWriteErrors.add(peerId);
                        } else {
                            SpincastUndertowWebsocketEndpointWriter.this.logger.error("An exception which is not a IOException occured while trying to write to a " +
                                                                                      "Websocket peer: " + throwable);
                        }

                        peerIdsRemaining.remove(peerId);
                        if(peerIdsRemaining.size() == 0) {
                            executor.writeErrors(peerIdsWriteErrors);
                        }
                    }

                    @Override
                    public void complete(WebSocketChannel channel, Void context) {

                        peerIdsRemaining.remove(peerId);
                        if(peerIdsRemaining.size() == 0) {
                            executor.writeErrors(peerIdsWriteErrors);
                        }
                    }
                });

            } catch(Exception ex) {

                this.logger.debug("Unable to send 'closing Websocket connection' to peer '" + peerId + "' : " + ex.getMessage());

                peerIdsRemaining.remove(peerId);
                if(peerIdsRemaining.size() == 0) {
                    executor.writeErrors(peerIdsWriteErrors);
                    return;
                }
            }
        }
    }

}
