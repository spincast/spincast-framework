package org.spincast.plugins.jsclosurecompiler.config;

import java.io.File;

import org.spincast.core.config.SpincastConfig;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

public class SpincastJsClosureCompilerConfigDefault implements SpincastJsClosureCompilerConfig {

    private final SpincastConfig spincastConfig;
    private File jsBundlesDir = null;

    @Inject
    public SpincastJsClosureCompilerConfigDefault(SpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    @Override
    public String getJavaBinPath() {
        return "java";
    }

    @Override
    public String getJsBundlePebbleFunctionName() {
        return "jsBundle";
    }

    @Override
    public String getJsBundlesUrlPath() {
        return "/spincast/plugins/jsclosurecompiler/jsbundles";
    }

    @Override
    public File getJsBundlesDir() {
        if (this.jsBundlesDir == null || !this.jsBundlesDir.isDirectory()) {
            File dir = new File(getSpincastConfig().getWritableRootDir(), "spincast/plugins/jsclosurecompiler/jsBundles");
            if (!dir.isDirectory()) {
                try {
                    FileUtils.forceMkdir(dir);
                } catch (Exception ex) {
                    throw SpincastStatics.runtimize(ex);
                }
            }

            this.jsBundlesDir = dir;
        }
        return this.jsBundlesDir;
    }

    @Override
    public boolean isJsBundlesIgnoreSslCertificateErrors() {
        return getSpincastConfig().isDevelopmentMode() || getSpincastConfig().isTestingMode();
    }

}
