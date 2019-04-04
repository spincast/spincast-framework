package org.spincast.plugins.gson.tests.utils;

import java.util.List;
import java.util.Set;

import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.json.JsonManager;
import org.spincast.core.json.JsonObjectFactory;
import org.spincast.plugins.gson.SpincastGsonPlugin;
import org.spincast.plugins.jacksonjson.SpincastJacksonJsonPlugin;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public abstract class GsonTestBase extends NoAppTestingBase {

    @Inject
    protected JsonManager jsonManager;

    @Inject
    protected JsonObjectFactory jsonObjectFactory;

    protected JsonManager getJsonManager() {
        return this.jsonManager;
    }

    protected JsonObjectFactory getJsonObjectFactory() {
        return this.jsonObjectFactory;
    }

    /**
     * We disable the default SpincastJacksonJsonPlugin
     * plugin.
     */
    @Override
    protected Set<String> getGuiceTweakerPluginsToDisable() {
        Set<String> pluginIdsToIgnore = super.getGuiceTweakerPluginsToDisable();
        pluginIdsToIgnore.add(SpincastJacksonJsonPlugin.PLUGIN_ID);
        return pluginIdsToIgnore;
    }

    /**
     * We use the SpincastGsonPlugin plugin as the
     * implementation for JsonManager.
     */
    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastGsonPlugin());
        return extraPlugins;
    }

}
