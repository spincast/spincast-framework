package org.spincast.quickstart.exchange;

import org.spincast.core.websocket.WebsocketContext;

/**
 * Custom type which allows our application to
 * extend the default Websocket Context.
 * 
 * Spincast will pass an instance of this class to all 
 * Websocket events handlers, when a new event occures.
 */
public interface AppWebsocketContext extends WebsocketContext<AppWebsocketContext> {

    /**
     * A custom method example.
     * This will simply send a "Hello!" message to the
     * current peer.
     */
    public void helloCurrentPeer();
}
