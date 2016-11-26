package org.spincast.plugins.httpclient.websocket;

/**
 * Spincast Websocket client writer
 */
public interface SpincastWebsocketClientWriter extends WebsocketClientWriter {

    /**
     * Sends a ping
     */
    public void sendPing();

}
