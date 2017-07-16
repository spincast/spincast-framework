package org.spincast.defaults.testing;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.testing.core.SpincastTestBase;

import com.google.inject.Injector;

public abstract class NoAppCustomContextTypesTestingBase<R extends RequestContext<?>, W extends WebsocketContext<?>>
                                                        extends SpincastTestBase {

    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .bindCurrentClass(false)
                       .requestContextImplementationClass(getRequestContextImplementationClass())
                       .websocketContextImplementationClass(getWebsocketContextImplementationClass())
                       .init(getMainArgs());
    }

    protected String[] getMainArgs() {
        return null;
    }

    protected abstract Class<? extends RequestContext<?>> getRequestContextImplementationClass();

    protected abstract Class<? extends WebsocketContext<?>> getWebsocketContextImplementationClass();

}
