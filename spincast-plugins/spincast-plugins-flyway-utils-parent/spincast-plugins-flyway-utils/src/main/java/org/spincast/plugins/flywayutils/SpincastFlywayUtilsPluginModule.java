package org.spincast.plugins.flywayutils;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Spincast Flyway Utils plugin module.
 */
public class SpincastFlywayUtilsPluginModule extends SpincastGuiceModuleBase {

    public SpincastFlywayUtilsPluginModule() {
        super();
    }

    public SpincastFlywayUtilsPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                           Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {

        install(new FactoryModuleBuilder().implement(SpincastFlywayMigrationContext.class,
                                                     getSpincastFlywayMigraterImpl())
                                          .build(SpincastFlywayFactory.class));
    }

    protected Class<? extends SpincastFlywayMigrationContext> getSpincastFlywayMigraterImpl() {
        return SpincastFlywayMigrationContextDefault.class;
    }

}
