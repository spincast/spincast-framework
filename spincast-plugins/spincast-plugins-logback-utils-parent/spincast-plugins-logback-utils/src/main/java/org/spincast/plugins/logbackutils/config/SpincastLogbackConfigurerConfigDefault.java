package org.spincast.plugins.logbackutils.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastUtils;

import com.google.inject.Inject;


public class SpincastLogbackConfigurerConfigDefault implements SpincastLogbackConfigurerConfig {

    protected final static Logger logger = LoggerFactory.getLogger(SpincastLogbackConfigurerConfigDefault.class);

    private final SpincastUtils spincastUtils;

    @Inject
    public SpincastLogbackConfigurerConfigDefault(SpincastUtils spincastUtils) {
        this.spincastUtils = spincastUtils;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    @Override
    public ResourceInfo getResourceInfo() {

        //==========================================
        // We keep the Logback default which is
        // to configure it using a file called
        // "logback.xml" located on the classpath.
        //==========================================
        return new ResourceInfo("/logback.xml", true);
    }

    @Override
    public String tweakContent(String logbackContent) {

        //==========================================
        // No tweaking by default.
        //==========================================
        return logbackContent;
    }

}
