package org.spincast.website.tests.demos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.json.JsonObject;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.httpclient.HttpClient;
import org.spincast.plugins.httpclient.HttpResponse;
import org.spincast.plugins.httpclient.SpincastHttpClientPlugin;
import org.spincast.plugins.processutils.JarExecutionHandlerDefault;
import org.spincast.plugins.processutils.MavenProjectGoal;
import org.spincast.plugins.processutils.SpincastProcessUtils;
import org.spincast.plugins.processutils.SpincastProcessUtilsPlugin;
import org.spincast.shaded.org.apache.http.HttpStatus;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public class BetterExecutableJarTest extends NoAppTestingBase {

    @Override
    public boolean isTestsFileDisabled() {
        //==========================================
        // This tests file can't be ran during a release
        // since the executable jar has dependencies on
        // other jars of the current version which won't be
        // installed yet during the Maven "test" phase...
        //==========================================
        return isMavenReleaseProfile();
    }

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastHttpClientPlugin());
        extraPlugins.add(new SpincastProcessUtilsPlugin());
        return extraPlugins;
    }

    File demoDir;

    @Override
    public void beforeClass() {
        super.beforeClass();

        unzipDemoProject();
        getSpincastProcessUtils().executeGoalOnExternalMavenProject(new ResourceInfo(this.demoDir.getAbsolutePath(), false),
                                                                    MavenProjectGoal.PACKAGE);
    }

    protected void unzipDemoProject() {
        String targetZipFileName = UUID.randomUUID().toString() + ".zip";
        File targetZipFile = new File(createTestingFilePath(targetZipFileName));
        getSpincastUtils().copyClasspathFileToFileSystem("/public/demo-apps/spincast-demos-better.zip", targetZipFile);

        File dir = createTestingDir();
        getSpincastUtils().zipExtract(targetZipFile, dir);
        this.demoDir = new File(dir, "spincast-demos-better");
    }

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

    @Test
    public void test() throws Exception {

        //==========================================
        // port 44419 must be available
        //==========================================
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 44419));
            throw new RuntimeException("Port 44419 not available!");
        } catch (IOException e) {
            // ok!
        } finally {
            try {
                socket.close();
            } catch (Exception ex) {
                //...
            }
        }

        File demoJar =
                new File(this.demoDir, "target/spincast-demos-better-" + getSpincastUtils().getSpincastCurrentVersion() + ".jar");
        assertTrue(demoJar.isFile());

        JarExecutionHandlerDefault handler = new JarExecutionHandlerDefault();
        getSpincastProcessUtils().executeJar(demoJar.getAbsolutePath(), null, handler);
        try {
            handler.waitForPortOpen("localhost", 44419, 10, 1000);

            String urlBase = "http://localhost:44419";
            HttpResponse response = getHttpClient().GET(urlBase + "/").send();
            assertEquals(HttpStatus.SC_OK, response.getStatus());

            JsonObject responseObj = response.getContentAsJsonObject();
            assertNotNull(responseObj);
            assertEquals("Stromgol", responseObj.getString("name"));
            assertEquals(new Integer(42), responseObj.getInteger("age"));

        } finally {
            handler.killJarProcess();
        }
    }
}
