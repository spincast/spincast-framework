package org.spincast.plugins.configpropsfile;

import org.spincast.core.config.SpincastConfig;
import org.spincast.plugins.config.SpincastConfigPlugin;

import com.google.inject.Module;

public class SpincastConfigPropsFilePlugin extends SpincastConfigPlugin {

    public static final String PLUGIN_ID = SpincastConfigPropsFilePlugin.class.getName();

    /**
     * Constructor
     */
    public SpincastConfigPropsFilePlugin() {
    }

    /**
     * Constructor
     * 
     * @param specificSpincastConfigImplClass the specific
     * configuration implementation class to use.
     */
    public SpincastConfigPropsFilePlugin(Class<? extends SpincastConfig> specificSpincastConfigImplClass) {
        super(specificSpincastConfigImplClass);
    }

    @Override
    public String getId() {
        return PLUGIN_ID;
    }

    /**
     * Uses a custom plugin module.
     */
    @Override
    protected Module getPluginModule(Class<? extends SpincastConfig> specificSpincastConfigImplClass) {
        return new SpincastConfigPropsFilePluginModule(specificSpincastConfigImplClass);
    }

}
