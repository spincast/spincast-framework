package org.spincast.plugins.openapi.bottomup.config;

import org.spincast.core.utils.ContentTypeDefaults;

public class SpincastOpenApiBottomUpPluginConfigDefault implements SpincastOpenApiBottomUpPluginConfig {

    @Override
    public String[] getDefaultConsumesContentTypes() {
        return new String[]{ContentTypeDefaults.JSON.getMainVariation()};
    }

    @Override
    public String[] getDefaultProducesContentTypes() {
        return new String[]{ContentTypeDefaults.JSON.getMainVariation()};
    }

    @Override
    public boolean isDisableAutoSpecs() {
        return false;
    }

}
