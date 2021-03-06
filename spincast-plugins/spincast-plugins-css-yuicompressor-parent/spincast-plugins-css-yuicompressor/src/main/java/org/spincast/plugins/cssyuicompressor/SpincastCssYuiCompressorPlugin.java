package org.spincast.plugins.cssyuicompressor;

import org.spincast.core.guice.SpincastPluginBase;
import org.spincast.plugins.httpclient.SpincastHttpClientPlugin;
import org.spincast.plugins.pebble.SpincastPebblePlugin;

import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Spincast CSS YUI Compressor plugin.
 */
public class SpincastCssYuiCompressorPlugin extends SpincastPluginBase {

    public static final String PLUGIN_ID = SpincastCssYuiCompressorPlugin.class.getName();

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
        return new SpincastCssYuiCompressorPluginModule();
    }

    protected Module applyRequiredPlugins(Module module) {

        SpincastHttpClientPlugin spincastHttpClientPlugin = new SpincastHttpClientPlugin();
        module = spincastHttpClientPlugin.apply(module);

        SpincastPebblePlugin spincastPebblePlugin = new SpincastPebblePlugin();
        module = spincastPebblePlugin.apply(module);

        return module;
    }



}
