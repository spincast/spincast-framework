package org.spincast.plugins.session;

import java.util.Set;

import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.plugins.crons.SpincastCronJob;
import org.spincast.plugins.session.config.SpincastSessionConfig;
import org.spincast.plugins.session.config.SpincastSessionConfigDefault;
import org.spincast.plugins.session.config.SpincastSessionPluginCronJobProvider;
import org.spincast.plugins.session.config.SpincastSessionPluginCronJobProviderDefault;

import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;

/**
 * Spincast Session plugin module.
 */
public class SpincastSessionPluginModule extends SpincastGuiceModuleBase {

    @Override
    protected void configure() {

        //==========================================
        // An implementation for SpincastSessionManager
        // must been bound in the application
        //==========================================
        requireBinding(SpincastSessionRepository.class);

        bind(SpincastSessionConfig.class).to(getSpincastSessionConfigImplClass())
                                         .in(Scopes.SINGLETON);

        bind(SpincastSessionManager.class).to(getSpincastSessionManagerImplClass())
                                          .in(Scopes.SINGLETON);

        install(new FactoryModuleBuilder().implement(SpincastSession.class,
                                                     getSpincastUserSessionImplClass())
                                          .build(SpincastSessionFactory.class));

        bind(SpincastSessionFilter.class).to(getSpincastSessionFilterImplClass())
                                         .in(Scopes.SINGLETON);

        //==========================================
        // Binds cron jobs
        //==========================================
        bind(SpincastSessionPluginCronJobProvider.class).to(getSpincastSessionPluginCronJobProviderImplClass())
                                                        .in(Scopes.SINGLETON);
        Multibinder<Set<SpincastCronJob>> cronsSetsMultibinder =
                Multibinder.newSetBinder(binder(), Key.get(new TypeLiteral<Set<SpincastCronJob>>() {}));
        cronsSetsMultibinder.addBinding().toProvider(SpincastSessionPluginCronJobProvider.class);
    }

    protected Class<? extends SpincastSessionConfig> getSpincastSessionConfigImplClass() {
        return SpincastSessionConfigDefault.class;
    }

    protected Class<? extends SpincastSession> getSpincastUserSessionImplClass() {
        return SpincastSessionDefault.class;
    }

    protected Class<? extends SpincastSessionFilter> getSpincastSessionFilterImplClass() {
        return SpincastSessionFilterDefault.class;
    }

    protected Class<? extends SpincastSessionPluginCronJobProvider> getSpincastSessionPluginCronJobProviderImplClass() {
        return SpincastSessionPluginCronJobProviderDefault.class;
    }

    protected Class<? extends SpincastSessionManager> getSpincastSessionManagerImplClass() {
        return SpincastSessionManagerDefault.class;
    }

}
