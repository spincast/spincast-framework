package org.spincast.plugins.cssyuicompressor;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.cssyuicompressor.config.SpincastCssYuiCompressorConfig;
import org.spincast.plugins.cssyuicompressor.config.SpincastCssYuiCompressorConfigDefault;

import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.mitchellbosecke.pebble.extension.Extension;

/**
 * Spincast CSS YUI Compressor plugin module.
 */
public class SpincastCssYuiCompressorPluginModule extends SpincastGuiceModuleBase {

    public SpincastCssYuiCompressorPluginModule() {
        super();
    }

    public SpincastCssYuiCompressorPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                                Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {
        bind(SpincastCssYuiCompressorManager.class).to(getSpincastCssYuiCompressorManagerImpl()).in(Scopes.SINGLETON);
        bind(SpincastCssYuiCompressorConfig.class).to(getSpincastCssYuiCompressorConfigImpl()).in(Scopes.SINGLETON);

        bindPebbleExtension();
    }

    protected Class<? extends SpincastCssYuiCompressorManager> getSpincastCssYuiCompressorManagerImpl() {
        return SpincastCssYuiCompressorManagerDefault.class;
    }

    protected Class<? extends SpincastCssYuiCompressorConfig> getSpincastCssYuiCompressorConfigImpl() {
        return SpincastCssYuiCompressorConfigDefault.class;
    }

    protected void bindPebbleExtension() {
        Multibinder<Extension> pebbleExtensionsMultibinder = Multibinder.newSetBinder(binder(), Extension.class);
        pebbleExtensionsMultibinder.addBinding().to(SpincastCssYuiCompressorPebbleExtension.class).in(Scopes.SINGLETON);
    }

}
