package org.spincast.core.websocket;

import org.spincast.core.exchange.IRequestContext;

/**
 * A controller for a WebSocket connection.
 */
public interface IWebsocketController<R extends IRequestContext<?>, W extends IWebsocketContext<?>> {

    /**
     * Called before the HTTP request is converted to a 
     * WebSocket connection.
     * <p>
     * Allows you to decide which endpoint and which peer id to use
     * for the WebSocket connection. You still have access to the
     * request parameters, the cookies, so you can make a decision
     * based on those.
     * </p>
     * @return the WebSocket configuration to used for this peer
     * or, if <code>null</code> is returned, the WebSocket 
     * connection won't be established.
     */
    public IWebsocketConnectionConfig onPeerPreConnect(R context);

    /**
     * This method is called when a new endpoint is created and its manager 
     * is ready. This occures when the controller returns a endpoint id to be
     * used for the first time. Save this manager and use it to send messages, 
     * to echo received messages from a peer to multiple peers, to close peers, 
     * or to close the endpoint itself.
     * <p>
     * IMPORTANT: To make sure you receive this manager <em>before</em> the 
     * connection with the very first peer is established, to prevent any 
     * concurrency issues, this method is actually called <em>synchronously</em> 
     * in the same thread handling the connection with that first peer. This 
     * means this method should <em>not</em> block! If you use this method to start 
     * a loop and send messages, for example, then the WebSocket connection with 
     * the first peer will fail...
     * </p>
     * <p>
     * To use this method as a starting point to send events to the peers, start 
     * <em>a new Thread</em> in it and return immediately.
     * <p>
     */
    public void onEndpointReady(IWebsocketEndpointManager endpointManager);

    /**
     * A peer is connected on the WebSocket endpoint.
     * 
     * You may want to wait for the first call to this method
     * before strating to send messages.
     */
    public void onPeerConnected(W context);

    /**
     * A Peer sent a String message.
     */
    public void onPeerMessage(W context, String message);

    /**
     * A peer sent a bytes[] message.
     */
    public void onPeerMessage(W context, byte[] message);

    /**
     * A peer closed the WebSocket connection.
     */
    public void onPeerClosed(W context);

    /**
     * The endpoint is now closed.
     */
    public void onEndpointClosed(String endpointId);

}
