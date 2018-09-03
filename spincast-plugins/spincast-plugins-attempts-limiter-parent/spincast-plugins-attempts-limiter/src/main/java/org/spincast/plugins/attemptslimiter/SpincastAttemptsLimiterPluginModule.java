package org.spincast.plugins.attemptslimiter;

import java.util.Set;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.attemptslimiter.config.SpincastAttemptsLimiterPluginConfig;
import org.spincast.plugins.attemptslimiter.config.SpincastAttemptsLimiterPluginConfigDefault;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTask;

import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;

/**
 * Spincast Attempts Limiter plugin module.
 */
public class SpincastAttemptsLimiterPluginModule extends SpincastGuiceModuleBase {

    public SpincastAttemptsLimiterPluginModule() {
        super();
    }

    public SpincastAttemptsLimiterPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                               Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {

        bind(AttemptsManager.class).to(getAttemptsManagerImpl()).in(Scopes.SINGLETON);

        bind(SpincastAttemptsLimiterPluginConfig.class).to(getSpincastAttemptsLimiterPluginConfigImpl()).in(Scopes.SINGLETON);

        install(new FactoryModuleBuilder().implement(Attempt.class,
                                                     getAttemptImplClass())
                                          .implement(AttemptRule.class,
                                                     getAttemptRuleClass())
                                          .build(AttemptFactory.class));

        //==========================================
        // Binds scheduled tasks  provider
        //==========================================
        bind(SpincastAttemptsLimiterPluginScheduledTaskProvider.class).to(getSpincastAttemptsLimiterPluginScheduledTaskProviderImplClass())
                                                                  .in(Scopes.SINGLETON);
        Multibinder<Set<SpincastScheduledTask>> scheduledTaskSetsMultibinder =
                Multibinder.newSetBinder(binder(), Key.get(new TypeLiteral<Set<SpincastScheduledTask>>() {}));
        scheduledTaskSetsMultibinder.addBinding().toProvider(SpincastAttemptsLimiterPluginScheduledTaskProvider.class);
    }

    protected Class<? extends Attempt> getAttemptImplClass() {
        return AttemptDefault.class;
    }

    protected Class<? extends AttemptRule> getAttemptRuleClass() {
        return AttemptRuleDefault.class;
    }

    protected Class<? extends SpincastAttemptsLimiterPluginConfig> getSpincastAttemptsLimiterPluginConfigImpl() {
        return SpincastAttemptsLimiterPluginConfigDefault.class;
    }

    protected Class<? extends AttemptsManager> getAttemptsManagerImpl() {
        return AttemptsManagerDefault.class;
    }

    protected Class<? extends SpincastAttemptsLimiterPluginScheduledTaskProvider> getSpincastAttemptsLimiterPluginScheduledTaskProviderImplClass() {
        return SpincastAttemptsLimiterPluginScheduledTaskProviderDefault.class;
    }

}
