package org.spincast.plugins.attemptslimiter;

import org.spincast.core.guice.SpincastPluginBase;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTasksPlugin;

import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Spincast Attempts Limiter plugin.
 */
public class SpincastAttemptsLimiterPlugin extends SpincastPluginBase {

    public static final String PLUGIN_ID = SpincastAttemptsLimiterPlugin.class.getName();

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

    protected Module applyRequiredPlugins(Module module) {
        module = applySpincastScheduledTasksPlugin(module);
        return module;
    }

    protected Module applySpincastScheduledTasksPlugin(Module module) {
        SpincastScheduledTasksPlugin spincastScheduledTasksPlugin = new SpincastScheduledTasksPlugin();
        return spincastScheduledTasksPlugin.apply(module);
    }

    protected Module getPluginModule() {
        return new SpincastAttemptsLimiterPluginModule();
    }

}
