package org.spincast.website.tests.demos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.spincast.core.json.JsonObject;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.processutils.ProcessExecutionHandlerDefault;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.website.tests.utils.DemoTestBase;

public class SumExecutableJarPostInstallTest extends DemoTestBase {

    @Override
    protected Class<?> getDemoAppClass() {
        return org.spincast.demos.sum.App.class;
    }

    @Override
    protected String getFileSystemDemoAppClassToSourceRootRelativePath() {
        return "../..";
    }

    @Override
    protected String getDemoZipClasspathPath() {
        return "/public/demo-apps/spincast-demos-sum.zip";
    }

    @Override
    protected String getDemoArtifactName() {
        return "spincast-demos-sum";
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
                new File(getDemoDir(), "target/spincast-demos-sum-" + getSpincastUtils().getSpincastCurrentVersion() + ".jar");
        assertTrue(demoJar.isFile());

        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault();
        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               demoJar.getAbsolutePath());
        try {
            handler.waitForPortOpen("localhost", 44419, 10, 1000);

            String urlBase = "http://localhost:44419";

            HttpResponse response = getHttpClient().POST(urlBase + "/sum")
                                                   .addFormBodyFieldValue("first", "10")
                                                   .addFormBodyFieldValue("second", "7")
                                                   .send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());

            JsonObject resultObj = response.getContentAsJsonObject();
            assertEquals(Integer.valueOf(17), resultObj.getInteger("result"));

        } finally {
            handler.killProcess();
        }
    }
}
