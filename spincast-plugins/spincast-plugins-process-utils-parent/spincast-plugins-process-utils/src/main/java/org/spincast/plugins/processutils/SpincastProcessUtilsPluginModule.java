package org.spincast.plugins.processutils;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Scopes;

/**
 * Spincast Process Utils plugin module.
 */
public class SpincastProcessUtilsPluginModule extends SpincastGuiceModuleBase {

    public SpincastProcessUtilsPluginModule() {
        super();
    }

    public SpincastProcessUtilsPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                            Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {
        bind(SpincastProcessUtils.class).to(getSpincastProcessUtilsImpl()).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastProcessUtils> getSpincastProcessUtilsImpl() {
        return SpincastProcessUtilsDefault.class;
    }

}
