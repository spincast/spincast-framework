package org.spincast.core.websocket;

import java.util.Locale;

import org.spincast.core.json.JsonManager;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.xml.XmlManager;

import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * The base interface for a WebSocket context.
 * 
 * A WebSocket context is the object passed to a WebSocket
 * controller when an event for a particular peer occured.
 * For example when a message is sent from a peer, or if
 * the peer closed the WebSocket connection.
 */
public interface WebsocketContext<W extends WebsocketContext<?>> {

    /**
     * The id of the endpoint
     */
    public String getEndpointId();

    /**
     * The id of the peer.
     */
    public String getPeerId();

    /**
     * Sends a String message to the
     * current peer.
     * 
     * Same as WebsocketEndpointManager#sendMessage(getPeerId(), message)
     */
    public void sendMessageToCurrentPeer(String message);

    /**
     * Sends a byte array message to the
     * current peer.
     * 
     * Same as WebsocketEndpointManager#sendMessage(getPeerId(), message)
     */
    public void sendMessageToCurrentPeer(byte[] message);

    /**
     * Closes the connection with the 
     * current peer.
     * 
     * Same as WebsocketEndpointManager#closePeer(getPeerId())
     */
    public void closeConnectionWithCurrentPeer();

    /**
     * Easy access to the <code>JsonManager</code>,
     * Json related methods.
     */
    public JsonManager json();

    /**
     * Easy access to the <code>XmlManager</code>,
     * XML related methods.
     */
    public XmlManager xml();

    /**
     * Easy access to the <code>TemplatingEngine</code>,
     * templating related methods.
     */
    public TemplatingEngine templating();

    /**
     * Easy access to the Guice context.
     */
    public Injector guice();

    /**
     * Shortcut to get an instance from Guice. Will
     * also cache the instance (as long as it is
     * request scoped or a singleton).
     */
    public <T> T get(Class<T> clazz);

    /**
     * Shortcut to get an instance from Guice. Will
     * also cache the instance (as long as it is
     * request scoped or a singleton)
     */
    public <T> T get(Key<T> key);

    /**
     * The best Locale to use, as resolved by 
     * the <code>LocaleResolver</code> during the
     * initial HTTP request.
     */
    public Locale getLocaleToUse();

}
