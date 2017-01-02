package org.spincast.core.guice;

import java.util.Set;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Injector;
import com.google.inject.Module;

public interface SpincastPlugin extends SpincastContextTypesInterested {

    /**
     * The id of the plugin.
     */
    public String getId();

    /**
     * Applies the plugin.
     * <p>
     * The plugin can add bindings to the current Guice module,
     * can modify it and can inspect it in order to decide what to
     * bind or not.
     * 
     * @return an ajusted Guice module.
     */
    public Module apply(Module currentModule);

    /**
     * The implementation class to use for RequestContext.
     */
    @Override
    public void setRequestContextImplementationClass(Class<? extends RequestContext<?>> requestContextImplementationClass);

    /**
     * The implementation class to use for WebsocletContext.
     */
    @Override
    public void setWebsocketContextImplementationClass(Class<? extends WebsocketContext<?>> websocketContextImplementationClass);

    /**
     * If required, the ids of plugins that shouldn't
     * be installed.
     * <p>
     * This plugin is repsonsible to bind any components
     * that won't be bound because it mark some plugins
     * as to be ignored.
     */
    public Set<String> getPluginsToDisable();

    /**
     * Once all the plugins have been applied,
     * this method is called with the resulting
     * Guice injector.
     */
    public void createdGuiceInjector(Injector injector);

}
