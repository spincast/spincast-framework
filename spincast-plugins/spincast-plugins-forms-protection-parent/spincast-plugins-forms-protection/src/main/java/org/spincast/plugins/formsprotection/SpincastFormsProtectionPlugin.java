package org.spincast.plugins.formsprotection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.guice.SpincastPluginBase;
import org.spincast.plugins.crons.SpincastCronsPlugin;
import org.spincast.plugins.crypto.SpincastCryptoPlugin;
import org.spincast.plugins.session.SpincastSessionPlugin;

import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Spincast Forms Protection plugin.
 */
public class SpincastFormsProtectionPlugin extends SpincastPluginBase {

    protected final static Logger logger = LoggerFactory.getLogger(SpincastFormsProtectionPlugin.class);

    public static final String PLUGIN_ID = SpincastFormsProtectionPlugin.class.getName();

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

        //==========================================
        // Then add our own Module
        //==========================================
        Module pluginModule = getPluginModule();
        setContextTypes(pluginModule);

        module = Modules.override(module).with(pluginModule);

        return module;
    }

    protected Module applyRequiredPlugins(Module module) {
        module = applySpincastCryptoPlugin(module);
        module = applySpincastCronsPlugin(module);
        module = applySpincastSessionPlugin(module);
        return module;
    }

    protected Module applySpincastCryptoPlugin(Module module) {
        SpincastCryptoPlugin spincastCryptoPlugin = new SpincastCryptoPlugin();
        return spincastCryptoPlugin.apply(module);
    }

    protected Module applySpincastCronsPlugin(Module module) {
        SpincastCronsPlugin spincastCronsPlugin = new SpincastCronsPlugin();
        return spincastCronsPlugin.apply(module);
    }

    protected Module applySpincastSessionPlugin(Module module) {
        SpincastSessionPlugin spincastSessionPlugin = new SpincastSessionPlugin();
        return spincastSessionPlugin.apply(module);
    }

    protected Module getPluginModule() {
        return new SpincastFormsProtectionPluginModule();
    }

}
