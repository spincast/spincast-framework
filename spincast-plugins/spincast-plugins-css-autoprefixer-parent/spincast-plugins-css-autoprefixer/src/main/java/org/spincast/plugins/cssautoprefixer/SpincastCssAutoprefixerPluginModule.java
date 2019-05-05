package org.spincast.plugins.cssautoprefixer;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.cssautoprefixer.config.SpincastCssAutoprefixerConfig;
import org.spincast.plugins.cssautoprefixer.config.SpincastCssAutoprefixerConfigDefault;

import com.google.inject.Scopes;

/**
 * Spincast CSS Autoprefixer plugin module.
 */
public class SpincastCssAutoprefixerPluginModule extends SpincastGuiceModuleBase {

    public SpincastCssAutoprefixerPluginModule() {
        super();
    }

    public SpincastCssAutoprefixerPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                               Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {
        bind(SpincastCssAutoprefixerManager.class).to(getSpincastCssAutoprefixerManagerImpl()).in(Scopes.SINGLETON);
        bind(SpincastCssAutoprefixerConfig.class).to(getCssAutoprefixerConfigImpl()).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastCssAutoprefixerManager> getSpincastCssAutoprefixerManagerImpl() {
        return SpincastCssAutoprefixerManagerDefault.class;
    }

    protected Class<? extends SpincastCssAutoprefixerConfig> getCssAutoprefixerConfigImpl() {
        return SpincastCssAutoprefixerConfigDefault.class;
    }
}
