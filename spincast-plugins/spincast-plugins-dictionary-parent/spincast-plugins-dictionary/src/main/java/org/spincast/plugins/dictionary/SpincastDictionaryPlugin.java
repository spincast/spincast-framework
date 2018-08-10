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

    private Class<? extends Dictionary> dictionaryImplClass;

    /**
     * Constructor
     */
    public SpincastDictionaryPlugin() {
    }

    /**
     * Constructor
     * 
     * @param dictionaryImplClass the specific
     * dictionary implementation class to use.
     */
    public SpincastDictionaryPlugin(Class<? extends Dictionary> dictionaryImplClass) {
        this.dictionaryImplClass = dictionaryImplClass;
    }

    @Override
    public String getId() {
        return PLUGIN_ID;
    }

    protected Class<? extends Dictionary> getDictionaryImplClass() {
        return this.dictionaryImplClass;
    }

    @Override
    public Module apply(Module module) {

        Class<? extends Dictionary> dictionaryImplClass = getDictionaryImplClass();

        //==========================================
        // Check if a custom SpincastDictionary implementation
        // class must be used...
        //==========================================
        if (dictionaryImplClass == null) {
            GuiceModuleUtils guiceModuleUtils = new GuiceModuleUtils(module);

            Set<Class<? extends Dictionary>> classes =
                    guiceModuleUtils.getBoundClassesExtending(Dictionary.class);
            if (classes.size() > 0) {

                if (classes.size() > 1) {
                    String msg = "More than one custom implementations of " +
                                 Dictionary.class.getName() + " " +
                                 "has been found. You'll have to pass the implementation " +
                                 "to use the constructor of this plugin to remove the " +
                                 "ambiguity about which one to use. Bindings found :\n";

                    for (Class<? extends Dictionary> clazz : classes) {
                        msg += "- " + clazz.getName() + "\n";
                    }
                    throw new RuntimeException(msg);
                }

                Class<? extends Dictionary> temp = (Class<? extends Dictionary>)classes.iterator().next();
                dictionaryImplClass = temp;
            }
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
