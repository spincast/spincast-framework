package org.spincast.defaults.testing;

import org.spincast.core.exchange.DefaultRequestContextDefault;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.GuiceTweaker;
import org.spincast.core.websocket.DefaultWebsocketContextDefault;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.defaults.bootstrapping.SpincastBootstrapper;
import org.spincast.testing.core.SpincastTestBase;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

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

        if(requestContextImplementationClass == null) {
            requestContextImplementationClass = DefaultRequestContextDefault.class;
        }
        if(websocketContextImplementationClass == null) {
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

        SpincastBootstrapper builder = Spincast.configure()
                                             .bindCurrentClass(false)
                                             .requestContextImplementationClass(getRequestContextImplementationClass())
                                             .websocketContextImplementationClass(getWebsocketContextImplementationClass());
        if(getExtraOverridingModule() != null) {
            builder.module(getExtraOverridingModule());
        }

        return builder.init();
    }

    /**
     * Since we do not call "App.main(...)" in test
     * class in this branch, we don't need to bind
     * the current class to the Guice context : the
     * Server will be started explicitly and the
     * @Test methods called explicitly.
     */
    @Override
    protected GuiceTweaker createGuiceTweaker() {

        GuiceTweaker guiceTweaker = super.createGuiceTweaker();
        if(guiceTweaker == null) {
            guiceTweaker = new GuiceTweaker();
        }
        guiceTweaker.bindCurrentClassByDefault(false);

        return guiceTweaker;
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
