package org.spincast.defaults.bootstrapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.exchange.DefaultRequestContextDefault;
import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.GuiceTweaker;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.guice.SpincastContextTypesInterested;
import org.spincast.core.guice.SpincastCorePlugin;
import org.spincast.core.guice.SpincastGuiceModuleBase;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.websocket.DefaultWebsocketContextDefault;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.config.SpincastConfigPlugin;
import org.spincast.plugins.dictionary.SpincastDictionaryPlugin;
import org.spincast.plugins.httpcaching.SpincastHttpCachingPlugin;
import org.spincast.plugins.jacksonjson.SpincastJacksonJsonPlugin;
import org.spincast.plugins.jacksonxml.SpincastJacksonXmlPlugin;
import org.spincast.plugins.localeresolver.SpincastLocaleResolverPlugin;
import org.spincast.plugins.pebble.SpincastPebblePlugin;
import org.spincast.plugins.request.SpincastRequestPlugin;
import org.spincast.plugins.response.SpincastResponsePlugin;
import org.spincast.plugins.routing.SpincastRoutingPlugin;
import org.spincast.plugins.templatingaddon.SpincastTemplatingAddonPlugin;
import org.spincast.plugins.timezoneresolver.SpincastTimeZoneResolverPlugin;
import org.spincast.plugins.undertow.SpincastUndertowPlugin;
import org.spincast.plugins.variables.SpincastVariablesPlugin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Modules;

/**
 * Builder to help initialize an application and create its
 * Guice context.
 * <p>
 * It is in general started using the static method
 * <code>Spincast.configure()</code>.
 */
public class SpincastBootstrapper {

    protected static final Logger logger = LoggerFactory.getLogger(SpincastBootstrapper.class);

    private static Map<String, Module> defaultModulesMap = null;

    private static Module defaultModuleWithCore;
    private static Module defaultModuleWithoutCore;
    private Set<Module> appModules;
    private List<SpincastPlugin> plugins;
    private Set<String> pluginsToDisable;

    private boolean disableAllDefaultPlugins = false;
    private boolean disableCorePlugin = false;
    private boolean disableDefaultRoutingPlugin = false;
    private boolean disableDefaultJsonPlugin = false;
    private boolean disableDefaultXmlPlugin = false;
    private boolean disableDefaultRequestPlugin = false;
    private boolean disableDefaultResponsePlugin = false;
    private boolean disableDefaultTemplatingPlugin = false;
    private boolean disableDefaultTemplatingAddonPlugin = false;
    private boolean disableDefaultVariablesPlugin = false;
    private boolean disableDefaultLocaleResolverPlugin = false;
    private boolean disableDefaultTimeZoneResolverPlugin = false;
    private boolean disableDefaultHttpCachingPlugin = false;
    private boolean disableDefaultCookiesPlugin = false;
    private boolean disableDefaultConfigPlugin = false;
    private boolean disableDefaultDictionaryPlugin = false;
    private boolean disableDefaultServerPlugin = false;

    private boolean bindCallerClass = true;
    private boolean bindCallerClassSet = false;
    private Class<?> appClass;

    private Class<? extends RequestContext<?>> requestContextImplementationClass = null;
    private Class<? extends WebsocketContext<?>> websocketContextImplementationClass = null;

    protected Class<? extends RequestContext<?>> getRequestContextImplementationClass() {

        if (this.requestContextImplementationClass == null) {
            this.requestContextImplementationClass = DefaultRequestContextDefault.class;
        }
        return this.requestContextImplementationClass;
    }

    protected Class<? extends WebsocketContext<?>> getWebsocketContextImplementationClass() {

        if (this.websocketContextImplementationClass == null) {
            this.websocketContextImplementationClass = DefaultWebsocketContextDefault.class;
        }
        return this.websocketContextImplementationClass;
    }

    protected Class<?> getAppClass() {
        return this.appClass;
    }

    protected boolean isBindCallerClass() {
        return this.bindCallerClass;
    }

