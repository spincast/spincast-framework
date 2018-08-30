package org.spincast.plugins.crons;

import java.util.Set;

import org.quartz.Scheduler;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.server.ServerStartedListener;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.crons.config.SpincastCronsConfig;
import org.spincast.plugins.crons.config.SpincastCronsConfigDefault;

import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

/**
 * Spincast Crons plugin module.
 */
public class SpincastCronsPluginModule extends SpincastGuiceModuleBase {

    public SpincastCronsPluginModule() {
        super();
    }

    public SpincastCronsPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                     Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {

        bind(SpincastCronsConfig.class).to(getSpincastCronsConfigrImplClass()).in(Scopes.SINGLETON);

        //==========================================
        // Default cron jobs multibinders
        //==========================================
        @SuppressWarnings("unused")
        Multibinder<SpincastCronJob> cronsMultibinder = Multibinder.newSetBinder(binder(), SpincastCronJob.class);
        @SuppressWarnings("unused")
        Multibinder<Set<SpincastCronJob>> cronsSetsMultibinder =
                Multibinder.newSetBinder(binder(), Key.get(new TypeLiteral<Set<SpincastCronJob>>() {}));

        bind(SpincastCronJobFactory.class).to(getSpincastCronJobFactoryImplClass()).in(Scopes.SINGLETON);

        bind(SpincastCronsSchedulerProvider.class).to(getSpincastSchedulerProviderImplClass()).in(Scopes.SINGLETON);
        bind(Scheduler.class).toProvider(SpincastCronsSchedulerProvider.class).in(Scopes.SINGLETON);

        bind(SpincastCronJobRegister.class).to(getSpincastSpincastCronRegistrarImplClass()).asEagerSingleton();

        //==========================================
        // We register the SpincastCronJobRegister as
        // a ServerStartedListener so it is informed
        // once the server is started succesfully.
        //==========================================
        Multibinder<ServerStartedListener> serverStartedListenerMultibinder =
                Multibinder.newSetBinder(binder(), ServerStartedListener.class);
        serverStartedListenerMultibinder.addBinding().to(SpincastCronJobRegister.class).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastCronsConfig> getSpincastCronsConfigrImplClass() {
        return SpincastCronsConfigDefault.class;
    }

    protected Class<? extends SpincastCronJobFactory> getSpincastCronJobFactoryImplClass() {
        return SpincastCronJobFactoryDefault.class;
    }

    protected Class<? extends SpincastCronsSchedulerProvider> getSpincastSchedulerProviderImplClass() {
        return SpincastCronsSchedulerProviderDefault.class;
    }

    protected Class<? extends SpincastCronJobRegister> getSpincastSpincastCronRegistrarImplClass() {
        return SpincastCronJobRegistrerDefault.class;
    }

}
