package org.spincast.plugins.httpclient.websocket.builders;

import org.spincast.plugins.httpclient.IHttpRequestBuilder;
import org.spincast.plugins.httpclient.IHttpResponse;
import org.spincast.plugins.httpclient.websocket.IWebsocketClientHandler;
import org.spincast.plugins.httpclient.websocket.IWebsocketClientWriter;

/**
 * Builder to create a WebSocket requesté
 */
public interface IWebsocketRequestBuilder extends IHttpRequestBuilder<IWebsocketRequestBuilder> {

    /**
     * You can use this to configure the pings that are automatically sent 
     * to the Websocket endpoint every X seconds.
     * <code>IWebsocketClientHandler#onConnectionClosed()</code>
     * will be called if the connection is closed.
     * <p>
     * Use a value <code>&lt;= 0</code> to disable the pings.
     * </p>
     * <p>
     * The automatic pings and their default interval are also configurable using:
     * <code>ISpincastHttpClientWithWebsocketConfig#isWebsocketAutomaticPingEnabled()</code><br>
     * and<br>
     * <code>ISpincastHttpClientWithWebsocketConfig#getWebsocketAutomaticPingIntervalSeconds()</code>
     * </p>
     * <p>
     * Pings are enabled by default.
     * </p>
     * 
     * @param seconds the interval in seconds or &lt;= 0 to disable the pings.
     */
    public IWebsocketRequestBuilder ping(int seconds);

    /**
     * Sends the request and establish the WebSocket connection.
     * 
     * @param handler The handler that is responsible to
     * handle the various WebSocket events.
     * 
     * @return a writer to send WebSocket messages to the connected endpoint.
     */
    public IWebsocketClientWriter connect(IWebsocketClientHandler handler);

    /**
     * Sends the request and gets the <code>HTTP</code> response.
     * 
     * Does <i>not</i> make the actual upgrade to a
     * WebSocket connection! Use the <code>connect(...)</code> 
     * method if you want the actual WebSocket connection
     * to be made.
     * <p>
     * This version is useful to debug the intermediate
     * <code>HTTP</code> upgrade response made from the server 
     * before the actual WebSocket connection is established.
     * </p>
     */
    @Override
    public IHttpResponse send();

}
