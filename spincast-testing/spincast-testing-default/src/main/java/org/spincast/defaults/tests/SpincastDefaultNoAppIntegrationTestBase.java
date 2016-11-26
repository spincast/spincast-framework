package org.spincast.defaults.tests;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.testing.core.SpincastNoAppIntegrationTestBase;

import com.google.inject.Module;

/**
 * Base class for integration tests with no application
 * associated and that use the default request context, the
 * default WebSocket context and 
 * <code>SpincastDefaultTestingModule</code> as the
 * main Guice module to bind.
 */
public class SpincastDefaultNoAppIntegrationTestBase extends
                                                     SpincastNoAppIntegrationTestBase<DefaultRequestContext, DefaultWebsocketContext> {

    @Override
    public Module getTestingModule() {
        return new SpincastDefaultTestingModule(getMainArgsToUse());
    }

    @Override
    protected String[] getMainArgsToUse() {
        return null;
    }

}
