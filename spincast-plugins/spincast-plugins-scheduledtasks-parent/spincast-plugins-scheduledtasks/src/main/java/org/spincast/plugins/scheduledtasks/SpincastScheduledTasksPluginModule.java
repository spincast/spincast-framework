package org.spincast.plugins.scheduledtasks;

import java.util.Set;

import org.quartz.Scheduler;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.server.ServerStartedListener;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.scheduledtasks.config.SpincastScheduledTasksPluginConfig;
import org.spincast.plugins.scheduledtasks.config.SpincastScheduledTasksPluginConfigDefault;

import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

/**
 * Spincast Scheduled Tasks plugin module.
 */
public class SpincastScheduledTasksPluginModule extends SpincastGuiceModuleBase {

    public SpincastScheduledTasksPluginModule() {
        super();
    }

    public SpincastScheduledTasksPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                              Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {

        bind(SpincastScheduledTasksPluginConfig.class).to(getSpincastScheduledTasksPluginConfigrImplClass()).in(Scopes.SINGLETON);

        //==========================================
        // Default Scheduled Tasks multibinders
        //==========================================
        @SuppressWarnings("unused")
        Multibinder<SpincastScheduledTask> scheduledTasksMultibinder =
                Multibinder.newSetBinder(binder(), SpincastScheduledTask.class);
        @SuppressWarnings("unused")
        Multibinder<Set<SpincastScheduledTask>> scheduledTaskSetsMultibinder =
                Multibinder.newSetBinder(binder(), Key.get(new TypeLiteral<Set<SpincastScheduledTask>>() {}));

        bind(SpincastScheduledTaskFactory.class).to(getSpincastScheduledTaskFactoryImplClass()).in(Scopes.SINGLETON);

        bind(SpincastTaskSchedulerProvider.class).to(getSpincastTaskSchedulerProviderImplClass()).in(Scopes.SINGLETON);
        bind(Scheduler.class).toProvider(SpincastTaskSchedulerProvider.class).in(Scopes.SINGLETON);

        bind(SpincastScheduledTaskRegister.class).to(getSpincastScheduledTaskRegisterImplClass()).asEagerSingleton();

        //==========================================
        // We register the SpincastScheduledTaskRegister as
        // a ServerStartedListener so it is informed
        // once the server is started succesfully.
        //==========================================
        Multibinder<ServerStartedListener> serverStartedListenerMultibinder =
                Multibinder.newSetBinder(binder(), ServerStartedListener.class);
        serverStartedListenerMultibinder.addBinding().to(SpincastScheduledTaskRegister.class).in(Scopes.SINGLETON);
    }

    protected Class<? extends SpincastScheduledTasksPluginConfig> getSpincastScheduledTasksPluginConfigrImplClass() {
        return SpincastScheduledTasksPluginConfigDefault.class;
    }

    protected Class<? extends SpincastScheduledTaskFactory> getSpincastScheduledTaskFactoryImplClass() {
        return SpincastScheduledTaskFactoryDefault.class;
    }

    protected Class<? extends SpincastTaskSchedulerProvider> getSpincastTaskSchedulerProviderImplClass() {
        return SpincastTaskSchedulerProviderDefault.class;
    }

    protected Class<? extends SpincastScheduledTaskRegister> getSpincastScheduledTaskRegisterImplClass() {
        return SpincastScheduledTaskRegistrerDefault.class;
    }

}
