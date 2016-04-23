package org.spincast.plugins.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.spincast.core.config.ISpincastConfig;

public class SpincastConfig implements ISpincastConfig {

    private File spincastDir;

    @Override
    public String getEnvironmentName() {
        return "local";
    }

    @Override
    public int getHttpServerPort() {
        return 44419;
    }

    @Override
    public int getHttpsServerPort() {
        return -1;
    }

    @Override
    public String getHttpsKeyStorePath() {
        return null;
    }

    @Override
    public String getHttpsKeyStoreType() {
        return null;
    }

    @Override
    public String getHttpsKeyStoreStorePass() {
        return null;
    }

    @Override
    public String getHttpsKeyStoreKeypass() {
        return null;
    }

    @Override
    public String getServerHost() {
        return "0.0.0.0";
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isRoutesCaseSensitive() {
        return false;
    }

    @Override
    public long getServerMaxRequestBodyBytes() {
        return 104857600; // 104857600 => 100MB
    }

    @Override
    public List<String> getContentTypesToSkipGziping() {

        List<String> prefixes = new ArrayList<String>();

        prefixes.add("image/*");
        prefixes.add("video/*");
        prefixes.add("audio/*");
        prefixes.add("application/pdf");
        prefixes.add("application/octet-stream");
        prefixes.add("application/exe");
        prefixes.add("application/x-font-woff");
        prefixes.add("application/zip");
        prefixes.add("application/x-gzip");
        prefixes.add("application/x-rar-compressed");

        return prefixes;
    }

    @Override
    public File getSpincastWritableDir() {

        if(this.spincastDir == null) {
            File baseDir = new File(System.getProperty("java.io.tmpdir"));
            if(!baseDir.isDirectory()) {
                throw new RuntimeException("Temporary directory doesn't exist : " + baseDir.getAbsolutePath());
            }
            File spincastDir = new File(baseDir, "spincast");
            if(!spincastDir.isDirectory()) {
                boolean result = spincastDir.mkdirs();
                if(!result) {
                    throw new RuntimeException("Unable to create the Spincast writable directory : " +
                                               spincastDir.getAbsolutePath());
                }
            }
            this.spincastDir = spincastDir;
        }
        return this.spincastDir;
    }

    @Override
    public Locale getDefaultLocale() {
        return Locale.US;
    }

    @Override
    public int getRouteForwardingMaxNumber() {
        return 2;
    }

}
