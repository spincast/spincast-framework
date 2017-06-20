package org.spincast.defaults.testing;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.testing.core.IntegrationTestNoAppBase;

import com.google.inject.Injector;

public abstract class IntegrationTestNoAppDefaultContextsBase extends
                                                              IntegrationTestNoAppBase<DefaultRequestContext, DefaultWebsocketContext> {

    /**
     * To override if required.
     */
    @Override
    protected Injector createInjector() {

        //==========================================
        // No need to bind the current class.
        //==========================================
        return Spincast.configure()
                       .bindCurrentClass(false)
                       .init(new String[]{});
    }
}
