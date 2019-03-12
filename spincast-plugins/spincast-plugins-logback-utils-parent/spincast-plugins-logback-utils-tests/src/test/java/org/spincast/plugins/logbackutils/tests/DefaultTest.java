package org.spincast.plugins.logbackutils.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.plugins.logbackutils.config.SpincastLogbackConfigurerConfig;

public class DefaultTest extends LogbackUtilsTestBase {

    protected final static Logger logger = LoggerFactory.getLogger(DefaultTest.class);

    @Override
    protected SpincastLogbackConfigurerConfig getSpincastLogbackConfigurerConfigImpl() {
        return null;
    }

    @Test
    public void warn() {
        String uuid = UUID.randomUUID().toString();

        resetOutput();
        logger.warn(uuid);
        String output = getOutput();
        assertFalse(output.contains(uuid));
    }

    @Test
    public void error() {
        String uuid = UUID.randomUUID().toString();

        resetOutput();
        logger.error(uuid);
        String output = getOutput();
        assertTrue(output.contains(uuid));
    }

}
