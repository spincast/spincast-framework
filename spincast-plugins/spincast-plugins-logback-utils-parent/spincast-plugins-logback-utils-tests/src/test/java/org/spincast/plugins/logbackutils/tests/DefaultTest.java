package org.spincast.plugins.logbackutils.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.UUID;

import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.plugins.logbackutils.config.SpincastLogbackConfigurerConfig;

public class DefaultTest extends LogbackUtilsTestBase {

    //==========================================
    // NOTE!
    // We only run this test class during a release.
    // When ran from an IDE, the /logback.xml
    // classpath resource is not always to correct one
    // due to multiple projects potentially overlapping.
    //==========================================
    @Override
    public boolean isTestClassDisabledPreBeforeClass(Collection<FrameworkMethod> filteredTests) {
        String mavenProfileId = System.getProperty("mavenProfileId");
        return !"release".equals(mavenProfileId);
    }

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
        assertFalse(output, output.contains(uuid));
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