    protected boolean isBindCallerClassSet() {
        return this.bindCallerClassSet;
    }

    protected boolean isDisableAllDefaultPlugins() {
        return this.disableAllDefaultPlugins;
    }

    protected boolean isDisableCorePlugin() {
        return this.disableCorePlugin;
    }

    protected boolean isDisableDefaultRoutingPlugin() {
        return this.disableDefaultRoutingPlugin;
    }

    protected boolean isDisableDefaultJsonPlugin() {
        return this.disableDefaultJsonPlugin;
    }

    protected boolean isDisableDefaultXmlPlugin() {
        return this.disableDefaultXmlPlugin;
    }

    protected boolean isDisableDefaultRequestPlugin() {
        return this.disableDefaultRequestPlugin;
    }

    protected boolean isDisableDefaultResponsePlugin() {
        return this.disableDefaultResponsePlugin;
    }

    protected boolean isDisableDefaultTemplatingPlugin() {
        return this.disableDefaultTemplatingPlugin;
    }

    protected boolean isDisableDefaultTemplatingAddonPlugin() {
        return this.disableDefaultTemplatingAddonPlugin;
    }

    protected boolean isDisableDefaultVariablesPlugin() {
        return this.disableDefaultVariablesPlugin;
    }

    protected boolean isDisableDefaultLocaleResolverPlugin() {
        return this.disableDefaultLocaleResolverPlugin;
    }

    protected boolean isDisableDefaultTimeZoneResolverPlugin() {
        return this.disableDefaultTimeZoneResolverPlugin;
    }

    protected boolean isDisableDefaultHttpCachingPlugin() {
        return this.disableDefaultHttpCachingPlugin;
    }

    protected boolean isDisableDefaultCookiesPlugin() {
        return this.disableDefaultCookiesPlugin;
    }

    protected boolean isDisableDefaultConfigPlugin() {
        return this.disableDefaultConfigPlugin;
    }

    protected boolean isDisableDefaultDictionaryPlugin() {
        return this.disableDefaultDictionaryPlugin;
    }

    protected boolean isDisableDefaultServerPlugin() {
        return this.disableDefaultServerPlugin;
    }

    protected static Map<String, Module> getDefaultModulesMap() {

        if (defaultModulesMap == null) {
            defaultModulesMap = new HashMap<String, Module>();
        }

        return defaultModulesMap;
    }

    protected List<SpincastPlugin> getPlugins() {

        if (this.plugins == null) {
            this.plugins = new ArrayList<SpincastPlugin>();
        }
        return this.plugins;
    }

    protected Set<Module> getAppModules() {

        if (this.appModules == null) {
            this.appModules = new HashSet<Module>();
        }
        return this.appModules;
    }

    protected Set<String> getPluginsToDisable() {

        if (this.pluginsToDisable == null) {
            this.pluginsToDisable = new HashSet<String>();
        }
        return this.pluginsToDisable;
    }

    public SpincastBootstrapper requestContextImplementationClass(Class<? extends RequestContext<?>> clazz) {

        if (clazz != null && clazz.isInterface()) {
            throw new RuntimeException("You have to bind the *implementation* class of your " +
                                       "custom RequestContext type, not its interface!");
        }

        this.requestContextImplementationClass = clazz;
        return this;
    }

    public SpincastBootstrapper websocketContextImplementationClass(Class<? extends WebsocketContext<?>> clazz) {

        if (clazz != null && clazz.isInterface()) {
            throw new RuntimeException("You have to bind the *implementation* class of your " +
                                       "custom WebsocketContext type, not its interface!");
        }

        this.websocketContextImplementationClass = clazz;
        return this;
    }

    public SpincastBootstrapper bindCurrentClass(boolean bindCallerClass) {
        this.bindCallerClass = bindCallerClass;
        this.bindCallerClassSet = true;
        return this;
    }

    /**
     * Bind the specified class as the app class. 
     * <p>
     * When this is called the "current class" won't be bound.
     */
    public SpincastBootstrapper appClass(Class<?> appClass) {
        this.appClass = appClass;
        return this;
    }

