package org.spincast.plugins.dateformatter;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import com.mitchellbosecke.pebble.extension.Extension;

/**
 * Spincast Date Formatter plugin module.
 */
public class SpincastDateFormatterPluginModule extends SpincastGuiceModuleBase {

    public SpincastDateFormatterPluginModule() {
        super();
    }

    public SpincastDateFormatterPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                             Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {

        //==========================================
        // Date Formatter Factory
        //==========================================
        bindDateFormatterFactory();

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

    protected void bindDateFormatterFactory() {
        install(new FactoryModuleBuilder().implement(DateFormatter.class,
                                                     getDateFormatterImplClass())
                                          .implement(RelativeDateFormatter.class,
                                                     getDateFormatterAgoImplClass())
                                          .build(DateFormatterFactory.class));
    }

    protected Class<? extends DateFormatter> getDateFormatterImplClass() {
        return DateFormatterDefault.class;
    }

    protected Class<? extends RelativeDateFormatter> getDateFormatterAgoImplClass() {
        return RelativeDateFormatterDefault.class;
    }

    protected void bindPebbleExtension() {

        bind(SpincastDateFormatterPebbleExtension.class).to(getPebbleExtensionImplClass()).in(Scopes.SINGLETON);

        Multibinder<Extension> pebbleExtensionsMultibinder = Multibinder.newSetBinder(binder(), Extension.class);
        pebbleExtensionsMultibinder.addBinding().to(SpincastDateFormatterPebbleExtension.class).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastDateFormatterPebbleExtension> getPebbleExtensionImplClass() {
        return SpincastDateFormatterPebbleExtensionDefault.class;
    }

}
