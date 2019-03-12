package org.spincast.plugins.logbackutils;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.logbackutils.config.SpincastLogbackConfigurerConfig;
import org.spincast.plugins.logbackutils.config.SpincastLogbackConfigurerConfigDefault;

/**
 * Spincast Logback Utils plugin module.
 */
public class SpincastLogbackUtilsPluginModule extends SpincastGuiceModuleBase {

    public SpincastLogbackUtilsPluginModule() {
        super();
    }

    public SpincastLogbackUtilsPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                            Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {

        //==========================================
        // "asEagerSingleton" so the logger config
        // are applied as soon as possible.
        //==========================================
        bind(SpincastLogbackConfigurer.class).asEagerSingleton();

        bind(SpincastLogbackConfigurerConfig.class).to(getSpincastLogbackConfigurerConfigImpl());
    }

    protected Class<? extends SpincastLogbackConfigurerConfig> getSpincastLogbackConfigurerConfigImpl() {
        return SpincastLogbackConfigurerConfigDefault.class;
    }
}
