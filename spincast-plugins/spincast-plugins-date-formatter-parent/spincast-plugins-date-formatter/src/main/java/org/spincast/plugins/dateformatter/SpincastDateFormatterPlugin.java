package org.spincast.plugins.dateformatter;

import org.spincast.core.guice.SpincastPluginBase;

import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Spincast Date Formatter plugin.
 */
public class SpincastDateFormatterPlugin extends SpincastPluginBase {

    public static final String PLUGIN_ID = SpincastDateFormatterPlugin.class.getName();

    @Override
    public String getId() {
        return PLUGIN_ID;
    }

    @Override
    public Module apply(Module module) {

        Module pluginModule = getPluginModule();
        setContextTypes(pluginModule);

        module = Modules.override(module).with(pluginModule);
        return module;
    }

    protected Module getPluginModule() {
        return new SpincastDateFormatterPluginModule();
    }

}
