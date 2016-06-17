package org.spincast.plugins.httpclient.websocket;

/**
 * Spincast Websocket client writer
 */
public interface ISpincastWebsocketClientWriter extends IWebsocketClientWriter {

    /**
     * Sends a ping
     */
    public void sendPing();

}
