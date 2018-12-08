package org.spincast.plugins.hotswap.classeswatcher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import org.hotswap.agent.annotation.Init;
import org.hotswap.agent.annotation.LoadEvent;
import org.hotswap.agent.annotation.OnClassLoadEvent;
import org.hotswap.agent.annotation.Plugin;
import org.hotswap.agent.command.Command;
import org.hotswap.agent.command.Scheduler;
import org.hotswap.agent.javassist.CannotCompileException;
import org.hotswap.agent.javassist.CtClass;
import org.hotswap.agent.javassist.NotFoundException;
import org.hotswap.agent.util.PluginManagerInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;

import com.google.inject.Inject;

/**
 * The instance of this class will be created by
 * HotSwap Agent.
 * <p>
 * We bind it to the Guice context using 
 * <code>toInstance()</code> and this will inject 
 * the required dependencies on members 
 * ("@Inject" annotated), directly. 
 */
@Plugin(name = "HotSwapClassesRedefinitionsWatcher", testedVersions = "")
public class HotSwapClassesRedefinitionsWatcherDefault implements HotSwapClassesRedefinitionsWatcher {

    protected final static Logger logger = LoggerFactory.getLogger(HotSwapClassesRedefinitionsWatcherDefault.class);
    protected final static String THIS_CLASS_NAME =
            "org.spincast.plugins.hotswap.classeswatcher.HotSwapClassesRedefinitionsWatcherDefault";
    private boolean guiceInjected;

    private Map<Class<?>, Set<HotSwapClassesRedefinitionsListener>> listenersByClassToWatch;

    /**
     * Flag to know that dependencies
     * have been injected by Guice.
     */
    @Inject
    public void guiceInjected() {
        this.guiceInjected = true;
    }

    protected boolean isGuiceInjected() {
        return this.guiceInjected;
    }

    /**
     * This will be called by HotSwap Agent to inject
     * the Scheduler instance. 
     */
    @Init
    protected Scheduler scheduler;

    protected Scheduler getScheduler() {
        return this.scheduler;
    }

    @Inject
    private SpincastConfig spincastConfig;

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    /**
     * Guice injects the bound listeners
     */
    @Inject
    public void setBoundListerns(@Nullable Set<HotSwapClassesRedefinitionsListener> boundListeners) {
        if (boundListeners != null) {
            for (HotSwapClassesRedefinitionsListener listener : boundListeners) {
                if (listener.isEnabled()) {
                    registerListener(listener);
                }
            }
        }
    }

    protected Map<Class<?>, Set<HotSwapClassesRedefinitionsListener>> getListenersByClassToWatch() {
        if (this.listenersByClassToWatch == null) {
            this.listenersByClassToWatch = new HashMap<Class<?>, Set<HotSwapClassesRedefinitionsListener>>();
        }
        return this.listenersByClassToWatch;
    }

    //==========================================
    // Inits the plugin.
    // This is required and has to be a static method...
    //==========================================
    @OnClassLoadEvent(classNameRegexp = THIS_CLASS_NAME, events = LoadEvent.DEFINE)
    public static void init(CtClass ctClass) throws NotFoundException, CannotCompileException {

        String src = PluginManagerInvoker.buildInitializePlugin(HotSwapClassesRedefinitionsWatcherDefault.class);

        //==========================================
        // All methods of the plugin must be registered here...
        // We only have one, "classRedefined"
        //==========================================
        src += PluginManagerInvoker.buildCallPluginMethod(HotSwapClassesRedefinitionsWatcherDefault.class, "classRedefined");
        ctClass.getDeclaredConstructor(new CtClass[0]).insertAfter(src);

        logger.info(HotSwapClassesRedefinitionsWatcherDefault.class.getSimpleName() + " is now initialized...");
    }

    @Override
    public void registerListener(HotSwapClassesRedefinitionsListener listener) {
        Objects.requireNonNull(listener, "The listener can't be NULL");

        Set<Class<?>> classesToWatch = listener.getClassesToWatch();
        if (classesToWatch == null) {
            return;
        }

        for (Class<?> classToWatch : classesToWatch) {
            Set<HotSwapClassesRedefinitionsListener> listeners = getListenersByClassToWatch().get(classToWatch);
            if (listeners == null) {
                listeners = new HashSet<HotSwapClassesRedefinitionsListener>();
                getListenersByClassToWatch().put(classToWatch, listeners);
            }
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(HotSwapClassesRedefinitionsListener listener) {
        Objects.requireNonNull(listener, "The listener can't be NULL");

        Set<Class<?>> classesToWatch = listener.getClassesToWatch();
        if (classesToWatch == null) {
            return;
        }

        for (Class<?> classToWatch : classesToWatch) {
            Set<HotSwapClassesRedefinitionsListener> listeners = getListenersByClassToWatch().get(classToWatch);
            if (listeners != null) {
                listeners.remove(listener);
                if (listeners.size() == 0) {
                    getListenersByClassToWatch().remove(classToWatch);
                }
            }
        }
    }

    @Override
    public void removeAllListeners() {
        getListenersByClassToWatch().clear();
    }

    /**
     * Listen to redefinitions of any class.
     */
    @OnClassLoadEvent(classNameRegexp = ".+", events = LoadEvent.REDEFINE)
    public void classRedefined(Class<?> classBeingRedefined) {

        if (!isGuiceInjected()) {
            logger.warn("Required dependencies still not injected by Guice, returning...");
            return;
        }

        Set<HotSwapClassesRedefinitionsListener> listeners = getListenersByClassToWatch().get(classBeingRedefined);
        if (listeners == null || listeners.size() == 0) {
            logger.info("Class \"" + classBeingRedefined.getName() + "\" redefined, no listeners.");
            return;
        }

        logger.info("Class \"" + classBeingRedefined.getName() + "\" redefined, " + listeners.size() + " listeners.");

        for (HotSwapClassesRedefinitionsListener listener : listeners) {

            //==========================================
            // Listener enabled?
            //==========================================
            if (!listener.isEnabled()) {
                continue;
            }

            //==========================================
            // The "@OnClassLoadEvent" method is
            // called *before* the class is actually reloaded.
            // By using the agent's scheduler, we can set 
            // an action to be run *after* the class has been
            // redefined.
            //==========================================
            getScheduler().scheduleCommand(new Command() {

                @Override
                public void executeCommand() {

                    Thread listenerThread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            listener.classRedefined(classBeingRedefined);
                        }
                    });
                    listenerThread.start();
                }
            });
        }
    }
}
