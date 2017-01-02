package org.spincast.core.guice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Spincast plugin made available through a ThreadLocal.
 * <p>
 * This allows the tweaking of the Guice context of an
 * application without touching its bootstrapping code.
 * <p>
 * The first use case for this is to be able to mock some
 * parts of an application in order to test it.
 * <p>
 * It is the responsibility of the code creating the
 * GuiceTweaker to make sure the ThreadLocal variable is removed.
 */
public class GuiceTweaker implements SpincastPlugin {

    public static final ThreadLocal<GuiceTweaker> threadLocal =
            new ThreadLocal<GuiceTweaker>();

    public static final String PLUGIN_ID = GuiceTweaker.class.getName();

    private List<SpincastPlugin> extraPlugins;
    private Set<Module> modules;
    private Set<String> pluginsToDisable;
    private boolean bindCurrentClassByDefault = true;

    private Injector injector;

    @Override
    public String getId() {
        return PLUGIN_ID;
    }

    protected List<SpincastPlugin> getExtraPlugins() {
        if (this.extraPlugins == null) {
            this.extraPlugins = new ArrayList<SpincastPlugin>();
        }
        return this.extraPlugins;
    }

    protected Set<Module> getModules() {
        if (this.modules == null) {
            this.modules = new HashSet<Module>();
        }
        return this.modules;
    }

    public boolean isBindCurrentClassByDefault() {
        return this.bindCurrentClassByDefault;
    }

    @Override
    public void createdGuiceInjector(Injector injector) {
        this.injector = injector;
    }

    public Injector getInjector() {
        return this.injector;
    }

    @Override
    public Module apply(Module currentModule) {

        //==========================================
        // Applies the extra plugins
        //==========================================
        for (SpincastPlugin plugin : getExtraPlugins()) {
            currentModule = plugin.apply(currentModule);
        }

        //==========================================
        // Overrides the current module, with the local ones.
        //==========================================
        if (getModules().size() > 0) {
            currentModule = Modules.override(currentModule).with(getModules());
        }

        return currentModule;
    }

    @Override
    public Set<String> getPluginsToDisable() {
        if (this.pluginsToDisable == null) {
            this.pluginsToDisable = new HashSet<String>();
        }
        return this.pluginsToDisable;
    }

    @Override
    public void setRequestContextImplementationClass(Class<? extends RequestContext<?>> requestContextImplementationClass) {
    }

    @Override
    public void setWebsocketContextImplementationClass(Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
    }

    public void module(Module module) {
        Objects.requireNonNull(module, "The module can't be NULL");
        getModules().add(module);
    }

    public void plugin(SpincastPlugin plugin) {
        Objects.requireNonNull(plugin, "The plugin can't be NULL");
        getExtraPlugins().add(plugin);
    }

    public void bindCurrentClassByDefault(boolean bindCurrentClassByDefault) {
        this.bindCurrentClassByDefault = bindCurrentClassByDefault;
    }

    public void pluginToDisable(String pluginId) {
        getPluginsToDisable().add(pluginId);
    }

}
