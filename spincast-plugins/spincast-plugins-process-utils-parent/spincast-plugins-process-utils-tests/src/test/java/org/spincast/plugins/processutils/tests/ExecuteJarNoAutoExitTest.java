package org.spincast.plugins.processutils.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.httpclient.HttpClient;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.httpclient.SpincastHttpClientPlugin;
import org.spincast.plugins.processutils.JarExecutionHandlerDefault;
import org.spincast.plugins.processutils.MavenProjectGoal;
import org.spincast.plugins.processutils.SpincastProcessUtils;
import org.spincast.plugins.processutils.SpincastProcessUtilsPlugin;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ExecuteJarNoAutoExitTest extends NoAppTestingBase {

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastProcessUtilsPlugin());
        extraPlugins.add(new SpincastHttpClientPlugin());
        return extraPlugins;
    }

    @Inject
    private SpincastProcessUtils spincastProcessUtils;

    @Inject
    private SpincastUtils spincastUtils;

    @Inject
    private HttpClient httpClient;

    protected SpincastProcessUtils getSpincastProcessUtils() {
        return this.spincastProcessUtils;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected HttpClient getHttpClient() {
        return this.httpClient;
    }

    protected File mavenProjectDir;

    @Override
    public void beforeClass() {
        super.beforeClass();
        this.mavenProjectDir = packageMavenProject();

        File jarFile = getMavenProjectJarFile();
        assertTrue(jarFile.isFile());
    }

    protected File getMavenProjectDir() {
        return this.mavenProjectDir;
    }

    protected String getSpincastVersionToUse() {
        return "1.3.1-SNAPSHOT";
    }


    protected File getMavenProjectJarFile() {
        File jarFile = new File(getMavenProjectDir(), "target/spincast-test-" + getSpincastVersionToUse() + ".jar");
        return jarFile;
    }

    protected File packageMavenProject() {
        //==========================================
        // Package a Maven project
        //==========================================
        File projectDir = getSpincastProcessUtils().executeGoalOnExternalMavenProject(new ResourceInfo("/externalMavenProject",
                                                                                                       true),
                                                                                      MavenProjectGoal.PACKAGE,
                                                                                      SpincastStatics.map("spincastVersion",
                                                                                                          getSpincastVersionToUse()));
        return projectDir;
    }

    @Test
    public void executeJar() throws Exception {

        //==========================================
        // Execute the generated .jar
        //==========================================
        int port = SpincastTestingUtils.findFreePort();
        File targetDir = createTestingDir();

        assertFalse(new File(targetDir, "bg.jpg").isFile());
        assertFalse(new File(targetDir, "logo.png").isFile());

        JarExecutionHandlerDefault handler = new JarExecutionHandlerDefault();
        getSpincastProcessUtils().executeJar(getMavenProjectJarFile().getAbsolutePath(),
                                             Lists.newArrayList(String.valueOf(port), "Stromgol"),
                                             handler);
        try {
            //==========================================
            // Wait for the port to be open
            //==========================================
            handler.waitForPortOpen("localhost", port, 10, 1000);

            String urlBase = "http://localhost:" + port;
            HttpResponse response = getHttpClient().GET(urlBase + "/").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());

            String body = response.getContentAsString();
            assertEquals("Hello Stromgol", body);
        } finally {
            handler.killJarProcess();
        }
    }

    @Test
    public void invalidPort() throws Exception {

        //==========================================
        // Execute the generated .jar
        //==========================================
        int port = SpincastTestingUtils.findFreePort();
        File targetDir = createTestingDir();

        assertFalse(new File(targetDir, "bg.jpg").isFile());
        assertFalse(new File(targetDir, "logo.png").isFile());

        JarExecutionHandlerDefault handler = new JarExecutionHandlerDefault();
        getSpincastProcessUtils().executeJar(getMavenProjectJarFile().getAbsolutePath(),
                                             Lists.newArrayList(String.valueOf(port), "Stromgol"),
                                             handler);
        try {

            try {
                handler.waitForPortOpen("localhost", port + 10, 5, 1000); // invalid port
                fail();
            } catch (Exception ex) {
            }
        } finally {
            handler.killJarProcess();
        }
    }

}
