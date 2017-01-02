package org.spincast.defaults.testing;

import org.spincast.core.exchange.DefaultRequestContext;
import org.spincast.core.websocket.DefaultWebsocketContext;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.bootstrapping.SpincastBootstrapper;
import org.spincast.testing.core.IntegrationTestNoAppBase;

import com.google.inject.Injector;
import com.google.inject.Module;

public abstract class IntegrationTestNoAppDefaultContextsBase extends
                                                              IntegrationTestNoAppBase<DefaultRequestContext, DefaultWebsocketContext> {

    /**
     * To override if required.
     */
    @Override
    protected Injector createInjector() {

        SpincastBootstrapper builder = Spincast.configure()
                                             .bindCurrentClass(false);
        if(getExtraOverridingModule() != null) {
            builder.module(getExtraOverridingModule());
        }

        return builder.init();
    }

    /**
     * If all the test class wants to tweak from a default
     * <code>Spincast.configure()</code> generated Injector is 
     * to add a module, one can be specified by overriding this
     * method.
     */
    protected Module getExtraOverridingModule() {
        return null;
    }
}
