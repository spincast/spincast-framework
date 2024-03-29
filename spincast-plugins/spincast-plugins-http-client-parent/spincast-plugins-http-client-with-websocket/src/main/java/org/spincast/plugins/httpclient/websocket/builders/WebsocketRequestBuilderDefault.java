package org.spincast.plugins.httpclient.websocket.builders;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConstants.HttpHeadersExtra;
import org.spincast.core.cookies.CookieFactory;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.httpclient.HttpResponseFactory;
import org.spincast.plugins.httpclient.builders.HttpRequestBuilderBase;
import org.spincast.plugins.httpclient.websocket.SpincastHttpClientWithWebsocketConfig;
import org.spincast.plugins.httpclient.websocket.SpincastWebsocketClientWriter;
import org.spincast.plugins.httpclient.websocket.WebsocketClientHandler;
import org.spincast.plugins.httpclient.websocket.WebsocketClientWriter;
import org.spincast.plugins.httpclient.websocket.utils.SpincastHttpClientWithWebsocketUtils;
import org.spincast.shaded.org.apache.commons.codec.binary.Base64;
import org.spincast.shaded.org.apache.http.client.methods.HttpGet;
import org.spincast.shaded.org.apache.http.client.methods.HttpRequestBase;
import org.spincast.shaded.org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.spincast.shaded.org.apache.http.cookie.Cookie;
import org.spincast.shaded.org.apache.http.ssl.SSLContexts;
import org.xnio.OptionMap;
import org.xnio.OptionMap.Builder;
import org.xnio.Options;
import org.xnio.Xnio;
import org.xnio.XnioWorker;
import org.xnio.ssl.JsseXnioSsl;
import org.xnio.ssl.XnioSsl;

import com.google.common.net.HttpHeaders;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.WebSocketExtension;
import io.undertow.websockets.client.WebSocketClient;
import io.undertow.websockets.client.WebSocketClient.ConnectionBuilder;
import io.undertow.websockets.client.WebSocketClientNegotiation;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedBinaryMessage;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.CloseMessage;
import io.undertow.websockets.core.WebSocketCallback;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

