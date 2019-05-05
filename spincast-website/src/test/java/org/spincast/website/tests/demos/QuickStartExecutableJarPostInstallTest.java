package org.spincast.website.tests.demos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.processutils.ProcessExecutionHandlerDefault;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.website.tests.utils.DemoTestBase;

public class QuickStartExecutableJarPostInstallTest extends DemoTestBase {

    @Override
    protected Class<?> getDemoAppClass() {
        return org.spincast.quickstart.App.class;
    }

    @Override
    protected String getFileSystemDemoAppClassToSourceRootRelativePath() {
        return "../..";
    }

    @Override
    protected String getDemoZipClasspathPath() {
        return "/public/quickstart/spincast-quick-start.zip";
    }

    @Override
    protected String getDemoArtifactName() {
        return "spincast-quickstart";
    }

    @Test
    public void test() throws Exception {

        //==========================================
        // Port 44419 must be available!
        //==========================================
        if (getSpincastUtils().isPortOpen("localhost", 44419)) {
            throw new RuntimeException("Port 44419 not available!");
        }

        File demoJar =
                new File(getDemoDir(), "target/spincast-quickstart-" + getSpincastUtils().getSpincastCurrentVersion() + ".jar");

        //==========================================
        // Will be "1.0.0" if taken from the classpath
        // .zip file (the pom.xml of the quickStart
        // is tweaked)!
        //==========================================
        if (!demoJar.isFile()) {
            demoJar = new File(getDemoDir(), "target/spincast-quickstart-1.0.0-SNAPSHOT.jar");
        }
        assertTrue(demoJar.isFile());

        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault();
        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               demoJar.getAbsolutePath());
        try {
            handler.waitForPortOpen("localhost", 44419, 10, 1000);

            String urlBase = "http://localhost:44419";

            HttpResponse response = getHttpClient().GET(urlBase + "/").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());
            String content = response.getContentAsString();
            assertTrue(content.contains("<div class=\"butterflyCon\">"));

            response = getHttpClient().GET(urlBase + "/robots.txt").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());
            content = response.getContentAsString();
            assertTrue(content.contains("User-agent: *"));

            response = getHttpClient().GET(urlBase + "/nope").send();
            assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());

            response = getHttpClient().GET(urlBase + "/exception-example").send();
            assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

        } finally {
            handler.killProcess();
        }
    }
}
