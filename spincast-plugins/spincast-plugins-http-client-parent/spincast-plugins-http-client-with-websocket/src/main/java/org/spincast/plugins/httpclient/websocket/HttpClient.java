package org.spincast.plugins.httpclient.websocket;

import org.spincast.plugins.httpclient.websocket.builders.WebsocketRequestBuilder;

public interface HttpClient extends org.spincast.plugins.httpclient.HttpClient {

    /**
     * Starts a builder for a websocket request.
     */
    public WebsocketRequestBuilder websocket(String url);
}
