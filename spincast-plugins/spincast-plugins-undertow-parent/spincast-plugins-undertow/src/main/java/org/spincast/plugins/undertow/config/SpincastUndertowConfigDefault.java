package org.spincast.plugins.undertow.config;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import io.undertow.websockets.core.CloseMessage;

/**
 * Default configuration for Spincast Undertow plugin.
 */
public class SpincastUndertowConfigDefault implements SpincastUndertowConfig {

    @Override
    public String getWebsocketPingMessageString() {
        return "__ping";
    }

    @Override
    public boolean isWebsocketAutomaticPing() {
        return true;
    }

    @Override
    public int getWebsocketAutomaticPingIntervalSeconds() {
        return 20;
    }

    @Override
    public int getWebsocketDefaultClosingCode() {
        return CloseMessage.NORMAL_CLOSURE; // 1000
    }

    @Override
    public String getWebsocketDefaultClosingReason() {
        return "";
    }

    @Override
    public int getWebsocketThreadExecutorForAppEventsThreadNumber() {
        return 100;
    }

    @Override
    public int getWebsocketThreadExecutorForAppEventsTimeoutAmount() {
        return 60;
    }

    @Override
    public TimeUnit getWebsocketThreadExecutorForAppEventsTimeoutTimeUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    public ThreadFactory getWebsocketThreadExecutorForAppEventsThreadFactory() {
        return null;
    }

    @Override
    public int getSecondsToWaitForWebSocketEndpointsToBeProperlyClosedBeforeKillingTheServer() {
        return 30;
    }

    @Override
    public int getMilliSecondsIncrementWhenWaitingForWebSocketEndpointsToBeProperlyClosedBeforeKillingTheServer() {
        return 200;
    }

    @Override
    public String getHtmlFormEncoding() {
        return "UTF-8";
    }

    @Override
    public boolean isEnableLearningPushHandler() {
        return false;
    }

}
