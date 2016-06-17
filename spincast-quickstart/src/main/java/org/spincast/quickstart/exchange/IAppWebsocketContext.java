package org.spincast.quickstart.exchange;

import org.spincast.core.websocket.IWebsocketContext;

/**
 * Custom type which allows our application to
 * extend the default Websocket context and add custom methods
 * and add-ons provided by plugins.
 * 
 * Spincast will pass an instance of this class to all 
 * Websocket events handlers, when a new event occures.
 */
public interface IAppWebsocketContext extends IWebsocketContext<IAppWebsocketContext> {

    /**
     * A custom method example.
     * This will simply send a "Hello!" message to the
     * current peer.
     */
    public void helloCurrentPeer();
}
