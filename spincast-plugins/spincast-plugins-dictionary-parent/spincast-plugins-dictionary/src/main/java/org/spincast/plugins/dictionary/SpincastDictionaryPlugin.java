package org.spincast.plugins.dictionary;

import java.util.Set;

import org.spincast.core.dictionary.Dictionary;
import org.spincast.core.guice.GuiceModuleUtils;
import org.spincast.core.guice.SpincastPluginBase;

import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Spincast Dictionary plugin.
 */
public class SpincastDictionaryPlugin extends SpincastPluginBase {

    public static final String PLUGIN_ID = SpincastDictionaryPlugin.class.getName();

    @Override
    public String getId() {
        return PLUGIN_ID;
    }

    @Override
    public Module apply(Module module) {

        Class<? extends Dictionary> dictionaryImplClass = null;

        //==========================================
        // Check if a custom SpincastDictionary implementation
        // class must be used...
        //==========================================
        GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module);

        Set<Class<? extends Dictionary>> classes =
                guiceModuleUtils.getBoundClassesExtending(Dictionary.class);
        if (classes.size() > 0) {

            if (classes.size() > 1) {
                String msg = "More than one custom implementations of " +
                             Dictionary.class.getName() + " " +
                             "has been found. Bindings found :\n";

                for (Class<? extends Dictionary> clazz : classes) {
                    msg += "- " + clazz.getName() + "\n";
                }
                throw new RuntimeException(msg);
            }

            Class<? extends Dictionary> temp = (Class<? extends Dictionary>)classes.iterator().next();
            dictionaryImplClass = temp;
        }

        Module pluginModule = getPluginModule(dictionaryImplClass);
        setContextTypes(pluginModule);

        module = Modules.override(module).with(pluginModule);
        return module;
    }

    protected Module getPluginModule(Class<? extends Dictionary> dictionaryImplClass) {
        return new SpincastDictionaryPluginModule(dictionaryImplClass);
    }

}
