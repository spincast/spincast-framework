package org.spincast.plugins.logbackutils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.logbackutils.config.SpincastLogbackConfigurerConfig;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.io.IOUtils;

import com.google.inject.Inject;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

/**
 * Component to configure Logback.
 */
public class SpincastLogbackConfigurer {

    protected final static Logger logger = LoggerFactory.getLogger(SpincastLogbackConfigurer.class);

    private final SpincastLogbackConfigurerConfig spincastLogbackConfigurerConfig;
    private final SpincastUtils spincastUtils;

    @Inject
    public SpincastLogbackConfigurer(SpincastLogbackConfigurerConfig spincastLogbackConfigurerConfig,
                                     SpincastUtils spincastUtils) {
        this.spincastLogbackConfigurerConfig = spincastLogbackConfigurerConfig;
        this.spincastUtils = spincastUtils;
    }

    @Inject
    protected void init() {
        configure();
    }

    protected SpincastLogbackConfigurerConfig getSpincastLogbackConfigurerConfig() {
        return this.spincastLogbackConfigurerConfig;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected void configure() {

        try {

            //==========================================
            // Get the initial Logback file content
            //==========================================
            ResourceInfo logbackFileInfo = getSpincastLogbackConfigurerConfig().getResourceInfo();

            String content;
            if (logbackFileInfo == null) {
                content = "";
            } else {
                if (logbackFileInfo.isClasspathResource()) {
                    InputStream is = getSpincastUtils().getClasspathInputStream(logbackFileInfo.getPath());
                    try {
                        if (is == null) {
                            throw new RuntimeException("The Logback file \"" + logbackFileInfo.getPath() +
                                                       "\" was not found on the classpath!");
                        }

                        content = IOUtils.toString(is, getLogbackFileEncoding());

                    } finally {
                        SpincastStatics.closeQuietly(is);
                    }
                } else {
                    File file = new File(logbackFileInfo.getPath());
                    if (!file.isFile()) {
                        throw new RuntimeException("The Logback file \"" + logbackFileInfo.getPath() +
                                                   "\" was not found on the file system!");
                    }

                    content = FileUtils.readFileToString(file, getLogbackFileEncoding());
                }
            }

            //==========================================
            // Tweak the content, if required...
            //==========================================
            String contentFinal = getSpincastLogbackConfigurerConfig().tweakContent(content);

            //==========================================
            // Configure Logback
            //==========================================
            if (logbackFileInfo != null) {
                logger.info("Logback logger configurations changed to those provided in file \"" + logbackFileInfo.getPath() +
                            (logbackFileInfo.isClasspathResource() ? "\" on the classpath." : " on the file system."));
            }
            if (!content.equals(contentFinal)) {
                logger.info("Logback logger configurations tweaked using SpincastLogbackConfigurerConfig#tweakContent(...)");
            }

            LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();

            InputStream stringInputStream = IOUtils.toInputStream(contentFinal, getLogbackFileEncoding());
            try {

                configurator.doConfigure(stringInputStream);
            } finally {
                SpincastStatics.closeQuietly(stringInputStream);
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected Charset getLogbackFileEncoding() {
        return StandardCharsets.UTF_8;
    }
}
