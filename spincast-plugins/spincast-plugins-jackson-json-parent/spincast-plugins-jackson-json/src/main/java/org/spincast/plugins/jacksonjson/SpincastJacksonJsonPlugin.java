package org.spincast.plugins.jacksonjson;

import org.spincast.core.guice.SpincastPluginBase;

import com.google.inject.Module;
import com.google.inject.util.Modules;

public class SpincastJacksonJsonPlugin extends SpincastPluginBase {

    public static final String PLUGIN_ID = SpincastJacksonJsonPlugin.class.getName();

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
        return new SpincastJacksonJsonPluginModule();
    }

}
