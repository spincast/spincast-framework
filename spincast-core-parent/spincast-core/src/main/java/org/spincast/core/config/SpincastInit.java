package org.spincast.core.config;

import java.io.File;

import org.spincast.core.utils.SpincastStatics;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

/**
 * Some basci initialization
 */
public class SpincastInit {

    private final SpincastConfig spincastConfig;

    @Inject
    public SpincastInit(SpincastConfig spincastConfig) {
        this.spincastConfig = spincastConfig;
    }

    @Inject
    public void init() {

        writableDirsCheck();

    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected void writableDirsCheck() {

        try {
            File writableRootDir = getSpincastConfig().getWritableRootDir();
            if (!writableRootDir.isDirectory() || !writableRootDir.canWrite()) {
                throw new RuntimeException("A writable directory is required. Invalid : " + writableRootDir.getAbsolutePath());
            }

            File tempDir = getSpincastConfig().getTempDir();
            if (!tempDir.isDirectory()) {
                boolean result = tempDir.mkdirs();
                if (!result) {
                    throw new RuntimeException("Unable to create the temp writable folder : " + tempDir.getAbsolutePath());
                }
            } else {
                FileUtils.cleanDirectory(tempDir);
            }

            if (!tempDir.canWrite()) {
                throw new RuntimeException("A writable temp directory is required. Invalid : " + tempDir.getAbsolutePath());
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

}
