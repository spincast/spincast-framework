package org.spincast.website.tests.utils;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.httpclient.HttpClient;
import org.spincast.plugins.httpclient.SpincastHttpClientPlugin;
import org.spincast.plugins.processutils.MavenProjectGoal;
import org.spincast.plugins.processutils.SpincastProcessUtils;
import org.spincast.plugins.processutils.SpincastProcessUtilsPlugin;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public abstract class DemoTestBase extends NoAppTestingBase {

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastHttpClientPlugin());
        extraPlugins.add(new SpincastProcessUtilsPlugin());
        return extraPlugins;
    }

    File demoDir;

    protected File getDemoDir() {
        return this.demoDir;
    }

    @Override
    public void beforeClass() {
        super.beforeClass();

        prepareDemoProject();
        getSpincastProcessUtils().executeGoalOnExternalMavenProject(new ResourceInfo(getDemoDir().getAbsolutePath(), false),
                                                                    MavenProjectGoal.PACKAGE);
    }

    protected void prepareDemoProject() {

        if (!getSpincastUtils().isClassLoadedFromJar(getDemoAppClass())) {
            this.demoDir =
                    new File(getSpincastUtils().getClassLocationDirOrJarFile(getDemoAppClass()),
                             getFileSystemDemoAppClassToSourceRootRelativePath());
        }

        //==========================================
        // If not in a jar of if the project was not found
        // on the file system, try the classpath...
        //==========================================
        if (this.demoDir == null || !this.demoDir.isDirectory()) {
            String targetZipFileName = UUID.randomUUID().toString() + ".zip";
            File targetZipFile = new File(createTestingFilePath(targetZipFileName));

            getSpincastUtils().copyClasspathFileToFileSystem(getDemoZipClasspathPath(),
                                                             targetZipFile);

            File dir = createTestingDir();
            getSpincastUtils().zipExtract(targetZipFile, dir);
            this.demoDir = new File(dir, getDemoArtifactName());
        }
    }

    protected abstract Class<?> getDemoAppClass();

    protected abstract String getFileSystemDemoAppClassToSourceRootRelativePath();

    protected abstract String getDemoZipClasspathPath();

    protected abstract String getDemoArtifactName();

    protected File getProjectDir() {
        return this.demoDir;
    }

    @Inject
    private SpincastUtils spincastUtils;

    @Inject
    private TemplatingEngine templatingEngine;

    @Inject
    private HttpClient httpClient;

    @Inject
    private SpincastProcessUtils spincastProcessUtils;

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    protected HttpClient getHttpClient() {
        return this.httpClient;
    }

    protected SpincastProcessUtils getSpincastProcessUtils() {
        return this.spincastProcessUtils;
    }

}
