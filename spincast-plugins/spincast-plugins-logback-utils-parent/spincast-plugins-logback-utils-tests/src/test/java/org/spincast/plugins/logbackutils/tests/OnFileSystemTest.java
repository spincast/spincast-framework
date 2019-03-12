package org.spincast.plugins.logbackutils.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.logbackutils.config.SpincastLogbackConfigurerConfig;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

public class OnFileSystemTest extends LogbackUtilsTestBase {

    protected final static Logger logger = LoggerFactory.getLogger(OnFileSystemTest.class);

    protected String filePath;

    @Override
    public void beforeClass() {

        this.filePath = createTestingFilePath();
        InputStream is = this.getClass().getResourceAsStream("/config/1.xml");
        try {
            FileUtils.copyInputStreamToFile(is, new File(this.filePath));
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        } finally {
            SpincastStatics.closeQuietly(is);
        }

        super.beforeClass();
    }

    @Override
    protected SpincastLogbackConfigurerConfig getSpincastLogbackConfigurerConfigImpl() {
        return new SpincastLogbackConfigurerConfig() {

            @Override
            public ResourceInfo getResourceInfo() {
                return new ResourceInfo(OnFileSystemTest.this.filePath, false);
            }

            @Override
            public String tweakContent(String logbackContent) {
                return logbackContent.replace("{{LEVEL}}", "info");
            }
        };
    }

    @Test
    public void debug() {
        String uuid = UUID.randomUUID().toString();

        resetOutput();
        logger.debug(uuid);
        String output = getOutput();
        assertFalse(output.contains(uuid));
    }

    @Test
    public void info() {
        String uuid = UUID.randomUUID().toString();

        resetOutput();
        logger.info(uuid);
        String output = getOutput();
        assertTrue(output.contains(uuid));
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
