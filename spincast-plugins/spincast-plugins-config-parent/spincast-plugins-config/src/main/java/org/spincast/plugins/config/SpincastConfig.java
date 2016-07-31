package org.spincast.plugins.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.spincast.core.config.ISpincastConfig;
import org.spincast.core.routing.IStaticResourceCacheConfig;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

public class SpincastConfig implements ISpincastConfig {

    private File spincastBaseWritableDir;
    private IStaticResourceCacheConfig staticResourceCacheConfig;
    private IStaticResourceCacheConfig dynamicResourceCacheConfig;

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

        if(this.spincastBaseWritableDir == null) {

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
            } else {
                cleanWritableSpincastDir(spincastDir);
            }

            this.spincastBaseWritableDir = spincastDir;
        }
        return this.spincastBaseWritableDir;
    }

    protected String getSpincastTempDirName() {
        return "temp";
    }

    protected void cleanWritableSpincastDir(File spincastTempDir) {
        try {
            FileUtils.cleanDirectory(spincastTempDir);
        } catch(IOException e) {
            throw new RuntimeException("Unable to clean the Spincast writable directory: " +
                                       spincastTempDir.getAbsolutePath());
        }
    }

    @Override
    public Locale getDefaultLocale() {
        return Locale.US;
    }

    @Override
    public int getRouteForwardingMaxNumber() {
        return 2;
    }

    @Override
    public int getDefaultRouteCacheFilterSecondsNbr() {
        return 3600; // == 1 hour
    }

    @Override
    public boolean isDefaultRouteCacheFilterPrivate() {
        return false;
    }

    @Override
    public Integer getDefaultRouteCacheFilterSecondsNbrCdns() {
        return null; // option not used by default
    }

    @Override
    public boolean isDisableWriteToDiskDynamicStaticResource() {
        return isDebugEnabled();
    }

    @Override
    public boolean isAddDefaultTemplateVariablesFilter() {
        return true;
    }

    @Override
    public int getDefaultTemplateVariablesFilterPosition() {
        //==========================================
        // "-10", before filter, so default values
        // can be modified by the main handler.
        //==========================================
        return -10;
    }

    @Override
    public IStaticResourceCacheConfig getDefaultStaticResourceCacheConfig(boolean isDynamicResource) {

        if(this.staticResourceCacheConfig == null) {
            this.staticResourceCacheConfig = new IStaticResourceCacheConfig() {

                @Override
                public int getCacheSeconds() {
                    return 86400; // 86400 => 1 day
                }

                @Override
                public boolean isCachePrivate() {
                    return false;
                }

                @Override
                public Integer getCacheSecondsCdn() {
                    return null;
                }
            };
        }

        if(this.dynamicResourceCacheConfig == null) {
            this.dynamicResourceCacheConfig = new IStaticResourceCacheConfig() {

                @Override
                public int getCacheSeconds() {
                    return 3600; // 3600 => 1 hour
                }

                @Override
                public boolean isCachePrivate() {
                    return false;
                }

                @Override
                public Integer getCacheSecondsCdn() {
                    return null;
                }
            };
        }

        if(isDynamicResource) {
            return this.dynamicResourceCacheConfig;
        } else {
            return this.staticResourceCacheConfig;
        }
    }

}