public class WebsocketRequestBuilderDefault extends HttpRequestBuilderBase<WebsocketRequestBuilder>
                                            implements WebsocketRequestBuilder {

    protected static final Logger logger = LoggerFactory.getLogger(WebsocketRequestBuilderDefault.class);

    private final SpincastHttpClientWithWebsocketUtils spincastHttpClientWithWebsocketUtils;
    private final SpincastHttpClientWithWebsocketConfig spincastHttpClientWithWebsocketConfig;

    private SSLContext sslContext;
    private Integer pingsIntervalSeconds = null;
    private volatile Thread pingSenderThread = null;
    private volatile boolean connectionIsClosed = false;
    private boolean onConnectionClosedEventCalled = false;
    private Object onConnectionClosedEventCalledLock = new Object();

    private WebSocketCallback<Void> websocketWriteHandler = null;
    private WebsocketClientHandler websocketClientHandler = null;
    private ExecutorService threadExecutorForClientEvents;

    /**
     * Constructor
     */
    @AssistedInject
    public WebsocketRequestBuilderDefault(@Assisted String url,
                                          CookieFactory cookieFactory,
                                          HttpResponseFactory spincastHttpResponseFactory,
                                          SpincastHttpClientWithWebsocketUtils spincastHttpClientWithWebsocketUtils,
                                          SpincastHttpClientWithWebsocketConfig spincastHttpClientWithWebsocketConfig) {
        super(url,
              cookieFactory,
              spincastHttpResponseFactory,
              spincastHttpClientWithWebsocketUtils,
              spincastHttpClientWithWebsocketConfig);
        this.spincastHttpClientWithWebsocketConfig = spincastHttpClientWithWebsocketConfig;
        this.spincastHttpClientWithWebsocketUtils = spincastHttpClientWithWebsocketUtils;
    }

    protected SpincastHttpClientWithWebsocketConfig getSpincastHttpClientWithWebsocketConfig() {
        return this.spincastHttpClientWithWebsocketConfig;
    }

    protected SpincastHttpClientWithWebsocketUtils getSpincastHttpClientWithWebsocketUtils() {
        return this.spincastHttpClientWithWebsocketUtils;
    }

    @Override
    protected HttpRequestBase createMethodSpecificHttpRequest(String url) {

        //==========================================
        // Websocket always uses GET to initiate the
        // connection.
        //==========================================
        return new HttpGet(url);
    }

    protected int getPingsIntervalSeconds() {

        if (this.pingsIntervalSeconds == null) {
            return getSpincastHttpClientWithWebsocketConfig().getWebsocketAutomaticPingIntervalSeconds();
        }
        return this.pingsIntervalSeconds;
    }

    protected WebsocketClientHandler getWebsocketClientReader() {
        return this.websocketClientHandler;
    }

    @Override
    public WebsocketRequestBuilder ping(int seconds) {
        this.pingsIntervalSeconds = seconds;
        return this;
    }

    @Override
    public HttpResponse send() {

        //==========================================
        // If we don't use the Udnertow Websocket client
        // to make the HTTP request, we have to add the
        // required headers by ourself.
        //==========================================
        addWebsocketRequestHeaders();

        return super.send();
    }

    protected void addWebsocketRequestHeaders() {
        addHeaderValue(HttpHeadersExtra.SEC_WEBSOCKET_VERSION, "13");

        //==========================================
        // Generates a random value for the "Sec-WebSocket-Key"
        // if none was specified.
        //==========================================
        List<String> keys = getHeaders().get(HttpHeadersExtra.SEC_WEBSOCKET_KEY);
        if (keys == null || keys.size() == 0) {
            keys = new ArrayList<String>();
            keys.add(UUID.randomUUID().toString());
            getHeaders().put(HttpHeadersExtra.SEC_WEBSOCKET_KEY, keys);
        }

        addHeaderValue(HttpHeaders.CONNECTION, "Upgrade");
        addHeaderValue(HttpHeaders.UPGRADE, "websocket");
    }

    @Override
    public WebsocketClientWriter connect(final WebsocketClientHandler websocketClientHandler) {

        this.websocketClientHandler = websocketClientHandler;

        try {
            final WebSocketChannel channel = createWebSocketChannel();

            channel.getReceiveSetter().set(new AbstractReceiveListener() {

                @Override
                protected void onFullTextMessage(final WebSocketChannel channel,
                                                 BufferedTextMessage bufferedTextMessage) throws IOException {

                    String message = bufferedTextMessage.getData();

                    //==========================================
                    // Event : String message
                    //==========================================
                    sendOnStringMessageClientEvent(message);
                }

                @Override
                protected void onFullBinaryMessage(final WebSocketChannel channel,
                                                   BufferedBinaryMessage message) throws IOException {
                    @SuppressWarnings("deprecation") // TODO No alternative for now from Undertow?
                    ByteBuffer[] byteBuffersArray = message.getData().getResource();
                    ByteBuffer byteBuffer = WebSockets.mergeBuffers(byteBuffersArray);

                    //==========================================
                    // Event : byte[] message
                    //==========================================
                    sendOnBytesMessageClientEvent(byteBuffer.array());
                }

                @Override
                protected void onCloseMessage(CloseMessage cm, WebSocketChannel channel) {
                    try {
                        WebsocketRequestBuilderDefault.this.connectionIsClosed = true;
                        if (channel.isOpen()) {
                            channel.close();
                        }

                    } catch (Exception ex) {
                        WebsocketRequestBuilderDefault.logger.warn("Error closing Websocket connection: " + ex.getMessage());
                    }

                    //==========================================
                    // Event : COnnection closed message
                    //==========================================
                    sendOnConnectionClosedMessageClientEvent(cm.getCode(), cm.getReason());
                }
            });

            channel.resumeReceives();

            final WebSocketCallback<Void> webSocketCallback = getWebsocketWriteCallback(websocketClientHandler);

            SpincastWebsocketClientWriter writer = new SpincastWebsocketClientWriter() {

                @Override
                public void sendMessage(byte[] message) {

                    if (WebsocketRequestBuilderDefault.this.connectionIsClosed) {
                        WebsocketRequestBuilderDefault.logger.warn("Connection is closed...");
                        return;
                    }

                    final ByteBuffer buffer = ByteBuffer.wrap(message);
                    WebSockets.sendBinary(buffer, channel, webSocketCallback);
                }

                @Override
                public void sendMessage(String message) {

                    if (WebsocketRequestBuilderDefault.this.connectionIsClosed) {
                        WebsocketRequestBuilderDefault.logger.warn("Connection is closed...");
                        return;
                    }

                    WebSockets.sendText(message, channel, webSocketCallback);
                }

                @Override
                public void closeConnection() {

                    if (WebsocketRequestBuilderDefault.this.connectionIsClosed) {
                        WebsocketRequestBuilderDefault.logger.info("Connection is already closed...");
                        return;
                    }
                    WebsocketRequestBuilderDefault.this.connectionIsClosed = true;

                    try {
                        if (channel.isOpen()) {
                            channel.close();
                        }
                    } catch (Exception ex) {
                        WebsocketRequestBuilderDefault.logger.error("Erreur closing the connection: " + ex.getMessage());
                    }
                }

                @Override
                public void sendPing() {

                    if (WebsocketRequestBuilderDefault.this.connectionIsClosed) {
                        return;
                    }

                    ByteBuffer buffer = null;
                    try {
                        buffer = ByteBuffer.wrap(getSpincastHttpClientWithWebsocketConfig().getWebsocketPingMessageString()
                                                                                           .getBytes("UTF-8"));
                    } catch (Exception ex) {
                        throw SpincastStatics.runtimize(ex);
                    }

                    WebSockets.sendPing(buffer, channel, webSocketCallback);
                }
            };

            //==========================================
            // Start sending pings, if required.
            //==========================================
            startSendingPings(writer);

            return writer;

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }

    }

    protected WebSocketCallback<Void> getWebsocketWriteCallback(final WebsocketClientHandler reader) {
        if (this.websocketWriteHandler == null) {
            this.websocketWriteHandler = new WebSocketCallback<Void>() {

                @Override
                public void onError(WebSocketChannel channel, Void context, Throwable throwable) {
                    if (throwable instanceof IOException || !channel.isOpen()) {
                        WebsocketRequestBuilderDefault.this.connectionIsClosed = true;
                        sendConnectionClosedAppEvent(reader);
                    } else {
                        WebsocketRequestBuilderDefault.logger.error("None IOException when trying to write to Websocket endpoint: " +
                                                                    throwable);
                    }
                }

                @Override
                public void complete(WebSocketChannel channel, Void context) {
                    // ok
                }
            };
        }
        return this.websocketWriteHandler;
    }

    protected void startSendingPings(final SpincastWebsocketClientWriter writer) {

        final int pingsIntervalSeconds = getPingsIntervalSeconds();
        if (pingsIntervalSeconds <= 0) {
            return;
        }

        this.pingSenderThread = new Thread(new Runnable() {

            @Override
            public void run() {

                while (true) {

                    try {
                        Thread.sleep(pingsIntervalSeconds * 1000);
                    } catch (Exception ex) {
                        WebsocketRequestBuilderDefault.logger.warn("Exception sleeping the thread: " +
                                                                   ex.getMessage());
                    }

                    if (WebsocketRequestBuilderDefault.this.connectionIsClosed ||
                        WebsocketRequestBuilderDefault.this.pingSenderThread == null ||
                        WebsocketRequestBuilderDefault.this.pingSenderThread != Thread.currentThread()) {
                        break;
                    }

                    writer.sendPing();
                }
            }
        });
        this.pingSenderThread.start();
    }

    protected Xnio getXnio() {
        return getSpincastHttpClientWithWebsocketUtils().getXnioInstance();
    }

    protected XnioWorker createXnioWorker() {

        try {

            Xnio xnio = getXnio();

            SSLContext sslContext = getSslContext();

            Builder builder = OptionMap.builder();
            builder.set(Options.WORKER_IO_THREADS, 2)
                   .set(Options.WORKER_TASK_CORE_THREADS, 30)
                   .set(Options.WORKER_TASK_MAX_THREADS, 30)
                   .set(Options.TCP_NODELAY, true)
                   .set(Options.CORK, true)
                   .set(Options.SSL_PROTOCOL, sslContext.getProtocol())
                   .set(Options.SSL_PROVIDER, sslContext.getProvider().getName());

            return xnio.createWorker(builder.getMap());

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected SSLContext getSslContext() {

        if (this.sslContext == null) {

            try {
                if (isDisableSslCertificateErrors()) {
                    this.sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
                } else {
                    this.sslContext = SSLContexts.createSystemDefault();
                }

            } catch (Exception ex) {
                throw SpincastStatics.runtimize(ex);
            }
        }
        return this.sslContext;
    }

    protected DefaultByteBufferPool createByteBufferPool() {
        return new DefaultByteBufferPool(false, 2048);
    }

    protected WebSocketChannel createWebSocketChannel() {

        XnioWorker worker = createXnioWorker();
        DefaultByteBufferPool bufferPool = createByteBufferPool();

        return createWebSocketChannel(worker, bufferPool, getUrl(), 0);
    }

    protected WebSocketChannel createWebSocketChannel(XnioWorker worker,
                                                      DefaultByteBufferPool bufferPool,
                                                      String url,
                                                      int redirectionNbr) {
        try {
            ConnectionBuilder connectionBuilder = createConnectionBuilder(worker, bufferPool, url);
            WebSocketChannel webSocketChannel = connectionBuilder.connect().get();
            return webSocketChannel;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected ConnectionBuilder createConnectionBuilder(XnioWorker worker,
                                                        DefaultByteBufferPool bufferPool,
                                                        String url) {

        URI uri = createWebsocketUri(url);

        ConnectionBuilder connectionBuilder = WebSocketClient.connectionBuilder(worker, bufferPool, uri);

        addSslContext(connectionBuilder);

        connectionBuilder.setClientNegotiation(new WebSocketClientNegotiation(createSupportedSubProtocols(),
                                                                              createSupportedExtensions()) {

            @Override
            public void beforeRequest(final Map<String, List<String>> headers) {

                //==========================================
                // Add custom headers
                //==========================================
                addCustomHeaders(headers);

                //==========================================
                // Add cookies
                //==========================================
                addCustomCookies(headers);

                //==========================================
                // Add HTTP authenticatiin headers
                //==========================================
                addHttpAuthHeaders(headers);
            }
        });

        return connectionBuilder;
    }

    protected void addSslContext(ConnectionBuilder connectionBuilder) {
        SSLContext sslContext = getSslContext();

        XnioSsl xnioSsl = new JsseXnioSsl(getXnio(), OptionMap.create(Options.USE_DIRECT_BUFFERS, true), sslContext);
        connectionBuilder.setSsl(xnioSsl);
    }

    protected URI createWebsocketUri(String url) {

        Objects.requireNonNull(url, "The url can't be NULL");

        //==========================================
        // The WebSocketClient's ConnectionBuilder
        // required a SSL connection to start with "wss://",
        // not "https://".
        //==========================================
        try {
            url = url.trim();
            if (url.toLowerCase().startsWith("https://")) {
                url = "wss://" + url.substring("https://".length());
            }
            return new URI(url);
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void addCustomHeaders(Map<String, List<String>> headers) {

        Map<String, List<String>> headersToAdd = getHeaders();
        if (headersToAdd != null) {
            for (Entry<String, List<String>> entry : headersToAdd.entrySet()) {

                List<String> values = headers.get(entry.getKey());
                if (values == null) {
                    values = new ArrayList<String>();
                    headers.put(entry.getKey(), values);
                }
                values.addAll(entry.getValue());
            }
        }
    }

    protected void addCustomCookies(Map<String, List<String>> headers) {

        List<Cookie> cookiesToAdd = getCookieStore().getCookies();
        if (cookiesToAdd == null || cookiesToAdd.size() == 0) {
            return;
        }

        List<String> cookies = headers.get(HttpHeaders.COOKIE);
        if (cookies == null) {
            cookies = new ArrayList<String>();
            headers.put(HttpHeaders.COOKIE, cookies);
        }

        for (Cookie cookie : cookiesToAdd) {
            String cookieHeaderValue = getSpincastHttpClientUtils().apacheCookieToHttpHeaderValue(cookie);
            cookies.add(cookieHeaderValue);
        }
    }

    protected void addHttpAuthHeaders(Map<String, List<String>> headers) {

        if (getHttpAuthUsername() == null) {
            return;
        }

        String value = getHttpAuthUsername() + ":" + getHttpAuthPassword();
        try {
            value = "Basic " + Base64.encodeBase64String(value.getBytes("UTF-8"));
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }

        List<String> values = new ArrayList<String>();
        values.add(value);

        headers.put(HttpHeaders.AUTHORIZATION, values);
    }

    protected List<String> createSupportedSubProtocols() {
        return new ArrayList<String>();
    }

    protected List<WebSocketExtension> createSupportedExtensions() {
        return new ArrayList<WebSocketExtension>();
    }

    protected void sendConnectionClosedAppEvent(final WebsocketClientHandler reader) {

        if (!this.onConnectionClosedEventCalled) {
            synchronized (this.onConnectionClosedEventCalledLock) {
                if (!this.onConnectionClosedEventCalled) {

                    this.onConnectionClosedEventCalled = true;

                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {
                            int code = getSpincastHttpClientWithWebsocketConfig().getWebsocketDefaultClosingCode();
                            String reason = getSpincastHttpClientWithWebsocketConfig().getWebsocketDefaultClosingReason();
                            getWebsocketClientReader().onConnectionClosed(code, reason);
                        }
                    };
                    sendClientEventInNewThread(runnable);
                }
            }
        }
    }

    /**
     * Sends a "String message" event to the app.
     */
    protected void sendOnStringMessageClientEvent(final String message) {

        if (this.connectionIsClosed) {
            return;
        }

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                getWebsocketClientReader().onEndpointMessage(message);
            }
        };
        sendClientEventInNewThread(runnable);
    }

    /**
     * Sends a "Bytes message" event to the app.
     */
    protected void sendOnBytesMessageClientEvent(final byte[] message) {

        if (this.connectionIsClosed) {
            return;
        }

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                getWebsocketClientReader().onEndpointMessage(message);
            }
        };
        sendClientEventInNewThread(runnable);
    }

    /**
     * Sends a "Connection closed message" event to the app.
     */
    protected void sendOnConnectionClosedMessageClientEvent(final int code,
                                                            final String reason) {

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                getWebsocketClientReader().onConnectionClosed(code, reason);
            }
        };
        sendClientEventInNewThread(runnable);
    }

    /**
     * Sends an event to the client in a separated thread.
     */
    protected void sendClientEventInNewThread(final Runnable runnable) {
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

            getThreadExecutorForClientEvents().invokeAll(callables,
                                                         getThreadExecutorForClientEventsTimeoutAmount(),
                                                         getThreadExecutorForClientEventsTimeoutTimeUnit());
        } catch (InterruptedException ex) {
            logger.error("A Thread used for sending a Websocket event to the client took too long " +
                         "(max " + getThreadExecutorForClientEventsTimeoutAmount() + " " +
                         getThreadExecutorForClientEventsTimeoutTimeUnit().toString() + "): " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("A Thread used for sending a Websocket event to the application thrown an exception: " +
                         "" + ex.getMessage());
        }
    }

    /**
     * The timeout amount before cancelling a task when
     * sending events to the application.
     */
    protected int getThreadExecutorForClientEventsTimeoutAmount() {
        return getSpincastHttpClientWithWebsocketConfig().getWebsocketThreadExecutorForClientEventsTimeoutAmount();
    }

    /**
     * The timeout asdasd before cancelling a task when
     * sending events to the application.
     */
    protected TimeUnit getThreadExecutorForClientEventsTimeoutTimeUnit() {
        return getSpincastHttpClientWithWebsocketConfig().getWebsocketThreadExecutorForClientEventsTimeoutTimeUnit();
    }

    /**
     * The ExecutorService to use to
     * send events to the client.
     */
    protected ExecutorService getThreadExecutorForClientEvents() {

        if (this.threadExecutorForClientEvents == null) {
            ThreadFactory threadFactory = getThreadExecutorForClientEventsThreadThreadFactory();
            if (threadFactory != null) {
                this.threadExecutorForClientEvents =
                        Executors.newFixedThreadPool(getThreadExecutorForClientEventsThreadNumber(), threadFactory);
            } else {
                this.threadExecutorForClientEvents = Executors.newFixedThreadPool(getThreadExecutorForClientEventsThreadNumber());
            }
        }

        return this.threadExecutorForClientEvents;
    }

    /**
     * The maximum number of concurrent threads used when
     * sending events to the application.
     */
    protected int getThreadExecutorForClientEventsThreadNumber() {
        return getSpincastHttpClientWithWebsocketConfig().getWebsocketThreadExecutorForClientEventsThreadNumber();
    }

    /**
     * The ThreadFactory to use for the Executor that
     * sends events to the client.
     *
     * @return the ThreadFactory to use or <code>null</code>
     * to use the default one.
     */
    protected ThreadFactory getThreadExecutorForClientEventsThreadThreadFactory() {
        return getSpincastHttpClientWithWebsocketConfig().getWebsocketThreadExecutorForClientEventsThreadFactory();
    }

}
