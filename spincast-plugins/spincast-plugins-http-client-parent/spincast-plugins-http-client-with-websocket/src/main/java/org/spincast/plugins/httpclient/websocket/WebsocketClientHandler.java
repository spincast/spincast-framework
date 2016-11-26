package org.spincast.plugins.httpclient.websocket;

/**
 * A Websocket client reader
 */
public interface WebsocketClientHandler {

    /**
     * Called when if the endpoint closes the connection.
     */
    public void onConnectionClosed(int code, String reason);

    /**
     * The endpoint sent a String message.
     */
    public void onEndpointMessage(String message);

    /**
     * The endpoint sent a bytes[] message.
     */
    public void onEndpointMessage(byte[] message);

}
