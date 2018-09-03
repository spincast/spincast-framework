package org.spincast.plugins.scheduledtasks;

import org.spincast.core.guice.SpincastPluginBase;

import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Spincast Scheduled Tasks plugin.
 */
public class SpincastScheduledTasksPlugin extends SpincastPluginBase {

    public static final String PLUGIN_ID = SpincastScheduledTasksPlugin.class.getName();

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
        return new SpincastScheduledTasksPluginModule();
    }

}
