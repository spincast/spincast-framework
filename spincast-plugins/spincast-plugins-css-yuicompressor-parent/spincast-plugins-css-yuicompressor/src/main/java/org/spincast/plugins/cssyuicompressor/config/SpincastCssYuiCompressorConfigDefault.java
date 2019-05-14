package org.spincast.plugins.cssyuicompressor.config;

import java.io.File;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

public class SpincastCssYuiCompressorConfigDefault implements SpincastCssYuiCompressorConfig {

    private final SpincastConfig spincastConfig;
    private File cssBundlesDir = null;

    @Inject
    public SpincastCssYuiCompressorConfigDefault(SpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    @Override
    public String getCssBundlePebbleFunctionName() {
        return "cssBundle";
    }

    @Override
    public String getCssBundlesUrlPath() {
        return "/spincast/plugins/cssyuicompressor/cssbundles";
    }

    @Override
    public File getCssBundlesDir() {
        if (this.cssBundlesDir == null || !this.cssBundlesDir.isDirectory()) {
            File dir = new File(getSpincastConfig().getWritableRootDir(), "spincast/plugins/cssyuicompressor/cssBundles");
            if (!dir.isDirectory()) {
                try {
                    FileUtils.forceMkdir(dir);
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }

            this.cssBundlesDir = dir;
        }
        return this.cssBundlesDir;
    }

    @Override
    public boolean isCssBundlesIgnoreSslCertificateErrors() {
        return getSpincastConfig().isDevelopmentMode() || getSpincastConfig().isTestingMode();
    }

}
