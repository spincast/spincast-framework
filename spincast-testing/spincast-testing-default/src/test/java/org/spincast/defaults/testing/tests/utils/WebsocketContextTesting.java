package org.spincast.defaults.testing.tests.utils;

import org.spincast.core.websocket.WebsocketContext;

/**
 * Custom WebSocket Context type
 */
public interface WebsocketContextTesting extends WebsocketContext<WebsocketContextTesting> {

    public String test2();
}
