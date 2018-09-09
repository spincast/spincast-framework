package org.spincast.plugins.hotswap;

import org.hotswap.agent.config.PluginManager;
import org.hotswap.agent.config.PluginRegistry;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.server.ServerStartedListener;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.hotswap.classeswatcher.HotSwapClassesRedefinitionsWatcher;
import org.spincast.plugins.hotswap.classeswatcher.HotSwapClassesRedefinitionsWatcherDefault;
import org.spincast.plugins.hotswap.classeswatcher.HotSwapClassesRedefinitionsWatcherDummy;
import org.spincast.plugins.hotswap.fileswatcher.HotSwapFilesModificationsWatcher;
import org.spincast.plugins.hotswap.fileswatcher.HotSwapFilesModificationsWatcherDefault;

import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

/**
 * Spincast HotSwap plugin module.
 */
public class SpincastHotSwapPluginModule extends SpincastGuiceModuleBase {

    protected static boolean hotswapAgentUnavailableMessageOutputed = false;

    public SpincastHotSwapPluginModule() {
        super();
    }

    public SpincastHotSwapPluginModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                       Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        super(requestContextImplementationClass, websocketContextImplementationClass);
    }

    @Override
    protected void configure() {

        bind(HotSwapManager.class).to(getHotSwapManagerDefaultImpl()).in(Scopes.SINGLETON);
        bind(HotSwapFilesModificationsWatcher.class).to(getHotSwapFilesModificationsWatcherImpl()).in(Scopes.SINGLETON);
        Multibinder<ServerStartedListener> serverStartedListenersMultibinder =
                Multibinder.newSetBinder(binder(), ServerStartedListener.class);
        serverStartedListenersMultibinder.addBinding().to(HotSwapFilesModificationsWatcher.class).in(Scopes.SINGLETON);

        if (isHotswapAgentAvailable()) {
            bind(HotSwapClassesRedefinitionsWatcher.class).to(getHotSwapClassesRedefinitionWatcherImpl()).in(Scopes.SINGLETON);
            bindHotSwapClassesRedefinitionWatcher();
        } else {
            if (!hotswapAgentUnavailableMessageOutputed) {
                hotswapAgentUnavailableMessageOutputed = true;
                System.out.println("Hotswap Agent is not available. " +
                                   "We won't register any class redefinition listeners...");
            }

            bind(HotSwapClassesRedefinitionsWatcher.class).to(getHotSwapClassesRedefinitionsWatcherDummyImpl())
                                                          .in(Scopes.SINGLETON);
        }
    }

    protected Class<? extends HotSwapClassesRedefinitionsWatcher> getHotSwapClassesRedefinitionsWatcherDummyImpl() {
        return HotSwapClassesRedefinitionsWatcherDummy.class;
    }

    protected boolean isHotswapAgentAvailable() {
        try {
            Class.forName("org.hotswap.agent.annotation.Plugin");
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    protected Class<? extends HotSwapManager> getHotSwapManagerDefaultImpl() {
        return HotSwapManagerDefault.class;
    }

    protected Class<? extends HotSwapFilesModificationsWatcher> getHotSwapFilesModificationsWatcherImpl() {
        return HotSwapFilesModificationsWatcherDefault.class;
    }

    protected Class<? extends HotSwapClassesRedefinitionsWatcher> getHotSwapClassesRedefinitionWatcherImpl() {
        return HotSwapClassesRedefinitionsWatcherDefault.class;
    }

    protected void bindHotSwapClassesRedefinitionWatcher() {

        try {
            ClassLoader classLoader = getClassloaderToUseToBindHotSwapClassesRedefinitionWatcher();
            PluginRegistry pluginRegistry = PluginManager.getInstance().getPluginRegistry();
            pluginRegistry.scanPlugins(classLoader, HotSwapClassesRedefinitionsWatcherDefault.class.getPackage().getName());
            pluginRegistry.initializePlugin(HotSwapClassesRedefinitionsWatcherDefault.class.getName(), classLoader);

            HotSwapClassesRedefinitionsWatcherDefault plugin =
                    pluginRegistry.getPlugin(HotSwapClassesRedefinitionsWatcherDefault.class, classLoader);
            bind(HotSwapClassesRedefinitionsWatcherDefault.class).toInstance(plugin);

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected ClassLoader getClassloaderToUseToBindHotSwapClassesRedefinitionWatcher() {
        return this.getClass().getClassLoader();
    }


}
