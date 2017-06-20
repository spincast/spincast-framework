package org.spincast.plugins.externalconfig.tests;

import java.io.File;
import java.io.InputStream;

import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.testing.IntegrationTestNoAppDefaultContextsBase;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.io.IOUtils;

public abstract class ConfigTestingBase extends IntegrationTestNoAppDefaultContextsBase {

    @Override
    public void beforeClass() {

        createConfigFiles();
        super.beforeClass();
    }

    protected String getConfigFile1Path() {
        String path = new File(".").getAbsolutePath();
        path = path.substring(0, path.length() - 1) + "app-config.yaml";
        return path;
    }

    protected String getConfigFile2Path() {
        String path = new File(".").getAbsolutePath();
        path = path.substring(0, path.length() - 1) + "app-config2.yaml";
        return path;
    }

    protected void createConfigFiles() {
        try {

            String content = readClasspathFile("/app-config-external.yaml");
            FileUtils.writeStringToFile(new File(getConfigFile1Path()), content, "UTF-8");

            content = readClasspathFile("/app-config2.yaml");
            FileUtils.writeStringToFile(new File(getConfigFile2Path()), content, "UTF-8");

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected String readClasspathFile(String path) {

        if (path == null) {
            return null;
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        InputStream in = this.getClass().getResourceAsStream(path);
        if (in == null) {
            return null;
        }

        try {
            return IOUtils.toString(in, "UTF-8");
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    public void afterClass() {
        deleteConfigFiles();
        super.afterClass();
    }

    protected void deleteConfigFiles() {
        File file = new File(getConfigFile1Path());
        if (file.isFile()) {
            FileUtils.deleteQuietly(file);
        }

        file = new File(getConfigFile2Path());
        if (file.isFile()) {
            FileUtils.deleteQuietly(file);
        }
    }

}
