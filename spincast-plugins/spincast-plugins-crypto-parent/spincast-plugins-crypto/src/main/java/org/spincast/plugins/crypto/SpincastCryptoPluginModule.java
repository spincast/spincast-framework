package org.spincast.plugins.crypto;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.crypto.config.SpincastCryptoConfig;
import org.spincast.plugins.crypto.config.SpincastCryptoConfigDefault;

import com.google.inject.Scopes;

/**
 * Spincast Crypto plugin module.
 */
public class SpincastCryptoPluginModule extends SpincastGuiceModuleBase {

    public SpincastCryptoPluginModule() {
        super();
    }

    public SpincastCryptoPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                      Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {
        bind(SpincastCryptoUtils.class).to(getCryptoUtilsImplClass()).in(Scopes.SINGLETON);
        bind(SpincastCryptoConfig.class).to(getSpincastCryptoConfigImplClass()).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastCryptoUtils> getCryptoUtilsImplClass() {
        return SpincastCryptoUtilsDefault.class;
    }

    protected Class<? extends SpincastCryptoConfig> getSpincastCryptoConfigImplClass() {
        return SpincastCryptoConfigDefault.class;
    }

}
