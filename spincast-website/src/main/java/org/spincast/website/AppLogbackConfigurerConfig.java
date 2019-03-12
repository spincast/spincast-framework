package org.spincast.website;

import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.logbackutils.config.SpincastLogbackConfigurerConfigDefault;

import com.google.inject.Inject;

public class AppLogbackConfigurerConfig extends SpincastLogbackConfigurerConfigDefault {

    private final AppConfig appConfig;

    @Inject
    public AppLogbackConfigurerConfig(SpincastUtils spincastUtils, AppConfig appConfig) {
        super(spincastUtils);
        this.appConfig = appConfig;
    }

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    @Override
    public ResourceInfo getResourceInfo() {
        return new ResourceInfo("/conf/logback-config.xml", true);
    }

    @Override
    public String tweakContent(String logbackContent) {

        //==========================================
        // During development, we allow more logs from
        // "org.spincast" packages.
        //==========================================
        String spincastLevel = getAppConfig().isDevelopmentMode() ? "debug" : "warn";
        return logbackContent.replace("{{SPINCAST_LEVEL}}", spincastLevel);
    }

}