    /**
     * Disable <em>all</em> the default plugins (including the
     * core plugin).
     * <p>
     * If you use this, you're going to have to 
     * bind implementations for all the 
     * components required by a Spincast application,
     * by yourself.
     */
    public SpincastBootstrapper disableAllDefaultPlugins() {
        this.disableAllDefaultPlugins = true;
        return this;
    }

    /**
     * Disables the default Core plugin.
     */
    public SpincastBootstrapper disableCorePlugin() {
        this.disableCorePlugin = true;
        return this;
    }

    /**
     * Disables the default Routing plugin.
     */
    public SpincastBootstrapper disableDefaultRoutingPlugin() {
        this.disableDefaultRoutingPlugin = true;
        return this;
    }

    /**
     * Disables the default Json plugin.
     */
    public SpincastBootstrapper disableDefaultJsonPlugin() {
        this.disableDefaultJsonPlugin = true;
        return this;
    }

    /**
     * Disables the default XML plugin.
     */
    public SpincastBootstrapper disableDefaultXmlPlugin() {
        this.disableDefaultXmlPlugin = true;
        return this;
    }

    /**
     * Disables the default Request plugin.
     */
    public SpincastBootstrapper disableDefaultRequestPlugin() {
        this.disableDefaultRequestPlugin = true;
        return this;
    }

    /**
     * Disables the default Response plugin.
     */
    public SpincastBootstrapper disableDefaultResponsePlugin() {
        this.disableDefaultResponsePlugin = true;
        return this;
    }

    /**
     * Disables the default Templating plugin.
     */
    public SpincastBootstrapper disableDefaultTemplatingPlugin() {
        this.disableDefaultTemplatingPlugin = true;
        return this;
    }

    /**
     * Disables the default Templating add-on plugin.
     */
    public SpincastBootstrapper disableDefaultTemplatingAddonPlugin() {
        this.disableDefaultTemplatingAddonPlugin = true;
        return this;
    }

    /**
     * Disables the default Variables add-on plugin.
     */
    public SpincastBootstrapper disableDefaultVariablesPlugin() {
        this.disableDefaultVariablesPlugin = true;
        return this;
    }

    /**
     * Disables the default Locale Resolver plugin.
     */
    public SpincastBootstrapper disableDefaultLocaleResolverPlugin() {
        this.disableDefaultLocaleResolverPlugin = true;
        return this;
    }

    /**
     * Disables the default TimeZone Resolver plugin.
     */
    public SpincastBootstrapper disableDefaultTimeZoneResolverPlugin() {
        this.disableDefaultTimeZoneResolverPlugin = true;
        return this;
    }

    /**
     * Disables the default HTTP Caching plugin.
     */
    public SpincastBootstrapper disableDefaultHttpCachingPlugin() {
        this.disableDefaultHttpCachingPlugin = true;
        return this;
    }

    /**
     * Disables the default Cookies plugin.
     */
    public SpincastBootstrapper disableDefaultCookiesPlugin() {
        this.disableDefaultCookiesPlugin = true;
        return this;
    }

    /**
     * Disables the default Configuration plugin.
     */
    public SpincastBootstrapper disableDefaultConfigPlugin() {
        this.disableDefaultConfigPlugin = true;
        return this;
    }

    /**
     * Disables the default Dictionary plugin.
     */
    public SpincastBootstrapper disableDefaultDictionaryPlugin() {
        this.disableDefaultDictionaryPlugin = true;
        return this;
    }

    /**
     * Disables the default Server plugin.
     */
    public SpincastBootstrapper disableDefaultServerPlugin() {
        this.disableDefaultServerPlugin = true;
        return this;
    }

    /**
     * Adds a Guice module.
     */
    public SpincastBootstrapper module(Module module) {
        Objects.requireNonNull(module, "The module can't be NULL");
        getAppModules().add(module);
        return this;
    }

