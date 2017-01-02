package org.spincast.core.guice;

import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Spincast core plugin
 */
public class SpincastCorePlugin extends SpincastPluginBase {

    public static final String PLUGIN_ID = SpincastCorePlugin.class.getName();

    @Override
    public String getId() {
        return PLUGIN_ID;
    }

    @Override
    public Module apply(Module module) {

        Module pluginModule = getPluginModule();
        setContextTypes(pluginModule);

        module = Modules.override(pluginModule).with(module);
        return module;
    }

    protected Module getPluginModule() {
        return new SpincastCorePluginModule();
    }

}
