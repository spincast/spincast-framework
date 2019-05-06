package org.spincast.plugins.cssyuicompressor;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Scopes;

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
    }

    protected Class<? extends SpincastCssYuiCompressorManager> getSpincastCssYuiCompressorManagerImpl() {
        return SpincastCssYuiCompressorManagerDefault.class;
    }

}