    /**
     * Adds some Guice modules.
     */
    public SpincastBootstrapper modules(List<Module> modules) {

        if (modules == null || modules.size() == 0) {
            return this;
        }

        for (Module module : modules) {
            module(module);
        }

        return this;
    }

    /**
     * Adds a plugin.
     * 
     * Plugins will be applied in the order they are added.
     */
    public SpincastBootstrapper plugin(SpincastPlugin plugin) {
        Objects.requireNonNull(plugin, "The plugin can't be NULL");
        getPlugins().add(plugin);
        return this;
    }

    /**
     * Adds some plugin.
     * 
     * Plugins will be applied in the order they are added.
     */
    public SpincastBootstrapper plugins(List<SpincastPlugin> plugins) {

        if (plugins == null || plugins.size() == 0) {
            return this;
        }

        for (SpincastPlugin plugin : plugins) {
            plugin(plugin);
        }

        return this;
    }

    /**
     * Create the Guice context and starts the 
     * application.
     * 
     * @param mainArgs the application main arguments.
     * Those will be automatically bound to :
     * <code>@MainArgs String[]</code> and
     * <code>@MainArgs List&lt;String&gt;</code>
     */
    public Injector init(String[] mainArgs) {
        try {

            //==========================================
            // Adds default plugins
            //==========================================
            if (!isDisableAllDefaultPlugins()) {
                getPlugins().addAll(getDefaultPlugins());
            }

            //==========================================
            // Is there a GuiceTweaker?
            //==========================================
            final GuiceTweaker guiceTweaker = GuiceTweaker.threadLocal.get();
            if (guiceTweaker != null) {
                guiceTweaker.setRequestContextImplementationClass(getRequestContextImplementationClass());
                guiceTweaker.setWebsocketContextImplementationClass(getWebsocketContextImplementationClass());
                getPlugins().add(guiceTweaker);
            }

            //==========================================
            // Plugins disabling other plugins
            //==========================================
            for (SpincastPlugin plugin : getPlugins()) {
                if (plugin.getPluginsToDisable() != null && !getPluginsToDisable().contains(plugin.getId())) {
                    getPluginsToDisable().addAll(plugin.getPluginsToDisable());
                }
            }

            //==========================================
            // Adds "main args" module
            //==========================================
            addMainArgsModule(mainArgs);

            //==========================================
            // Binds the app class, if required.
            //==========================================
            Class<?> appClass = null;
            if (getAppClass() != null) {
                appClass = getAppClass();
                getAppModules().add(new SpincastGuiceModuleBase() {

                    @Override
                    protected void configure() {
                        bind(getAppClass()).in(Scopes.SINGLETON);
                    }
                });
            } else if (isBindCallerClass()) {

                if (guiceTweaker == null || !guiceTweaker.isDisableBindCurrentClass()) {
                    appClass = addCallerClassModule();
                }
            }

            //==========================================
            // Set the Request Context type and the
            // Websocket Context type on modules and
            // plugins
            //==========================================
            for (Module module : getAppModules()) {
                if (module instanceof SpincastContextTypesInterested) {
                    setSpincastContextes((SpincastContextTypesInterested)module);
                }
            }
            for (SpincastPlugin plugin : getPlugins()) {
                setSpincastContextes(plugin);
            }

            //==========================================
            // We start the creation of the Guice context
            // with the app's modules.
            //
            // This is required so some plugins can
            // know if a specific implementation has to
            // be used instead of a default one.
            //
            // For example, in SpincastRoutingPlugin,
            // we check if there is a custom implementation
            // of the "Router" and we bind this custom
            // implementation to 3 other Guice Keys!
            //==========================================
            Module combinedModule = Modules.combine(getAppModules());

            //==========================================
            // GuiceTweaker : tweaks the combined Module
            // before applying the plugins.
            //==========================================
            if (guiceTweaker != null) {
                combinedModule = guiceTweaker.beforePlugins(combinedModule);
            }

            //==========================================
            // Applies the plugins
            //==========================================
            for (SpincastPlugin plugin : getPlugins()) {
                if (!getPluginsToDisable().contains(plugin.getId())) {
                    combinedModule = plugin.apply(combinedModule);
                    logger.info("Plugin '" + plugin.getId() + "' applied.");
                } else {
                    logger.info("Plugin '" + plugin.getId() + "' ignored.");
                }
            }

            //==========================================
            // We add the application modules *again*, this
            // time as an "override"...
            // This is required for the app to be able to
            // override a default plugin configuration
            // class, for example.
            //
            // The app must have the very last word on
            // what bindings to use (exceot if a GuiceTweaker
            // is used, during testing)
            //==========================================
            combinedModule = Modules.override(combinedModule).with(getAppModules());

            //==========================================
            // GuiceTweaker : final tweaks of the modules...
            //==========================================
            if (guiceTweaker != null) {
                combinedModule = guiceTweaker.afterPlugins(combinedModule);
            }

            //==========================================
            // We create the Guice context!
            //==========================================
            Injector guice = Guice.createInjector(combinedModule);

            //==========================================
            // Pass the created guice injector to plugins
            // that may be interested in it.
            //==========================================
            for (SpincastPlugin plugin : getPlugins()) {
                plugin.createdGuiceInjector(guice);
            }

            //==========================================
            // Now that the Guice context is ready (including
            // the asEagerSingleton components), we can 
            // initialize the caller, if required.
            //==========================================
            if (appClass != null) {
                guice.getInstance(appClass);
            }

            return guice;

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Adds a module to bind the caller class.
     * 
     * @return the caller class
     */
    protected Class<?> addCallerClassModule() {

        //==========================================
        // Gets the caller's class
        //==========================================
        Class<?> callerClass = null;
        try {
            String caller = getCallerClassName();
            if (caller == null) {
                throw new RuntimeException("Unable to get the name of the caller class! " +
                                           "You may want to disable its automatic binding by using \".bindCallerClass(false)\".");
            } else {
                callerClass = Class.forName(caller);
            }
        } catch (Throwable ex) {
            throw new RuntimeException("Unable to create the class of the caller." +
                                       "You may want to disable its automatic binding by using \".bindCallerClass(false)\".");
        }

        final Class<?> callerClassFinal = callerClass;
        if (callerClassFinal != null) {
            getAppModules().add(new SpincastGuiceModuleBase() {

                @Override
                protected void configure() {
                    bind(callerClassFinal).in(Scopes.SINGLETON);
                }
            });
        }
        return callerClass;
    }

    /**
     * Returns the class name of the caller class.
     */
    protected String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(SpincastBootstrapper.class.getName()) &&
                !ste.getClassName().equals(Spincast.class.getName()) &&
                !ste.getClassName().equals(Thread.class.getName())) {
                return ste.getClassName();
            }
        }
        return null;
    }

    /**
     * Adds a module to bind the main args.
     */
    protected void addMainArgsModule(String[] mainArgs) {

        if (mainArgs == null) {
            mainArgs = new String[]{};
        }
        final String[] mainArgsFinal = mainArgs;
        getAppModules().add(new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
                bind(new TypeLiteral<String[]>() {}).annotatedWith(MainArgs.class)
                                                    .toInstance(mainArgsFinal);
                bind(new TypeLiteral<List<String>>() {}).annotatedWith(MainArgs.class)
                                                        .toInstance(Arrays.asList(mainArgsFinal));
            }
        });
    }

    /**
     * Returns the default plugins, except those already
     * bound.
     */
    protected List<SpincastPlugin> getDefaultPlugins() {
        return getDefaultPlugins(true);
    }

    /**
     * Returns the default plugins, except those already
     * bound.
     */
    protected List<SpincastPlugin> getDefaultPlugins(boolean addCodePlugin) {

        List<SpincastPlugin> plugins = new ArrayList<SpincastPlugin>();

        if (addCodePlugin && !this.disableCorePlugin && !pluginBound(SpincastCorePlugin.class)) {
            plugins.add(getSpincastCorePlugin());
        }

        if (!isDisableDefaultRoutingPlugin() && !pluginBound(SpincastRoutingPlugin.class)) {
            plugins.add(getSpincastRoutingPlugin());
        }

        if (!isDisableDefaultJsonPlugin() && !pluginBound(SpincastJacksonJsonPlugin.class)) {
            plugins.add(getSpincastJacksonJsonPlugin());
        }

        if (!isDisableDefaultXmlPlugin() && !pluginBound(SpincastJacksonXmlPlugin.class)) {
            plugins.add(getSpincastJacksonXmlPlugin());
        }

        if (!isDisableDefaultRequestPlugin() && !pluginBound(SpincastRequestPlugin.class)) {
            plugins.add(getSpincastRequestPlugin());
        }

        if (!isDisableDefaultResponsePlugin() && !pluginBound(SpincastResponsePlugin.class)) {
            plugins.add(getSpincastResponsePlugin());
        }

        if (!isDisableDefaultTemplatingPlugin() && !pluginBound(SpincastPebblePlugin.class)) {
            plugins.add(getSpincastPebblePlugin());
        }

        if (!isDisableDefaultTemplatingAddonPlugin() && !pluginBound(SpincastTemplatingAddonPlugin.class)) {
            plugins.add(getSpincastTemplatingAddonPlugin());
        }

        if (!isDisableDefaultVariablesPlugin() && !pluginBound(SpincastVariablesPlugin.class)) {
            plugins.add(getSpincastVariablesPlugin());
        }

        if (!isDisableDefaultLocaleResolverPlugin() && !pluginBound(SpincastLocaleResolverPlugin.class)) {
            plugins.add(getSpincastLocaleResolverPlugin());
        }

        if (!isDisableDefaultTimeZoneResolverPlugin() && !pluginBound(SpincastTimeZoneResolverPlugin.class)) {
            plugins.add(getSpincastTimeZoneResolverPlugin());
        }

        if (!isDisableDefaultHttpCachingPlugin() && !pluginBound(SpincastHttpCachingPlugin.class)) {
            plugins.add(getSpincastHttpCachingPlugin());
        }

        if (!isDisableDefaultConfigPlugin() && !pluginBound(SpincastConfigPlugin.class)) {
            plugins.add(getSpincastConfigPlugin());
        }

        if (!isDisableDefaultDictionaryPlugin() && !pluginBound(SpincastDictionaryPlugin.class)) {
            plugins.add(getSpincastDictionaryPlugin());
        }

        if (!isDisableDefaultServerPlugin() && !pluginBound(SpincastUndertowPlugin.class)) {
            plugins.add(getSpincastUndertowPlugin());
        }

        return plugins;
    }

    protected boolean pluginBound(Class<? extends SpincastPlugin> clazz) {

        List<SpincastPlugin> plugins = getPlugins();
        for (SpincastPlugin plugin : plugins) {
            if (clazz.isAssignableFrom(plugin.getClass())) {
                return true;
            }
        }

        Set<Module> modules = getAppModules();
        for (Module module : modules) {
            if (clazz.isAssignableFrom(module.getClass())) {
                return true;
            }
        }

        return false;
    }

    protected SpincastCorePlugin getSpincastCorePlugin() {
        return new SpincastCorePlugin();
    }

    protected SpincastConfigPlugin getSpincastConfigPlugin() {
        return new SpincastConfigPlugin();
    }

    protected SpincastRoutingPlugin getSpincastRoutingPlugin() {
        return new SpincastRoutingPlugin();
    }

    protected SpincastJacksonJsonPlugin getSpincastJacksonJsonPlugin() {
        return new SpincastJacksonJsonPlugin();
    }

    protected SpincastJacksonXmlPlugin getSpincastJacksonXmlPlugin() {
        return new SpincastJacksonXmlPlugin();
    }

    protected SpincastRequestPlugin getSpincastRequestPlugin() {
        return new SpincastRequestPlugin();
    }

    protected SpincastResponsePlugin getSpincastResponsePlugin() {
        return new SpincastResponsePlugin();
    }

    protected SpincastPebblePlugin getSpincastPebblePlugin() {
        return new SpincastPebblePlugin();
    }

    protected SpincastTemplatingAddonPlugin getSpincastTemplatingAddonPlugin() {
        return new SpincastTemplatingAddonPlugin();
    }

    protected SpincastVariablesPlugin getSpincastVariablesPlugin() {
        return new SpincastVariablesPlugin();
    }

    protected SpincastLocaleResolverPlugin getSpincastLocaleResolverPlugin() {
        return new SpincastLocaleResolverPlugin();
    }

    protected SpincastTimeZoneResolverPlugin getSpincastTimeZoneResolverPlugin() {
        return new SpincastTimeZoneResolverPlugin();
    }

    protected SpincastHttpCachingPlugin getSpincastHttpCachingPlugin() {
        return new SpincastHttpCachingPlugin();
    }

    protected SpincastDictionaryPlugin getSpincastDictionaryPlugin() {
        return new SpincastDictionaryPlugin();
    }

    protected SpincastUndertowPlugin getSpincastUndertowPlugin() {
        return new SpincastUndertowPlugin();
    }

    /**
     * Sets the request and websocket contexts 
     * on a component.
     */
    protected void setSpincastContextes(SpincastContextTypesInterested interested) {
        interested.setRequestContextImplementationClass(getRequestContextImplementationClass());
        interested.setWebsocketContextImplementationClass(getWebsocketContextImplementationClass());
    }

    public static Module getDefaultModule() {
        return getCoreAndDefaultModuleInternal(DefaultRequestContextDefault.class,
                                               DefaultWebsocketContextDefault.class,
                                               true);
    }

    public static Module getDefaultModule(boolean includeCoreModule) {
        return getCoreAndDefaultModuleInternal(DefaultRequestContextDefault.class,
                                               DefaultWebsocketContextDefault.class,
                                               includeCoreModule);
    }

    public static Module getDefaultModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                          Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        return getCoreAndDefaultModuleInternal(requestContextImplementationClass,
                                               websocketContextImplementationClass,
                                               false);
    }

    public static Module getDefaultModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                          Class<? extends WebsocketContext<?>> websocketContextImplementationClass,
                                          boolean includeCoreModule) {
        return getCoreAndDefaultModuleInternal(requestContextImplementationClass,
                                               websocketContextImplementationClass,
                                               includeCoreModule);
    }

    protected static Module getCoreAndDefaultModuleInternal(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                                            Class<? extends WebsocketContext<?>> websocketContextImplementationClass,
                                                            boolean addCodePlugin) {

        if (addCodePlugin) {
            if (defaultModuleWithCore != null) {
                return defaultModuleWithCore;
            }
        } else {
            if (defaultModuleWithoutCore != null) {
                return defaultModuleWithoutCore;
            }
        }

        SpincastBootstrapper bootstrapper = new SpincastBootstrapper();
        bootstrapper.requestContextImplementationClass(requestContextImplementationClass);
        bootstrapper.websocketContextImplementationClass(websocketContextImplementationClass);

        Module module = new SpincastGuiceModuleBase() {

            @Override
            protected void configure() {
            }
        };

        List<SpincastPlugin> defaultPlugins = bootstrapper.getDefaultPlugins(addCodePlugin);
        for (SpincastPlugin plugin : defaultPlugins) {
            bootstrapper.setSpincastContextes(plugin);
            module = plugin.apply(module);
        }

        if (addCodePlugin) {
            defaultModuleWithCore = module;
        } else {
            defaultModuleWithoutCore = module;
        }

        return module;
    }

    protected static String createModulesMapKey(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                                Class<? extends WebsocketContext<?>> websocketContextImplementationClass,
                                                boolean addCore) {
        return (requestContextImplementationClass != null ? requestContextImplementationClass.getName() : "[null]") + "|" +
               (websocketContextImplementationClass != null ? websocketContextImplementationClass.getName() : "[null]") + "|" +
               String.valueOf(addCore);
    }

}
