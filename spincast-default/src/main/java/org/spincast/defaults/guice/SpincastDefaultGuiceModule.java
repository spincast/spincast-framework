package org.spincast.defaults.guice;

import org.spincast.core.guice.SpincastCoreGuiceModule;
import org.spincast.plugins.config.SpincastConfigPluginGuiceModule;
import org.spincast.plugins.cookies.SpincastCookiesPluginGuiceModule;
import org.spincast.plugins.dictionary.SpincastDictionaryPluginGuiceModule;
import org.spincast.plugins.httpcaching.SpincastHttpCachingPluginGuiceModule;
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
 * <p>
 * To tweak this module,
 * <ol>
 * <li>
 * Extend it to create your custom module
 * and override some methods. For example:
 * <pre>
 *public class AppModule extends SpincastDefaultGuiceModule {
 *
 *    protected void configure() {
 *        super.configure();
 *            
 *        // Add some new bindings here...
 *    }
 *        
 *    // Override some methods here... 
 *        
 *}
 *</pre>
 * Then:
 *<pre>Injector guice = Guice.createInjector(new AppModule());</pre> 
 * </li>
 * </ol>
 * <li>
 * Use Modules.override() to add modules. For example:
 * <pre>
 * Injector guice = Guice.createInjector(Modules.override(new SpincastDefaultGuiceModule(args))
 *                                              .with(new AppModule()));
 * </pre>
 * </li>
 * <li>
 * You can also extend {@link SpincastCoreGuiceModule} directly if you
 * want to start from scratch and not use the default implementations!   
 * </li>                               
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
        bindHttpCachingPlugin();
    }

    protected void bindConfigPlugin() {

        //==========================================
        // Spincast Config Plugin
        //==========================================
        install(new SpincastConfigPluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
    }

    protected void bindDictionaryPlugin() {

        //==========================================
        // Spincast Dictionary Plugin
        //==========================================
        install(new SpincastDictionaryPluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
    }

    protected void bindServerPlugin() {

        //==========================================
        // Undertow
        //==========================================
        install(new SpincastUndertowPluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
    }

    protected void bindTemplatingEnginePlugin() {

        //==========================================
        // Pebble
        //==========================================
        install(new SpincastPebblePluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
    }

    protected void binJsonManagerPlugin() {

        //==========================================
        // Jackson
        //==========================================
        install(new SpincastJacksonJsonPluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
    }

    protected void binXmlManagerPlugin() {

        //==========================================
        // Jackson
        //==========================================
        install(new SpincastJacksonXmlPluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
    }

    protected void bindCookiesPlugin() {

        //==========================================
        // Spincast Cookies plugin
        //==========================================
        install(new SpincastCookiesPluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
    }

    protected void bindRequestPlugin() {

        //==========================================
        // Spincast Request plugin
        //==========================================
        install(new SpincastRequestPluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
    }

    protected void bindResponsePlugin() {

        //==========================================
        // Spincast Response plugin
        //==========================================
        install(new SpincastResponsePluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
    }

    protected void bindRoutingPlugin() {

        //==========================================
        // Spincast Routing plugin
        //==========================================
        install(new SpincastRoutingPluginGuiceModule(getRequestContextType(), getWebsocketContextType()));

    }

    protected void bindTemplatingPlugin() {

        //==========================================
        // Spincast Templating plugin
        //==========================================
        install(new SpincastTemplatingAddonPluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
    }

    protected void bindVariablesPlugin() {

        //==========================================
        // Spincast Variables plugin
        //==========================================
        install(new SpincastVariablesPluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
    }

    protected void bindLocaleResolverPlugin() {

        //==========================================
        // Spincast Locale Resolver plugin
        //==========================================
        install(new SpincastLocaleResolverPluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
    }

    protected void bindHttpCachingPlugin() {

        //==========================================
        // Spincast HTTP Caching plugin
        //==========================================
        install(new SpincastHttpCachingPluginGuiceModule(getRequestContextType(), getWebsocketContextType()));
    }

}
