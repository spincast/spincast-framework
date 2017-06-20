package org.spincast.defaults.testing;

import org.spincast.core.exchange.DefaultRequestContextDefault;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.DefaultWebsocketContextDefault;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.testing.core.SpincastTestBase;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Base for non integration test classes that
 * simply need the default implementations of the
 * required components.
 */
public class UnitTestBase extends SpincastTestBase {

    private final Class<? extends RequestContext<?>> requestContextImplementationClass;
    private final Class<? extends WebsocketContext<?>> websocketContextImplementationClass;

    @Inject
    public UnitTestBase() {
        this(DefaultRequestContextDefault.class, DefaultWebsocketContextDefault.class);
    }

    public UnitTestBase(Class<? extends RequestContext<?>> requestContextImplementationClass,
                        Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {

        if (requestContextImplementationClass == null) {
            requestContextImplementationClass = DefaultRequestContextDefault.class;
        }
        if (websocketContextImplementationClass == null) {
            websocketContextImplementationClass = DefaultWebsocketContextDefault.class;
        }

        this.requestContextImplementationClass = requestContextImplementationClass;
        this.websocketContextImplementationClass = websocketContextImplementationClass;
    }

    protected Class<? extends RequestContext<?>> getRequestContextImplementationClass() {
        return this.requestContextImplementationClass;
    }

    protected Class<? extends WebsocketContext<?>> getWebsocketContextImplementationClass() {
        return this.websocketContextImplementationClass;
    }

    @Override
    protected Injector createInjector() {

        return Spincast.configure()
                       .bindCurrentClass(false)
                       .requestContextImplementationClass(getRequestContextImplementationClass())
                       .websocketContextImplementationClass(getWebsocketContextImplementationClass())
                       .init(new String[]{});
    }

}
