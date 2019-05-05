package org.spincast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.httpclient.HttpClient;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.httpclient.SpincastHttpClientPlugin;
import org.spincast.plugins.processutils.MavenProjectGoal;
import org.spincast.plugins.processutils.ProcessExecutionHandlerDefault;
import org.spincast.plugins.processutils.SpincastProcessUtils;
import org.spincast.plugins.processutils.SpincastProcessUtilsPlugin;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public class SpincastUtilsWithExecutableJarPostInstallTest extends NoAppTestingBase {

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastHttpClientPlugin());
        extraPlugins.add(new SpincastProcessUtilsPlugin());
        return extraPlugins;
    }

    @Override
    public void beforeClass() {
        super.beforeClass();
        packageTestmavenProject();
        assertTrue(getTestMavenJarFile().getAbsolutePath(), getTestMavenJarFile().isFile());
    }

    private File testMavenProjectDir;

    protected void packageTestmavenProject() {
        this.testMavenProjectDir =
                getSpincastProcessUtils().executeGoalOnExternalMavenProject(new ResourceInfo("/testMavenProject",
                                                                                             true),
                                                                            MavenProjectGoal.PACKAGE,
                                                                            SpincastStatics.map("spincastVersion",
                                                                                                getSpincastUtils().getSpincastCurrentVersion()));
    }

    protected File getTestMavenProjectDir() {
        return this.testMavenProjectDir;
    }

    protected File getTestMavenJarFile() {
        File jarFile = new File(getTestMavenProjectDir(),
                                "target/spincast-test-" + getSpincastUtils().getSpincastCurrentVersion() + ".jar");
        return jarFile;
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

    @Test
    public void copyClasspathFileToFileSystemInsideJar() throws Exception {

        int port = SpincastTestingUtils.findFreePort();

        //==========================================
        // Execute the project's jar which is going
        // to copy a classpath dir to the specified
        // "targetDir" passed as an argument.
        //==========================================
        File targetFile = new File(createTestingFilePath());
        assertFalse(targetFile.isFile());

        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault();
        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               getTestMavenJarFile().getAbsolutePath(),
                                               String.valueOf(port),
                                               "/quick-start/public/js/main.js",
                                               targetFile.getAbsolutePath());

        try {
            handler.waitForPortOpen("localhost", port, 10, 1000);

            String urlBase = "http://localhost:" + port;
            HttpResponse response = getHttpClient().GET(urlBase + "/file").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());
            assertTrue(targetFile.isFile());
        } finally {
            handler.killProcess();
        }
    }

    @Test
    public void copyClasspathDirToFileSystemInsideJar() throws Exception {

        int port = SpincastTestingUtils.findFreePort();

        //==========================================
        // Execute the project's jar which is going
        // to copy a classpath dir to the specified
        // "targetDir" passed as an argument.
        //==========================================
        File targetDir = createTestingDir();
        assertFalse(new File(targetDir, "bg.jpg").isFile());
        assertFalse(new File(targetDir, "logo.png").isFile());

        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault();
        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               getTestMavenJarFile().getAbsolutePath(),
                                               String.valueOf(port),
                                               "/quick-start/public/images",
                                               targetDir.getAbsolutePath());

        try {
            handler.waitForPortOpen("localhost", port, 10, 1000);

            String urlBase = "http://localhost:" + port;
            HttpResponse response = getHttpClient().GET(urlBase + "/").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());

            assertTrue(new File(targetDir, "bg.jpg").isFile());
            assertTrue(new File(targetDir, "logo.png").isFile());
        } finally {
            handler.killProcess();
        }
    }

    @Test
    public void isClasspathResourceLoadedFromJar() throws Exception {

        int port = SpincastTestingUtils.findFreePort();

        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault();
        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               getTestMavenJarFile().getAbsolutePath(),
                                               String.valueOf(port));
        try {
            handler.waitForPortOpen("localhost", port, 10, 1000);

            String urlBase = "http://localhost:" + port;
            HttpResponse response = getHttpClient().GET(urlBase + "/resource").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());

            String body = response.getContentAsString();
            assertEquals("true", body);

        } finally {
            handler.killProcess();
        }
    }

    @Test
    public void getClassLocationDirOrJar() throws Exception {

        int port = SpincastTestingUtils.findFreePort();

        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault();
        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               getTestMavenJarFile().getAbsolutePath(),
                                               String.valueOf(port));
        try {
            handler.waitForPortOpen("localhost", port, 10, 1000);

            String urlBase = "http://localhost:" + port;
            HttpResponse response = getHttpClient().GET(urlBase + "/getClassLocationDirOrJar").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());

            String body = response.getContentAsString();
            File file = new File(body);
            assertTrue(file.isFile());
            assertTrue(file.getAbsolutePath().toLowerCase().endsWith(".jar"));

        } finally {
            handler.killProcess();
        }
    }
}
