package org.spincast.website.tests.demos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.processutils.ProcessExecutionHandlerDefault;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.website.tests.utils.DemoTestBase;

public class SuperExecutableJarPostInstallTest extends DemoTestBase {

    @Override
    protected Class<?> getDemoAppClass() {
        return org.spincast.demos.supercalifragilisticexpialidocious.App.class;
    }

    @Override
    protected String getFileSystemDemoAppClassToSourceRootRelativePath() {
        return "../..";
    }

    @Override
    protected String getDemoZipClasspathPath() {
        return "/public/demo-apps/spincast-demos-supercalifragilisticexpialidocious.zip";
    }

    @Override
    protected String getDemoArtifactName() {
        return "spincast-demos-supercalifragilisticexpialidocious";
    }

    @Override
    public void prepareDemoProject() {
        super.prepareDemoProject();

        //==========================================
        // Copy the provided config near the jar file!
        //==========================================
        try {
            FileUtils.copyFile(new File(getDemoDir(), "app-config.yaml"), new File(getDemoDir(), "target/app-config.yaml"));
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Test
    public void test() throws Exception {

        //==========================================
        // Port 12345 must be available!
        //==========================================
        if (getSpincastUtils().isPortOpen("localhost", 12345)) {
            throw new RuntimeException("Port 12345 not available!");
        }

        File demoJar =
                new File(getDemoDir(),
                         "target/spincast-demos-supercalifragilisticexpialidocious-" +
                                       getSpincastUtils().getSpincastCurrentVersion() + ".jar");
        assertTrue(demoJar.isFile());

        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault();
        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               demoJar.getAbsolutePath());
        try {
            handler.waitForPortOpen("localhost", 12345, 10, 1000);

            String urlBase = "http://localhost:12345";
            HttpResponse response = getHttpClient().GET(urlBase + "/").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());

            String content = response.getContentAsString();
            assertEquals("Hello World!", content);

        } finally {
            handler.killProcess();
        }
    }
}
