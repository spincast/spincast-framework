package org.spincast.plugins.flywayutils;

import org.spincast.core.guice.SpincastPluginBase;
import org.spincast.plugins.jdbc.SpincastJdbcPlugin;

import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Spincast Flyway Utils plugin.
 */
public class SpincastFlywayUtilsPlugin extends SpincastPluginBase {

    public static final String PLUGIN_ID = SpincastFlywayUtilsPlugin.class.getName();

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
        return new SpincastFlywayUtilsPluginModule();
    }

    protected Module applyRequiredPlugins(Module module) {
        module = applySpincastJdbcPlugin(module);
        return module;
    }

    protected Module applySpincastJdbcPlugin(Module module) {
        SpincastJdbcPlugin plugin = new SpincastJdbcPlugin();
        return plugin.apply(module);
    }

}
