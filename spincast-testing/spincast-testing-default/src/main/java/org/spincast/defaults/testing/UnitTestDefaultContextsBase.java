package org.spincast.defaults.testing;

import org.spincast.core.exchange.DefaultRequestContextDefault;
import org.spincast.core.websocket.DefaultWebsocketContextDefault;

/**
 * Base for non integration test classes that
 * simply need the default implementations of the
 * required components and use the default
 * Request context type and default Websocket context 
 * type.
 */
public class UnitTestDefaultContextsBase extends UnitTestBase {

    public UnitTestDefaultContextsBase() {
        super(DefaultRequestContextDefault.class, DefaultWebsocketContextDefault.class);
    }
}
