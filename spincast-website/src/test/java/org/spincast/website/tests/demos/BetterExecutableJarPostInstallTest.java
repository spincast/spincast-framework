package org.spincast.website.tests.demos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.spincast.core.json.JsonObject;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.processutils.ProcessExecutionHandlerDefault;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.website.tests.utils.DemoTestBase;

public class BetterExecutableJarPostInstallTest extends DemoTestBase {

    @Override
    protected Class<?> getDemoAppClass() {
        return org.spincast.demos.better.App.class;
    }

    @Override
    protected String getFileSystemDemoAppClassToSourceRootRelativePath() {
        return "../..";
    }

    @Override
    protected String getDemoZipClasspathPath() {
        return "/public/demo-apps/spincast-demos-better.zip";
    }

    @Override
    protected String getDemoArtifactName() {
        return "spincast-demos-better";
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
                new File(getDemoDir(), "target/spincast-demos-better-" + getSpincastUtils().getSpincastCurrentVersion() + ".jar");
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

            JsonObject responseObj = response.getContentAsJsonObject();
            assertNotNull(responseObj);
            assertEquals("Stromgol", responseObj.getString("name"));
            assertEquals(Integer.valueOf(42), responseObj.getInteger("age"));

        } finally {
            handler.killProcess();
        }
    }
}
