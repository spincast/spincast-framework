package org.spincast.plugins.timezoneresolver;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.timezone.TimeZoneResolver;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.mitchellbosecke.pebble.extension.Extension;

/**
 * Spincast TimeZone Resolver plugin module.
 */
public class SpincastTimeZoneResolverPluginModule extends SpincastGuiceModuleBase {

    public SpincastTimeZoneResolverPluginModule() {
        super();
    }

    public SpincastTimeZoneResolverPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                                Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {

        //==========================================
        // TimeZone Resolver
        //==========================================
        bindTimeZoneResolver();

        //==========================================
        // Pebble is set as a *provided* dependency for this
        // plugin. If it is not available, we do not bind
        // the extension...
        //==========================================
        if (isPebbleAvailable()) {
            bindPebbleExtension();
        }
    }

    protected boolean isPebbleAvailable() {
        try {
            Class.forName("com.mitchellbosecke.pebble.extension.Extension");
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    protected void bindTimeZoneResolver() {
        bind(TimeZoneResolver.class).to(TimeZoneResolverDefault.class).in(Scopes.SINGLETON);
    }

    protected void bindPebbleExtension() {

        bind(SpincastTimeZonePebbleExtension.class).to(getPebbleExtensionImplClass()).in(Scopes.SINGLETON);

        Multibinder<Extension> pebbleExtensionsMultibinder = Multibinder.newSetBinder(binder(), Extension.class);
        pebbleExtensionsMultibinder.addBinding().to(SpincastTimeZonePebbleExtension.class).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastTimeZonePebbleExtension> getPebbleExtensionImplClass() {
        return SpincastTimeZonePebbleExtensionDefault.class;
    }

}
