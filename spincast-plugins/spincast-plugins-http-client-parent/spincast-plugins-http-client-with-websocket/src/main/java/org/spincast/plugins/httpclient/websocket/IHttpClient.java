package org.spincast.plugins.httpclient.websocket;

import org.spincast.plugins.httpclient.websocket.builders.IWebsocketRequestBuilder;

public interface IHttpClient extends org.spincast.plugins.httpclient.IHttpClient {

    /**
     * Starts a builder for a websocket request.
     */
    public IWebsocketRequestBuilder websocket(String url);
}
