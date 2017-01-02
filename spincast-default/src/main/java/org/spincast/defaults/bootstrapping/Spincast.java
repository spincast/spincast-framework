package org.spincast.defaults.bootstrapping;

import org.spincast.core.exchange.RequestContext;
import org.spincast.core.guice.GuiceTweaker;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.websocket.WebsocketContext;
import org.spincast.plugins.routing.DefaultRouter;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Starts the initialization of a Spincast application.
 * <p>
 * Will create the final Guice Injector and return
 * it when the <code>init()</code> method is called.
 * <p>
 * In addition to the methods directly provided by this
 * bootstrapper, it is possible to tweak the resulting Guice
 * context by using the {@link GuiceTweaker} class. The main
 * use case where a tweaker may be useful, is to write
 * <em>test classes</em> : the tweaker allows you to reuse the
 * exact same code that bootstraps your application
 * (often by calling its <code>main(...)</code> method), but
 * to <em>tweak</em> some bindings, mock some components.
 */
public class Spincast {

    /**
     * Starts the bootsrapping of a Spincast application, using the
     * default plugins.
     * <p>
     * When the configuration is done, call the <code>init()</code>
     * method to initialize the Guice context.
     * <p>
     * By default, the caller class is going to be bound in the Guice context.
     * If you want to disable this, call <code>".bindCallerClass(false)"</code>!
     * 
     * @return The bootstrapper
     */
    public static SpincastBootstrapper configure() {
        return new SpincastBootstrapper();
    }

    /**
     * Quickly bootstrap a Spincast application using 
     * the default plugins.
     * <p>
     * The caller class is going to be bound in the Guice context.
     * You may want to add a <code>"@Inject init(...)"</code>
     * method to it and start your application there.
     * <p>
     * The default <code>Request Context</code> type and the
     * default <code>Websocket Context</code> type will be used.
     * You can inject and use the {@link DefaultRouter} to add
     * Routes.
     * 
     * @return The create Guice Injector (context).
     */
    public static Injector init() {
        return init(null);
    }

    /**
     * Initialize a default Spincast application.
     * <p>
     * The caller class is going to be bound in the Guice context.
     * You may want to add some kind of <code>"@Inject init()"</code>
     * method in it and start your application there!
     * <p>
     * The default <code>Request Context</code> type and the
     * default <code>Websocket Context</code> type will be used.
     * 
     * @param args The parameters received in the <code>main</code>
     * method. Those will be bound in the Guice context and you will
     * be able to inject them wherever you need, using the 
     * {@link MainArgs @MainArgs} annotation.
     * 
     * @return The create Guice Injector (context).
     */
    public static Injector init(String[] args) {
        return configure().mainArgs(args).init();
    }

    /**
     * Returns a module combining all the default 
     * plugins implementations, parametrized with the 
     * default Request and Websocket contextes.
     * <p>
     * The {@link SpincastCoreGuiceModule} module is
     * included.
     */
    public static Module getDefaultModule() {
        return SpincastBootstrapper.getDefaultModule();
    }

    /**
     * Returns a module combining all the default 
     * plugins implementations, parametrized with the 
     * default Request and Websocket contextes.
     * <p>
     * The {@link SpincastCoreGuiceModule} module is not
     * included.
     * 
     * @param includeCoreModule Should the Core module being
     * added too?
     */
    public static Module getDefaultModule(boolean includeCoreModule) {
        return SpincastBootstrapper.getDefaultModule(includeCoreModule);
    }

    /**
     * Returns a module combining all the default 
     * plugins implementations.
     * <p>
     * The {@link SpincastCoreGuiceModule} module is not
     * included.
     */
    public static Module getDefaultModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                          Class<? extends WebsocketContext<?>> websocketContextImplementationClass) {
        return SpincastBootstrapper.getDefaultModule(requestContextImplementationClass,
                                                     websocketContextImplementationClass);
    }

    /**
     * Returns a module combining all the default 
     * plugins implementations.
     * <p>
     * The {@link SpincastCoreGuiceModule} module is not
     * included.
     * 
     * @param includeCoreModule Should the Core module being
     * added too?
     */
    public static Module getDefaultModule(Class<? extends RequestContext<?>> requestContextImplementationClass,
                                          Class<? extends WebsocketContext<?>> websocketContextImplementationClass,
                                          boolean includeCoreModule) {
        return SpincastBootstrapper.getDefaultModule(requestContextImplementationClass,
                                                     websocketContextImplementationClass,
                                                     includeCoreModule);
    }

}
