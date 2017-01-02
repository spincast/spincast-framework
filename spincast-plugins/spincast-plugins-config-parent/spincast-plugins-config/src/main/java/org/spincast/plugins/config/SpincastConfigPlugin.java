package org.spincast.plugins.config;

import java.util.Set;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.GuiceModuleUtils;
import org.spincast.core.guice.SpincastPluginBase;

import com.google.inject.Module;
import com.google.inject.util.Modules;

public class SpincastConfigPlugin extends SpincastPluginBase {

    public static final String PLUGIN_ID = SpincastConfigPlugin.class.getName();

    private Class<? extends SpincastConfig> specificSpincastConfigImplClass;

    /**
     * Constructor
     */
    public SpincastConfigPlugin() {
    }

    /**
     * Constructor
     * 
     * @param specificSpincastConfigImplClass the specific
     * configuration implementation class to use.
     */
    public SpincastConfigPlugin(Class<? extends SpincastConfig> specificSpincastConfigImplClass) {
        this.specificSpincastConfigImplClass = specificSpincastConfigImplClass;
    }

    @Override
    public String getId() {
        return PLUGIN_ID;
    }

    protected Class<? extends SpincastConfig> getSpecificSpincastConfigImplClass() {
        return this.specificSpincastConfigImplClass;
    }

    @Override
    public Module apply(Module module) {

        Class<? extends SpincastConfig> specificSpincastConfigImplClass = getSpecificSpincastConfigImplClass();

        //==========================================
        // Check if a custom SpincastConfig implementation
        // class must be used...
        //==========================================
        if(specificSpincastConfigImplClass == null) {
            GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module);

            Set<Class<? extends SpincastConfig>> classes =
                    guiceModuleUtils.getBoundClassesExtending(SpincastConfig.class);
            if(classes.size() > 0) {

                if(classes.size() > 1) {
                    String msg = "More than one custom implementations of " +
                                 SpincastConfig.class.getName() + " " +
                                 "has been found. You'll have to pass the implementation " +
                                 "to use the constructor of this plugin to remove the " +
                                 "ambiguity about which one to use. Bindings found :\n";

                    for(Class<? extends SpincastConfig> clazz : classes) {
                        msg += "- " + clazz.getName() + "\n";
                    }
                    throw new RuntimeException(msg);
                }

                Class<? extends SpincastConfig> temp = (Class<? extends SpincastConfig>)classes.iterator().next();
                specificSpincastConfigImplClass = temp;
            }
        }

        Module pluginModule = getPluginModule(specificSpincastConfigImplClass);
        setContextTypes(pluginModule);

        module = Modules.override(pluginModule).with(module);
        return module;
    }

    protected Module getPluginModule(Class<? extends SpincastConfig> specificSpincastConfigImplClass) {
        return new SpincastConfigPluginModule(specificSpincastConfigImplClass);
    }

}
