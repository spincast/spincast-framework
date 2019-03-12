package org.spincast.plugins.logbackutils.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.plugins.logbackutils.config.SpincastLogbackConfigurerConfig;

public class NoFileTest extends LogbackUtilsTestBase {

    protected final static Logger logger = LoggerFactory.getLogger(NoFileTest.class);

    @Override
    protected SpincastLogbackConfigurerConfig getSpincastLogbackConfigurerConfigImpl() {
        return new SpincastLogbackConfigurerConfig() {

            @Override
            public ResourceInfo getResourceInfo() {
                return null; // no file to start with!
            }

            @Override
            public String tweakContent(String logbackContent) {
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                       "<configuration>" +
                       "    <appender name=\"stdout\" class=\"ch.qos.logback.core.ConsoleAppender\">" +
                       "        <encoder>" +
                       "            <charset>UTF-8</charset>" +
                       "            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%level] %msg ~ %caller{1}</pattern>" +
                       "        </encoder>" +
                       "    </appender>" +
                       "    <root level=\"warn\">" +
                       "        <appender-ref ref=\"stdout\"/>" +
                       "    </root>" +
                       "</configuration>";
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
