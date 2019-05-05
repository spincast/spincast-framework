package org.spincast.plugins.processutils.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.spincast.core.guice.SpincastPlugin;
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

public class ExecuteAsyncNoAutoExitPostInstallTest extends NoAppTestingBase {

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

    protected File getMavenProjectJarFile() {
        File jarFile =
                new File(getMavenProjectDir(), "target/spincast-test-" + getSpincastUtils().getSpincastCurrentVersion() + ".jar");
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
                                                                                                          getSpincastUtils().getSpincastCurrentVersion()));
        return projectDir;
    }

    @Test
    public void executeAsync() throws Exception {

        //==========================================
        // Execute the generated .jar
        //==========================================
        int port = SpincastTestingUtils.findFreePort();
        File targetDir = createTestingDir();

        assertFalse(new File(targetDir, "bg.jpg").isFile());
        assertFalse(new File(targetDir, "logo.png").isFile());

        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault();
        assertFalse(handler.isProcessAlive());

        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               getMavenProjectJarFile().getAbsolutePath(),
                                               String.valueOf(port),
                                               "Stromgol");
        try {

            //==========================================
            // Wait for the port to be open
            //==========================================
            handler.waitForPortOpen("localhost", port, 10, 1000);

            assertTrue(handler.isProcessAlive());

            String urlBase = "http://localhost:" + port;
            HttpResponse response = getHttpClient().GET(urlBase + "/").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());

            String body = response.getContentAsString();
            assertEquals("Hello Stromgol", body);
        } finally {
            handler.killProcess();
        }
    }

    @Test
    public void invalidPortToWaitFor() throws Exception {

        //==========================================
        // Execute the generated .jar
        //==========================================
        int port = SpincastTestingUtils.findFreePort();
        File targetDir = createTestingDir();

        assertFalse(new File(targetDir, "bg.jpg").isFile());
        assertFalse(new File(targetDir, "logo.png").isFile());

        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault();
        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               getMavenProjectJarFile().getAbsolutePath(),
                                               String.valueOf(port),
                                               "Stromgol");
        try {

            try {
                handler.waitForPortOpen("localhost", port + 10, 5, 1000); // invalid port
                fail();
            } catch (Exception ex) {
            }
        } finally {
            handler.killProcess();
        }
    }

    @Test
    public void executeAsyncError() throws Exception {

        Integer[] exitValue = new Integer[]{null};
        Exception[] exception = new Exception[]{null};
        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault() {

            /**
             * Called when the execution process exits
             */
            @Override
            public void onExit(int exitVal) {
                exitValue[0] = exitVal;
            }

            @Override
            public void onLaunchException(Exception ex) {
                exception[0] = ex;
            }
        };
        assertFalse(handler.isProcessAlive());

        getSpincastProcessUtils().executeAsync(handler, "_nope");
        try {
            assertFalse(handler.isProcessAlive());
            assertNotNull(exception[0]);
            assertNull(exitValue[0]); // not called
        } finally {
            // should not throw errors
            handler.killProcess();
        }
    }

    @Test
    public void executeAsyncTimeout() throws Exception {

        int port = SpincastTestingUtils.findFreePort();

        Integer[] exitValue = new Integer[]{null};
        Exception[] launchException = new Exception[]{null};
        boolean[] timeoutException = new boolean[]{false};
        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault() {

            /**
             * Called when the execution process exits
             */
            @Override
            public void onExit(int exitVal) {
                exitValue[0] = exitVal;
            }

            @Override
            public void onLaunchException(Exception ex) {
                launchException[0] = ex;
            }

            @Override
            public void onTimeoutException() {
                timeoutException[0] = true;
            }
        };
        assertFalse(handler.isProcessAlive());

        getSpincastProcessUtils().executeAsync(handler,
                                               1,
                                               TimeUnit.SECONDS,
                                               "java",
                                               "-jar",
                                               getMavenProjectJarFile().getAbsolutePath(),
                                               String.valueOf(port),
                                               "Stromgol");
        try {

            Thread.sleep(1500);

            assertFalse(handler.isProcessAlive());
            assertNull(launchException[0]);
            assertTrue(timeoutException[0]);
            assertNull(exitValue[0]);
        } finally {
            handler.killProcess();
        }
    }

}
