package org.spincast.plugins.dictionary;

import java.util.Set;

import org.spincast.core.config.SpincastDictionary;
import org.spincast.core.guice.GuiceModuleUtils;
import org.spincast.core.guice.SpincastPluginBase;

import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Spincast Dictionary plugin.
 */
public class SpincastDictionaryPlugin extends SpincastPluginBase {

    public static final String PLUGIN_ID = SpincastDictionaryPlugin.class.getName();

    private Class<? extends SpincastDictionary> specificSpincastDictionaryImplClass;

    /**
     * Constructor
     */
    public SpincastDictionaryPlugin() {
    }

    /**
     * Constructor
     * 
     * @param specificSpincastDictionaryImplClass the specific
     * dictionary implementation class to use.
     */
    public SpincastDictionaryPlugin(Class<? extends SpincastDictionary> specificSpincastDictionaryImplClass) {
        this.specificSpincastDictionaryImplClass = specificSpincastDictionaryImplClass;
    }

    @Override
    public String getId() {
        return PLUGIN_ID;
    }

    protected Class<? extends SpincastDictionary> getSpecificSpincastDictionaryImplClass() {
        return this.specificSpincastDictionaryImplClass;
    }

    @Override
    public Module apply(Module module) {

        Class<? extends SpincastDictionary> specificSpincastDictionaryImplClass = getSpecificSpincastDictionaryImplClass();

        //==========================================
        // Check if a custom SpincastDictionary implementation
        // class must be used...
        //==========================================
        if (specificSpincastDictionaryImplClass == null) {
            GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module);

            Set<Class<? extends SpincastDictionary>> classes =
                    guiceModuleUtils.getBoundClassesExtending(SpincastDictionary.class);
            if (classes.size() > 0) {

                if (classes.size() > 1) {
                    String msg = "More than one custom implementations of " +
                                 SpincastDictionary.class.getName() + " " +
                                 "has been found. You'll have to pass the implementation " +
                                 "to use the constructor of this plugin to remove the " +
                                 "ambiguity about which one to use. Bindings found :\n";

                    for (Class<? extends SpincastDictionary> clazz : classes) {
                        msg += "- " + clazz.getName() + "\n";
                    }
                    throw new RuntimeException(msg);
                }

                Class<? extends SpincastDictionary> temp = (Class<? extends SpincastDictionary>)classes.iterator().next();
                specificSpincastDictionaryImplClass = temp;
            }
        }

        Module pluginModule = getPluginModule(specificSpincastDictionaryImplClass);
        setContextTypes(pluginModule);

        module = Modules.override(module).with(pluginModule);
        return module;
    }

    protected Module getPluginModule(Class<? extends SpincastDictionary> specificSpincastDictionaryImplClass) {
        return new SpincastDictionaryPluginModule(specificSpincastDictionaryImplClass);
    }

}
