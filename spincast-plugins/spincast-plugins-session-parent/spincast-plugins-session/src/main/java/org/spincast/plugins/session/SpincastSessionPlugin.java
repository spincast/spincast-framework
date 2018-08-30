package org.spincast.plugins.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.guice.SpincastPluginBase;
import org.spincast.plugins.crons.SpincastCronsPlugin;

import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Spincast Session plugin.
 */
public class SpincastSessionPlugin extends SpincastPluginBase {

    protected final static Logger logger = LoggerFactory.getLogger(SpincastSessionPlugin.class);

    public static final String PLUGIN_ID = SpincastSessionPlugin.class.getName();

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
        module = applySpincastCronsPlugin(module);
        return module;
    }

    protected Module applySpincastCronsPlugin(Module module) {
        SpincastCronsPlugin spincastCronsPlugin = new SpincastCronsPlugin();
        return spincastCronsPlugin.apply(module);
    }

    protected Module getPluginModule() {
        return new SpincastSessionPluginModule();
    }

}
