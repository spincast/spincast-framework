package org.spincast.core.guice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.websocket.WebsocketContext;

import com.google.inject.Injector;
import com.google.inject.Key;
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
    private Set<Module> overridingModules;
    private Set<Key<?>> exactBindingsToRemove;
    private Set<Class<?>> bindingsHierarchiesToRemove;
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

    protected Set<Module> getOverridingModules() {
        if (this.overridingModules == null) {
            this.overridingModules = new HashSet<Module>();
        }
        return this.overridingModules;
    }

    protected Set<Key<?>> getExactBindingsToRemove() {
        if (this.exactBindingsToRemove == null) {
            this.exactBindingsToRemove = new HashSet<Key<?>>();
        }
        return this.exactBindingsToRemove;
    }

    public Set<Class<?>> getBindingsHierarchiesToRemove() {
        if (this.bindingsHierarchiesToRemove == null) {
            this.bindingsHierarchiesToRemove = new HashSet<Class<?>>();
        }
        return this.bindingsHierarchiesToRemove;
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

    /**
     * Ran before the plugins are applied in
     * SpincastBootstrapper.
     * 
     * @param combinedModule the combinaison of all the
     * Guice modules just before the plugins are applied.
     */
    public Module beforePlugins(final Module combinedModule) {

        Module newModule = combinedModule;

        GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(newModule);

        //==========================================
        // Removes some bindings hierarchies
        //==========================================
        if (getBindingsHierarchiesToRemove().size() > 0) {
            for (Class<?> parentClass : this.bindingsHierarchiesToRemove) {
                Set<Class<?>> classes = guiceModuleUtils.getBoundClassesExtending(parentClass);
                if (classes.size() > 0) {
                    for (Class<?> clazz : classes) {
                        getExactBindingsToRemove().add(Key.get(clazz));
                    }
                }
            }
        }

        //==========================================
        // Removes some exact bindings
        //==========================================
        if (getExactBindingsToRemove().size() > 0) {
            newModule = GuiceModuleUtils.removeBindings(newModule, getExactBindingsToRemove());
        }

        //==========================================
        // Overrides some bindings
        //==========================================
        if (getOverridingModules() != null && getOverridingModules().size() > 0) {
            newModule = Modules.override(newModule).with(getOverridingModules());
        }

        return newModule;
    }

    /**
     * Ran as a regular plugin.
     */
    @Override
    public Module apply(Module currentModule) {

        //==========================================
        // Applies the extra plugins
        //==========================================
        for (SpincastPlugin plugin : getExtraPlugins()) {
            currentModule = plugin.apply(currentModule);
        }

        return currentModule;
    }

    /**
     * Ran after the plugins are applied in
     * SpincastBootstrapper.
     * 
     * @param combinedModule the combinaison of all the
     * Guice modules just after the plugins have been applied.
     */
    public Module afterPlugins(Module combinedModule) {

        //==========================================
        // Nothing required for now
        //==========================================
        return combinedModule;
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

    /**
     * Adds an overriding Module. The bindings of this Module
     * will be applied before the plugins run in SpincastBootstrapper.
     */
    public void overridingModule(Module overridingModule) {
        Objects.requireNonNull(overridingModule, "The module can't be NULL");
        getOverridingModules().add(overridingModule);
    }

    /**
     * An exact binding to remove from the combined modules.
     * This will be done  before the plugins run 
     * in SpincastBootstrapper.
     */
    public void exactBindingToRemove(Key<?> key) {
        Objects.requireNonNull(key, "The key can't be NULL");
        getExactBindingsToRemove().add(key);
    }

    /**
     * An binding hierarchy to remove from the combined modules. All
     * bindings implementing or extending the specified parent
     * class, directly or indirectly, will be removed.
     * 
     * This will be done  before the plugins run 
     * in SpincastBootstrapper.
     */
    public void bindingHierarchyToRemove(Class<?> parentClass) {
        Objects.requireNonNull(parentClass, "The class can't be NULL");
        getBindingsHierarchiesToRemove().add(parentClass);
    }

    /**
     * Adds an extra plugin to be applied when
     * the Guice context is created.
     */
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
