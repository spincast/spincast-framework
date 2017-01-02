package org.spincast.core.guice;

import java.util.Set;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Suggested base class for a Spincast plugin.
 */
public abstract class SpincastPluginBase implements SpincastPlugin {

    private Class<? extends RequestContext<?>> requestContextImplementationClass;
    private Class<? extends WebsocketContext<?>> websocketContextImplementationClass;

    @Override
    public void setRequestContextImplementationClass(Class<? extends RequestContext<?>> requestContextImplementationClass) {
        this.requestContextImplementationClass = requestContextImplementationClass;
    }

    @Override
    public void setWebsocketContextImplementationClass(Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        this.websocketContextImplementationClass = websocketContextImplementationClass;
    }

    protected Class<? extends RequestContext<?>> getRequestContextImplementationClass() {
        return this.requestContextImplementationClass;
    }

    protected Class<? extends WebsocketContext<?>> getWebsocketContextImplementationClass() {
        return this.websocketContextImplementationClass;
    }

    @Override
    public Set<String> getPluginsToDisable() {
        return null;
    }

    @Override
    public void createdGuiceInjector(Injector injector) {
        // not required by default
    }

    protected void setContextTypes(Module module) {

        if(module instanceof SpincastContextTypesInterested) {
            ((SpincastContextTypesInterested)module).setRequestContextImplementationClass(getRequestContextImplementationClass());
            ((SpincastContextTypesInterested)module).setWebsocketContextImplementationClass(getWebsocketContextImplementationClass());
        }
    }

}
