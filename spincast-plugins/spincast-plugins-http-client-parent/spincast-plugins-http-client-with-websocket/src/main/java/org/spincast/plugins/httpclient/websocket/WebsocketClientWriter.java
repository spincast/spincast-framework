package org.spincast.plugins.httpclient.websocket;

/**
 * A Websocket client writer
 */
public interface WebsocketClientWriter {

    /**
     * Sends a text message to the endpoint.
     */
    public void sendMessage(String message);

    /**
     * Sends a binary message to the endpoint
     */
    public void sendMessage(byte[] message);

    /**
     * Closes the WebSocket connection.
     */
    public void closeConnection();

}
