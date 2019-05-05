package org.spincast.plugins.cssautoprefixer;

import org.spincast.core.guice.SpincastPluginBase;
import org.spincast.plugins.processutils.SpincastProcessUtilsPlugin;

import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Spincast CSS Autoprefixer plugin.
 */
public class SpincastCssAutoprefixerPlugin extends SpincastPluginBase {

    public static final String PLUGIN_ID = SpincastCssAutoprefixerPlugin.class.getName();

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
        return new SpincastCssAutoprefixerPluginModule();
    }

    protected Module applyRequiredPlugins(Module module) {
        module = applySpincastProcessUtilsPlugin(module);
        return module;
    }

    protected Module applySpincastProcessUtilsPlugin(Module module) {
        SpincastProcessUtilsPlugin spincastProcessUtilsPlugin = new SpincastProcessUtilsPlugin();
        return spincastProcessUtilsPlugin.apply(module);
    }

}
