package org.spincast.tests.websocket;

import org.junit.Ignore;
import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.tests.SpincastDefaultTestingModule;

import com.google.inject.Module;

/**
 * Base class for WebSocket tests without an existing
 * application and using the default request context and
 * the default WebSocket context.
 */
@Ignore
public class SpincastDefaultWebsocketNoAppIntegrationTestBase extends
                                                              SpincastWebsocketNoAppIntegrationTestBase<DefaultRequestContext, DefaultWebsocketContext> {

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule(getMainArgsToUse());
    }

    @Override
    protected String[] getMainArgsToUse() {
        return null;
    }
}
