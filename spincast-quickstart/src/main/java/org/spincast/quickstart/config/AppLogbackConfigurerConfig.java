package org.spincast.quickstart.config;

import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.logbackutils.config.SpincastLogbackConfigurerConfigDefault;

import com.google.inject.Inject;

/**
 * Configure Logback
 */
public class AppLogbackConfigurerConfig extends SpincastLogbackConfigurerConfigDefault {

    private final AppConfig appConfig;
    private final TemplatingEngine templatingEngine;

    @Inject
    public AppLogbackConfigurerConfig(SpincastUtils spincastUtils, AppConfig appConfig, TemplatingEngine templatingEngine) {
        super(spincastUtils);
        this.appConfig = appConfig;
        this.templatingEngine = templatingEngine;
    }

    protected AppConfig getAppConfig() {
        return this.appConfig;
    }

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    @Override
    public ResourceInfo getResourceInfo() {
        return new ResourceInfo("/quick-start/logback-config.xml", true);
    }

    @Override
    public String tweakContent(String logbackContent) {

        //==========================================
        // During development, we allow more logs from
        // the root app packages.
        //==========================================
        String appRootPackage = getAppConfig().getAppRootPackage();
        String defaultLevel = getAppConfig().isDevelopmentMode() ? "warn" : "error";
        String appLevel = getAppConfig().isDevelopmentMode() ? "debug" : "warn";

        String logbackContentTweaked = getTemplatingEngine().evaluate(logbackContent,
                                                                      SpincastStatics.params("APP_ROOT_PACKAGE",
                                                                                             appRootPackage,
                                                                                             "DEFAULT_LEVEL",
                                                                                             defaultLevel,
                                                                                             "APP_LEVEL",
                                                                                             appLevel));
        return logbackContentTweaked;
    }

}
