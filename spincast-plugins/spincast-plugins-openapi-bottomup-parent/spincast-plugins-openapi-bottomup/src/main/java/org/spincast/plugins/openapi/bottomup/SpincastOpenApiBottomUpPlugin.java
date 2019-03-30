package org.spincast.plugins.openapi.bottomup;

import org.spincast.core.guice.SpincastPluginBase;

import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Spincast Open API BottomUp plugin.
 */
public class SpincastOpenApiBottomUpPlugin extends SpincastPluginBase {

    public static final String PLUGIN_ID = SpincastOpenApiBottomUpPlugin.class.getName();

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
        return new SpincastOpenApiBottomUpPluginModule();
    }

    protected Module applyRequiredPlugins(Module module) {
        // none...
        return module;
    }


}
