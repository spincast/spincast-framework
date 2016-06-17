package org.spincast.plugins.undertow.config;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.inject.ImplementedBy;

/**
 * Configurations for the Spincast Undertow plugin.
 * 
 * We use "@ImplementedBy" to specify the default configurations
 * to use if none is specified in a Guice module.
 */
@ImplementedBy(SpincastUndertowConfigDefault.class)
public interface ISpincastUndertowConfig {

    /**
     * If <code>true</code>, pings will automatically
     * be sent to peers of a WebSocket endpoint as an
     * heartbeat.
     * 
     * Enabled by default.
     */
    public boolean isWebsocketAutomaticPing();

    /**
     * When the automatic WebSocket pings are enabled, this is the
     * interval (in seconds) between two pings.
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
     * The default code to use when sending a 
     * "closing Websocket connection" event to a peer.
     * Valid codes can be found <a href="https://tools.ietf.org/html/rfc6455#section-7.4.1">here</a>
     * 
     * Defaults to <code>1000</code>, a normal closure.
     */
    public int getWebsocketDefaultClosingCode();

    /**
     * The default reason to use when sending a 
     * "closing Websocket connection" event to a peer.
     * 
     * Defaults to an empty message.
     */
    public String getWebsocketDefaultClosingReason();

    /**
     * The maximum number of concurrent threads used when
     * sending WebSocket events to the application. 
     * 
     * Defaults to <code>100</code>.
     */
    public int getWebsocketThreadExecutorForAppEventsThreadNumber();

    /**
     * The timeout <code>amount</code> before cancelling a task when
     * sending WebSocket events to the application. 
     * 
     * Defaults to <code>60</code>.
     */
    public int getWebsocketThreadExecutorForAppEventsTimeoutAmount();

    /**
     * The timeout <code>TimeUnit</code> before cancelling a task when
     * sending WebSocket events to the application. 
     * 
     * Defaults to <code>SECONDS</code>.
     */
    public TimeUnit getWebsocketThreadExecutorForAppEventsTimeoutTimeUnit();

    /**
     * The <code>ThreadFactory</code> to use to create threads when
     * sending WebSocket events to the application.
     * 
     * Defaults to <code>null</code>.
     */
    public ThreadFactory getWebsocketThreadExecutorForAppEventsThreadFactory();

}
