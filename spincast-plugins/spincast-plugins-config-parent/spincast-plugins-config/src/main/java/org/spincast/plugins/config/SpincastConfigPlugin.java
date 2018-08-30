package org.spincast.plugins.config;

import java.util.Set;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.guice.GuiceModuleUtils;
import org.spincast.core.guice.SpincastPluginBase;

import com.google.inject.Module;
import com.google.inject.util.Modules;

public class SpincastConfigPlugin extends SpincastPluginBase {

    public static final String PLUGIN_ID = SpincastConfigPlugin.class.getName();

    @Override
    public String getId() {
        return PLUGIN_ID;
    }

    @Override
    public Module apply(Module module) {

        Class<? extends SpincastConfig> specificConfigImplClass = null;

        //==========================================
        // Check if a custom SpincastConfig implementation
        // class must be used...
        //==========================================
        GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module);

        Set<Class<? extends SpincastConfig>> classes =
                guiceModuleUtils.getBoundClassesExtending(SpincastConfig.class);
        if (classes.size() > 0) {

            if (classes.size() > 1) {
                String msg = "More than one custom implementations of " +
                             SpincastConfig.class.getName() + " " +
                             "has been found. Bindings found :\n";

                for (Class<? extends SpincastConfig> clazz : classes) {
                    msg += "- " + clazz.getName() + "\n";
                }
                throw new RuntimeException(msg);
            }

            Class<? extends SpincastConfig> temp = (Class<? extends SpincastConfig>)classes.iterator().next();
            specificConfigImplClass = temp;
        }

        Module pluginModule = getPluginModule(specificConfigImplClass);
        setContextTypes(pluginModule);

        module = Modules.override(module).with(pluginModule);
        return module;
    }

    protected Module getPluginModule(Class<? extends SpincastConfig> specificConfigImplClass) {
        return new SpincastConfigPluginModule(specificConfigImplClass);
    }

}
