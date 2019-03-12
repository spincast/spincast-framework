package org.spincast.plugins.logbackutils.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.plugins.logbackutils.config.SpincastLogbackConfigurerConfig;

public class OnClasspathTest extends LogbackUtilsTestBase {

    protected final static Logger logger = LoggerFactory.getLogger(OnClasspathTest.class);

    @Override
    protected SpincastLogbackConfigurerConfig getSpincastLogbackConfigurerConfigImpl() {
        return new SpincastLogbackConfigurerConfig() {

            @Override
            public ResourceInfo getResourceInfo() {
                return new ResourceInfo("/config/1.xml", true);
            }

            @Override
            public String tweakContent(String logbackContent) {
                return logbackContent.replace("{{LEVEL}}", "warn");
            }
        };
    }

    @Test
    public void info() {
        String uuid = UUID.randomUUID().toString();

        resetOutput();
        logger.info(uuid);
        String output = getOutput();
        assertFalse(output.contains(uuid));
    }

    @Test
    public void warn() {
        String uuid = UUID.randomUUID().toString();

        resetOutput();
        logger.warn(uuid);
        String output = getOutput();
        assertTrue(output.contains(uuid));
    }

}
