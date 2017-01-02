package org.spincast.plugins.routing;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.guice.GuiceModuleUtils;
import org.spincast.core.guice.SpincastPluginBase;
import org.spincast.core.routing.Router;

import com.google.inject.Module;
import com.google.inject.util.Modules;

public class SpincastRoutingPlugin extends SpincastPluginBase {

    protected final Logger logger = LoggerFactory.getLogger(SpincastRoutingPlugin.class);

    public static final String PLUGIN_ID = SpincastRoutingPlugin.class.getName();

    private Class<? extends Router<?, ?>> specificRouterImplementationClass;

    public SpincastRoutingPlugin() {
    }

    public SpincastRoutingPlugin(Class<? extends Router<?, ?>> specificRouterImplementationClass) {
        this.specificRouterImplementationClass = specificRouterImplementationClass;
    }

    protected Class<? extends Router<?, ?>> getSpecificRouterImplementationClass() {
        return this.specificRouterImplementationClass;
    }

    @Override
    public String getId() {
        return PLUGIN_ID;
    }

    @Override
    public Module apply(Module module) {

        Class<? extends Router<?, ?>> specificRouterImplementationClass = getSpecificRouterImplementationClass();

        //==========================================
        // Check if a custom SpincastRouter implementation
        // class must be used...
        //==========================================
        if(specificRouterImplementationClass == null) {

            GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module);

            @SuppressWarnings("rawtypes")
            Set<Class<? extends Router>> classes =
                    guiceModuleUtils.getBoundClassesExtending(Router.class);
            if(classes.size() > 0) {

                if(classes.size() > 1) {
                    String msg = "More than one custom implementations of " +
                                 Router.class.getName() + " " +
                                 "has been found. You'll have to pass the implementation " +
                                 "to use the constructor of this plugin to remove the " +
                                 "ambiguity about which one to use. Bindings found :\n";

                    for(@SuppressWarnings("rawtypes")
                    Class<? extends Router> clazz : classes) {
                        msg += "- " + clazz.getName() + "\n";
                    }
                    throw new RuntimeException(msg);
                }

                @SuppressWarnings("unchecked")
                Class<? extends Router<?, ?>> temp = (Class<? extends Router<?, ?>>)classes.iterator().next();
                specificRouterImplementationClass = temp;
            }
        }

        Module pluginModule = getPluginModule(specificRouterImplementationClass);
        setContextTypes(pluginModule);

        module = Modules.override(pluginModule).with(module);
        return module;
    }

    protected Module getPluginModule(Class<? extends Router<?, ?>> specificRouterImplementationClass) {
        return new SpincastRoutingPluginModule(specificRouterImplementationClass);
    }

}
