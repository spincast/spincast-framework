package org.spincast.plugins.httpclient.websocket;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.spincast.plugins.httpclient.SpincastHttpClientConfig;

import com.google.inject.ImplementedBy;

/**
 * Configurations for the Spincast Http Client with WebSocket support
 * plugin.
 * 
 * We use "@ImplementedBy" to specify the default configurations
 * to use if none is specified in a Guice module.
 */
@ImplementedBy(SpincastHttpClientWithWebsocketConfigDefault.class)
public interface SpincastHttpClientWithWebsocketConfig extends SpincastHttpClientConfig {

    /**
     * Are automatic pings enabled when a WebSocket
     * connection is established?
     * 
     * Default to <code>true</code>.
     */
    public boolean isWebsocketAutomaticPingEnabled();

    /**
     * When automatic pings are enabled for WebSocket
     * connections, how many seconds should be waited
     * between two pings?
     * 
     * Defaults to <code>20</code> seconds.
     */
    public int getWebsocketAutomaticPingIntervalSeconds();

    /**
     * The ping text to use. Must be <code>&lt; 125</code> characters.
     * 
     * Defaults to <code>"__ping"</code>
     */
    public String getWebsocketPingMessageString();

    /**
     * The maximum number of concurrent threads used when
     * sending events to the <code>WebsocketClientHandler</code>. 
     * 
     * Defaults to <code>10</code>.
     */
    public int getWebsocketThreadExecutorForClientEventsThreadNumber();

    /**
     * The timeout <code>amount</code> before cancelling a task when
     * sending events to the <code>WebsocketClientHandler</code>. 
     * 
     * Defaults to <code>60</code>.
     */
    public int getWebsocketThreadExecutorForClientEventsTimeoutAmount();

    /**
     * The timeout <code>TimeUnit</code> before cancelling a task when
     * sending events to the <code>WebsocketClientHandler</code>. 
     * 
     * Defaults to <code>SECONDS</code>.
     */
    public TimeUnit getWebsocketThreadExecutorForClientEventsTimeoutTimeUnit();

    /**
     * The <code>ThreadFactory</code> to use to create threads to send WebSocket events
     * to the <code>WebsocketClientHandler</code>.
     * 
     * Defaults to <code>null</code>.
     */
    public ThreadFactory getWebsocketThreadExecutorForClientEventsThreadFactory();

    /**
     * The default code to send to the <code>WebsocketClientHandler</code> when a WebSocket connection
     * was found to be closed.
     * Valid codes can be found <a href="https://tools.ietf.org/html/rfc6455#section-7.4.1">here</a>
     * 
     * Defaults to <code>1001</code>: 
     * "1001 indicates that an endpoint is "going away", such as a server
     * going down or a browser having navigated away from a page.."
     */
    public int getWebsocketDefaultClosingCode();

    /**
     * The default reason to send to the <code>WebsocketClientHandler</code> 
     * when a WebSocket connection was found to be closed.
     * 
     * Defaults to an empty message.
     */
    public String getWebsocketDefaultClosingReason();
}
