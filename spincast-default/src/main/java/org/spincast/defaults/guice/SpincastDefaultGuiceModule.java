package org.spincast.defaults.guice;

import org.spincast.core.guice.SpincastCoreGuiceModule;
import org.spincast.plugins.config.SpincastConfigPluginGuiceModule;
import org.spincast.plugins.cookies.SpincastCookiesPluginGuiceModule;
import org.spincast.plugins.dictionary.SpincastDictionaryPluginGuiceModule;
import org.spincast.plugins.jacksonjson.SpincastJacksonJsonPluginGuiceModule;
import org.spincast.plugins.jacksonxml.SpincastJacksonXmlPluginGuiceModule;
import org.spincast.plugins.localeresolver.SpincastLocaleResolverPluginGuiceModule;
import org.spincast.plugins.pebble.SpincastPebblePluginGuiceModule;
import org.spincast.plugins.request.SpincastRequestPluginGuiceModule;
import org.spincast.plugins.response.SpincastResponsePluginGuiceModule;
import org.spincast.plugins.routing.SpincastRoutingPluginGuiceModule;
import org.spincast.plugins.templatingaddon.SpincastTemplatingAddonPluginGuiceModule;
import org.spincast.plugins.undertow.SpincastUndertowPluginGuiceModule;
import org.spincast.plugins.variables.SpincastVariablesPluginGuiceModule;

/**
 * Spincast Guice module that binds a default implementation
 * for all the required modules.
 * 
 * To tweak this module,
 * 
 * 1. Extend it to create your custom module
 *    and override some methods. For example:
 * 
 *    public class AppModule extends SpincastDefaultGuiceModule {
 *        @Override
 *        protected void configure() {
 *            super.configure();
 *            
 *            // Add some new bindings here...
 *        }
 *        
 *        // Override some methods here... 
 *        
 *    }
 *    
 *    Then:
 *    Injector guice = Guice.createInjector(new AppModule());
 *    
 * 2. Use Modules.override() to add modules. For example:
 * 
 *    Injector guice = Guice.createInjector(Modules.override(new SpincastDefaultGuiceModule(args))
 *                                                 .with(new AppModule()));
 *                                                 
 * 3. You can also extends from SpincastCoreGuiceModule directly if you
 *    want to start from scratch and not use the default implementations.                                      
 */
public class SpincastDefaultGuiceModule extends SpincastCoreGuiceModule {

    public SpincastDefaultGuiceModule() {
        this(null);
    }

    /**
     * @param mainArgs The main method's arguments. If specified, they will
     *        be bound uding the @MainArgs annotation.
     */
    public SpincastDefaultGuiceModule(String[] mainArgs) {
        super(mainArgs);
    }

    @Override
    protected void configure() {

        super.configure();

        bindConfigPlugin();
        bindDictionaryPlugin();
        bindServerPlugin();
        bindTemplatingEnginePlugin();
        binJsonManagerPlugin();
        binXmlManagerPlugin();
        bindCookiesPlugin();
        bindRequestPlugin();
        bindResponsePlugin();
        bindRoutingPlugin();
        bindTemplatingPlugin();
        bindVariablesPlugin();
        bindLocaleResolverPlugin();
    }

    protected void bindConfigPlugin() {

        //==========================================
        // Spincast Config Plugin
        //==========================================
        install(new SpincastConfigPluginGuiceModule(getRequestContextType()));
    }

    protected void bindDictionaryPlugin() {

        //==========================================
        // Spincast Dictionary Plugin
        //==========================================
        install(new SpincastDictionaryPluginGuiceModule(getRequestContextType()));
    }

    protected void bindServerPlugin() {

        //==========================================
        // Undertow
        //==========================================
        install(new SpincastUndertowPluginGuiceModule(getRequestContextType()));
    }

    protected void bindTemplatingEnginePlugin() {

        //==========================================
        // Pebble
        //==========================================
        install(new SpincastPebblePluginGuiceModule(getRequestContextType()));
    }

    protected void binJsonManagerPlugin() {

        //==========================================
        // Jackson
        //==========================================
        install(new SpincastJacksonJsonPluginGuiceModule(getRequestContextType()));
    }

    protected void binXmlManagerPlugin() {

        //==========================================
        // Jackson
        //==========================================
        install(new SpincastJacksonXmlPluginGuiceModule(getRequestContextType()));
    }

    protected void bindCookiesPlugin() {

        //==========================================
        // Spincast Cookies plugin
        //==========================================
        install(new SpincastCookiesPluginGuiceModule(getRequestContextType()));
    }

    protected void bindRequestPlugin() {

        //==========================================
        // Spincast Request plugin
        //==========================================
        install(new SpincastRequestPluginGuiceModule(getRequestContextType()));
    }

    protected void bindResponsePlugin() {

        //==========================================
        // Spincast Response plugin
        //==========================================
        install(new SpincastResponsePluginGuiceModule(getRequestContextType()));
    }

    protected void bindRoutingPlugin() {

        //==========================================
        // Spincast Routing plugin
        //==========================================
        installRoutingPlugin();

    }

    protected void installRoutingPlugin() {
        install(new SpincastRoutingPluginGuiceModule(getRequestContextType()));
    }

    protected void bindTemplatingPlugin() {

        //==========================================
        // Spincast Templating plugin
        //==========================================
        install(new SpincastTemplatingAddonPluginGuiceModule(getRequestContextType()));
    }

    protected void bindVariablesPlugin() {

        //==========================================
        // Spincast Variables plugin
        //==========================================
        install(new SpincastVariablesPluginGuiceModule(getRequestContextType()));
    }

    protected void bindLocaleResolverPlugin() {

        //==========================================
        // Spincast Locale Resolver plugin
        //==========================================
        install(new SpincastLocaleResolverPluginGuiceModule(getRequestContextType()));
    }

}
