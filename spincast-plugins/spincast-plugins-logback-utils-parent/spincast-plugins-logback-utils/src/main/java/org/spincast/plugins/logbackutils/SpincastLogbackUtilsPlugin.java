package org.spincast.plugins.logbackutils;

import org.spincast.core.guice.SpincastPluginBase;

import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Spincast Logback Utils plugin.
 */
public class SpincastLogbackUtilsPlugin extends SpincastPluginBase {

    public static final String PLUGIN_ID = SpincastLogbackUtilsPlugin.class.getName();

    @Override
    public String getId() {
        return PLUGIN_ID;
    }

    @Override
    public Module apply(Module module) {

        //==========================================
        // We make sure all the non-default plugins
        // we depend on are applied...
        //==========================================
        module = applyRequiredPlugins(module);

        Module pluginModule = getPluginModule();
        setContextTypes(pluginModule);

        module = Modules.override(module).with(pluginModule);
        return module;
    }

    protected Module getPluginModule() {
        return new SpincastLogbackUtilsPluginModule();
    }

    protected Module applyRequiredPlugins(Module module) {
        // none...
        return module;
    }


}
