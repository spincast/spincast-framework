package org.spincast.plugins.processutils.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.processutils.ExecutionOutputStrategy;
import org.spincast.plugins.processutils.MavenProjectGoal;
import org.spincast.plugins.processutils.SpincastProcessUtils;
import org.spincast.plugins.processutils.SpincastProcessUtilsPlugin;
import org.spincast.plugins.processutils.SyncExecutionResult;
import org.spincast.plugins.processutils.exceptions.LaunchException;
import org.spincast.plugins.processutils.exceptions.TimeoutException;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public class ExecuteSyncTest extends NoAppTestingBase {

    @Override
    protected List<SpincastPlugin> getExtraPlugins() {
        List<SpincastPlugin> extraPlugins = super.getExtraPlugins();
        extraPlugins.add(new SpincastProcessUtilsPlugin());
        return extraPlugins;
    }

    @Inject
    private SpincastProcessUtils spincastProcessUtils;

    @Inject
    private SpincastUtils spincastUtils;


    protected SpincastProcessUtils getSpincastProcessUtils() {
        return this.spincastProcessUtils;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
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
        File jarFile = new File(getMavenProjectDir(), "target/spincast-test-1.0.0.jar");
        return jarFile;
    }

    protected File packageMavenProject() {
        File projectDir = getSpincastProcessUtils().executeGoalOnExternalMavenProject(new ResourceInfo("/externalMavenProject2",
                                                                                                       true),
                                                                                      MavenProjectGoal.PACKAGE,
                                                                                      null);
        return projectDir;
    }

    @Test
    public void executeExit0() throws Exception {

        SyncExecutionResult result = getSpincastProcessUtils().executeSync(1,
                                                                           TimeUnit.MINUTES,
                                                                           "java",
                                                                           "-jar",
                                                                           getMavenProjectJarFile().getAbsolutePath(),
                                                                           "0");
        assertEquals(0, result.getExitCode());
    }

    @Test
    public void executeExit100() throws Exception {

        SyncExecutionResult result = getSpincastProcessUtils().executeSync(1,
                                                                           TimeUnit.MINUTES,
                                                                           "java",
                                                                           "-jar",
                                                                           getMavenProjectJarFile().getAbsolutePath(),
                                                                           "100");
        assertEquals(1, result.getExitCode());
    }

    @Test
    public void timeout() throws Exception {

        try {
            @SuppressWarnings("unused")
            SyncExecutionResult result = getSpincastProcessUtils().executeSync(20,
                                                                               TimeUnit.MILLISECONDS,
                                                                               "java",
                                                                               "-jar",
                                                                               getMavenProjectJarFile().getAbsolutePath(),
                                                                               "123");
            fail();
        } catch (Exception ex) {
            assertTrue("Instance of: " + ex.getClass().getName(), ex instanceof TimeoutException);
        }
    }

    @Test
    public void launchException() throws Exception {

        try {
            @SuppressWarnings("unused")
            SyncExecutionResult result = getSpincastProcessUtils().executeSync(1,
                                                                               TimeUnit.MINUTES,
                                                                               UUID.randomUUID().toString());
            fail();
        } catch (Exception ex) {
            assertTrue(ex instanceof LaunchException);
        }
    }

    @Test
    public void outputDefaultToSystemOut() throws Exception {

        PrintStream outOriginal = System.out;
        PrintStream errOriginal = System.err;
        try {
            ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
            PrintStream outPrintStream = new PrintStream(outBaos);
            System.setOut(outPrintStream);

            ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
            PrintStream errPrintStream = new PrintStream(errBaos);
            System.setErr(errPrintStream);

            SyncExecutionResult result = getSpincastProcessUtils().executeSync(1,
                                                                               TimeUnit.MINUTES,
                                                                               "java",
                                                                               "-jar",
                                                                               getMavenProjectJarFile().getAbsolutePath(),
                                                                               "0");
            assertNotNull(result.getSystemOutLines());
            assertEquals(0, result.getSystemOutLines().size());

            assertNotNull(result.getSystemErrLines());
            assertEquals(0, result.getSystemErrLines().size());

            String out = outBaos.toString();
            assertNotNull(out);
            List<String> outLines = Arrays.asList(out.split("\\r?\\n"));
            assertTrue(outLines.contains("This is a System.out output1"));
            assertTrue(outLines.contains("This is a System.out output2"));

            String err = errBaos.toString();
            assertNotNull(err);
            List<String> errLines = Arrays.asList(err.split("\\r?\\n"));
            assertTrue(errLines.contains("This is a System.err output1"));
            assertTrue(errLines.contains("This is a System.err output2"));

        } finally {
            System.setOut(outOriginal);
            System.setErr(errOriginal);
        }
    }

    @Test
    public void outputToSystemOutExplicit() throws Exception {

        PrintStream outOriginal = System.out;
        PrintStream errOriginal = System.err;
        try {
            ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
            PrintStream outPrintStream = new PrintStream(outBaos);
            System.setOut(outPrintStream);

            ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
            PrintStream errPrintStream = new PrintStream(errBaos);
            System.setErr(errPrintStream);

            SyncExecutionResult result = getSpincastProcessUtils().executeSync(1,
                                                                               TimeUnit.MINUTES,
                                                                               ExecutionOutputStrategy.SYSTEM,
                                                                               "java",
                                                                               "-jar",
                                                                               getMavenProjectJarFile().getAbsolutePath(),
                                                                               "0");
            assertNotNull(result.getSystemOutLines());
            assertEquals(0, result.getSystemOutLines().size());

            assertNotNull(result.getSystemErrLines());
            assertEquals(0, result.getSystemErrLines().size());

            String out = outBaos.toString();
            assertNotNull(out);
            List<String> outLines = Arrays.asList(out.split("\\r?\\n"));
            assertTrue(outLines.contains("This is a System.out output1"));
            assertTrue(outLines.contains("This is a System.out output2"));

            String err = errBaos.toString();
            assertNotNull(err);
            List<String> errLines = Arrays.asList(err.split("\\r?\\n"));
            assertTrue(errLines.contains("This is a System.err output1"));
            assertTrue(errLines.contains("This is a System.err output2"));

        } finally {
            System.setOut(outOriginal);
            System.setErr(errOriginal);
        }
    }

    @Test
    public void outputNone() throws Exception {

        PrintStream outOriginal = System.out;
        PrintStream errOriginal = System.err;
        try {
            ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
            PrintStream outPrintStream = new PrintStream(outBaos);
            System.setOut(outPrintStream);

            ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
            PrintStream errPrintStream = new PrintStream(errBaos);
            System.setErr(errPrintStream);

            SyncExecutionResult result = getSpincastProcessUtils().executeSync(1,
                                                                               TimeUnit.MINUTES,
                                                                               ExecutionOutputStrategy.NONE,
                                                                               "java",
                                                                               "-jar",
                                                                               getMavenProjectJarFile().getAbsolutePath(),
                                                                               "0");
            assertNotNull(result.getSystemOutLines());
            assertEquals(0, result.getSystemOutLines().size());

            assertNotNull(result.getSystemErrLines());
            assertEquals(0, result.getSystemErrLines().size());

            String out = outBaos.toString();
            assertNotNull(out);
            List<String> outLines = Arrays.asList(out.split("\\r?\\n"));
            assertFalse(outLines.contains("This is a System.out output1"));
            assertFalse(outLines.contains("This is a System.out output2"));

            String err = errBaos.toString();
            assertNotNull(err);
            List<String> errLines = Arrays.asList(err.split("\\r?\\n"));
            assertFalse(errLines.contains("This is a System.err output1"));
            assertFalse(errLines.contains("This is a System.err output2"));


        } finally {
            System.setOut(outOriginal);
            System.setErr(errOriginal);
        }
    }

    @Test
    public void outputBuffer() throws Exception {

        SyncExecutionResult result = getSpincastProcessUtils().executeSync(1,
                                                                           TimeUnit.MINUTES,
                                                                           ExecutionOutputStrategy.BUFFER,
                                                                           "java",
                                                                           "-jar",
                                                                           getMavenProjectJarFile().getAbsolutePath(),
                                                                           "0");
        assertTrue(result.getSystemOutLines().size() >= 2);
        assertTrue(result.getSystemOutLines().contains("This is a System.out output1"));
        assertTrue(result.getSystemOutLines().contains("This is a System.out output2"));

        assertTrue(result.getSystemErrLines().size() >= 2);
        assertTrue(result.getSystemErrLines().contains("This is a System.err output1"));
        assertTrue(result.getSystemErrLines().contains("This is a System.err output2"));
    }

    @Test
    public void timeoutOutputAvailable() throws Exception {

        try {
            @SuppressWarnings("unused")
            SyncExecutionResult result = getSpincastProcessUtils().executeSync(2,
                                                                               TimeUnit.SECONDS,
                                                                               ExecutionOutputStrategy.BUFFER,
                                                                               "java",
                                                                               "-jar",
                                                                               getMavenProjectJarFile().getAbsolutePath(),
                                                                               "456");
            fail();
        } catch (Exception ex) {
            assertTrue("Instance of: " + ex.getClass().getName(), ex instanceof TimeoutException);
            SyncExecutionResult result = ((TimeoutException)ex).getSyncExecutionResult();
            assertNotNull(result);
            assertTrue(result.getSystemOutLines().size() >= 2);
            assertTrue(result.getSystemOutLines().contains("This is a System.out output1"));
            assertTrue(result.getSystemOutLines().contains("This is a System.out output2"));

            assertTrue(result.getSystemErrLines().size() >= 2);
            assertTrue(result.getSystemErrLines().contains("This is a System.err output1"));
            assertTrue(result.getSystemErrLines().contains("This is a System.err output2"));
        }
    }

}
