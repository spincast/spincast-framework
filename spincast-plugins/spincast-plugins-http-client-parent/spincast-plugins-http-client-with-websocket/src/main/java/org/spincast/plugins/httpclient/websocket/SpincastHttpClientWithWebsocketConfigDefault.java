package org.spincast.plugins.httpclient.websocket;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.spincast.plugins.httpclient.SpincastHttpClientConfigDefault;

/**
 * Default configurations for the Spincast Http Client with 
 * Websocket support plugin.
 */
public class SpincastHttpClientWithWebsocketConfigDefault extends SpincastHttpClientConfigDefault
                                                          implements ISpincastHttpClientWithWebsocketConfig {

    @Override
    public boolean isWebsocketAutomaticPingEnabled() {
        return true;
    }

    @Override
    public int getWebsocketAutomaticPingIntervalSeconds() {
        return 20;
    }

    @Override
    public String getWebsocketPingMessageString() {
        return "__ping";
    }

    @Override
    public int getWebsocketThreadExecutorForClientEventsThreadNumber() {
        return 10;
    }

    @Override
    public int getWebsocketThreadExecutorForClientEventsTimeoutAmount() {
        return 60;
    }

    @Override
    public TimeUnit getWebsocketThreadExecutorForClientEventsTimeoutTimeUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    public ThreadFactory getWebsocketThreadExecutorForClientEventsThreadFactory() {
        return null;
    }

    @Override
    public int getWebsocketDefaultClosingCode() {
        return 1001; // "1001 indicates that an endpoint is "going away", such as a server
                     // going down or a browser having navigated away from a page.."
                     // https://tools.ietf.org/html/rfc6455#section-7.4.1
    }

    @Override
    public String getWebsocketDefaultClosingReason() {
        return "";
    }

}
