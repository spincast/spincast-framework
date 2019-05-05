package org.spincast.plugins.processutils.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.processutils.MavenProjectGoal;
import org.spincast.plugins.processutils.ProcessExecutionHandlerDefault;
import org.spincast.plugins.processutils.SpincastProcessUtils;
import org.spincast.plugins.processutils.SpincastProcessUtilsPlugin;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.core.utils.TrueChecker;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public class ExecuteAsyncAutoExitTest extends NoAppTestingBase {

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

        int[] exitCode = new int[]{-1};

        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault() {

            @Override
            public void onExit(int exitVal) {
                exitCode[0] = exitVal;
            }
        };

        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               getMavenProjectJarFile().getAbsolutePath(),
                                               "0");
        try {

            SpincastTestingUtils.waitForTrue(new TrueChecker() {

                @Override
                public boolean check() {
                    return exitCode[0] > -1;
                }
            }, 5000);

            assertEquals(0, exitCode[0]);

        } finally {
            handler.killProcess();
        }
    }

    @Test
    public void executeJarExit1() throws Exception {

        int[] exitCode = new int[]{-1};
        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault() {

            @Override
            public void onExit(int exitVal) {
                exitCode[0] = exitVal;
            }
        };

        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               getMavenProjectJarFile().getAbsolutePath(),
                                               "1");

        try {

            SpincastTestingUtils.waitForTrue(new TrueChecker() {

                @Override
                public boolean check() {
                    return exitCode[0] > -1;
                }
            }, 5000);

            assertEquals(1, exitCode[0]);

        } finally {
            handler.killProcess();
        }
    }

    @Test
    public void executeJarExit2() throws Exception {

        int[] exitCode = new int[]{-1};
        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault() {

            @Override
            public void onExit(int exitVal) {
                exitCode[0] = exitVal;
            }
        };

        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               getMavenProjectJarFile().getAbsolutePath(),
                                               "2");
        try {

            SpincastTestingUtils.waitForTrue(new TrueChecker() {

                @Override
                public boolean check() {
                    return exitCode[0] > -1;
                }
            }, 5000);

            assertEquals(2, exitCode[0]);

        } finally {
            handler.killProcess();
        }
    }

    @Test
    public void executeJarExitException() throws Exception {

        int[] exitCode = new int[]{-1};
        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault() {

            @Override
            public void onExit(int exitVal) {
                exitCode[0] = exitVal;
            }
        };

        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               getMavenProjectJarFile().getAbsolutePath(),
                                               "100"); // 100 => throw exception

        try {

            SpincastTestingUtils.waitForTrue(new TrueChecker() {

                @Override
                public boolean check() {
                    return exitCode[0] > -1;
                }
            }, 5000);

            assertEquals(1, exitCode[0]);

        } finally {
            handler.killProcess();
        }
    }

    @Test
    public void executeJarStandardExit() throws Exception {

        int[] exitCode = new int[]{-1};
        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault() {

            @Override
            public void onExit(int exitVal) {
                exitCode[0] = exitVal;
            }
        };

        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               getMavenProjectJarFile().getAbsolutePath(),
                                               "1000"); // 1000 => standard exit
        try {

            SpincastTestingUtils.waitForTrue(new TrueChecker() {

                @Override
                public boolean check() {
                    return exitCode[0] > -1;
                }
            }, 5000);

            assertEquals(0, exitCode[0]);

        } finally {
            handler.killProcess();
        }
    }


    @Test
    public void outputDefaultsToSystemOut() throws Exception {


        PrintStream outOriginal = System.out;
        PrintStream errOriginal = System.err;
        try {
            ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
            PrintStream outPrintStream = new PrintStream(outBaos);
            System.setOut(outPrintStream);

            ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
            PrintStream errPrintStream = new PrintStream(errBaos);
            System.setErr(errPrintStream);

            CountDownLatch exitLatch = new CountDownLatch(1);
            ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault() {

                @Override
                public void onEnd() {
                    exitLatch.countDown();
                }
            };

            getSpincastProcessUtils().executeAsync(handler,
                                                   "java",
                                                   "-jar",
                                                   getMavenProjectJarFile().getAbsolutePath(),
                                                   "0");
            try {

                exitLatch.await(1, TimeUnit.MINUTES);

                String out = outBaos.toString();
                assertNotNull(out);
                List<String> outLines = Arrays.asList(out.split("\\r?\\n"));
                assertTrue(outLines.size() >= 2);
                assertTrue(outLines.contains("This is a System.out output1"));
                assertTrue(outLines.contains("This is a System.out output2"));

                String err = errBaos.toString();
                assertNotNull(err);
                List<String> errLines = Arrays.asList(err.split("\\r?\\n"));
                assertTrue(errLines.size() >= 2);
                assertTrue(errLines.contains("This is a System.err output1"));
                assertTrue(errLines.contains("This is a System.err output2"));

            } finally {
                handler.killProcess();
            }


        } finally {
            System.setOut(outOriginal);
            System.setErr(errOriginal);
        }
    }

    @Test
    public void getOutput() throws Exception {

        CountDownLatch exitLatch = new CountDownLatch(1);
        List<String> outLines = new ArrayList<String>();
        List<String> errLines = new ArrayList<String>();
        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault() {

            @Override
            public void onSystemOut(String line) {
                outLines.add(line);
            }

            @Override
            public void onSystemErr(String line) {
                errLines.add(line);
            }

            @Override
            public void onEnd() {
                exitLatch.countDown();
            }
        };

        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               getMavenProjectJarFile().getAbsolutePath(),
                                               "0");
        try {

            exitLatch.await(1, TimeUnit.MINUTES);

            assertEquals(2, outLines.size());
            assertTrue(outLines.contains("This is a System.out output1"));
            assertTrue(outLines.contains("This is a System.out output2"));

            assertEquals(2, errLines.size());
            assertTrue(errLines.contains("This is a System.err output1"));
            assertTrue(errLines.contains("This is a System.err output2"));

        } finally {
            handler.killProcess();
        }
    }

    @Test
    public void noOutput() throws Exception {

        CountDownLatch exitLatch = new CountDownLatch(1);

        List<String> outLines = new ArrayList<String>();
        List<String> errLines = new ArrayList<String>();
        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault() {

            @Override
            public void onSystemOut(String line) {
                outLines.add(line);
            }

            @Override
            public void onSystemErr(String line) {
                errLines.add(line);
            }

            @Override
            public void onEnd() {
                exitLatch.countDown();
            }
        };

        getSpincastProcessUtils().executeAsync(handler,
                                               "java",
                                               "-jar",
                                               getMavenProjectJarFile().getAbsolutePath(),
                                               "1000"); // 1000 => no output!
        try {

            exitLatch.await(1, TimeUnit.MINUTES);

            assertEquals(0, outLines.size());
            assertEquals(0, errLines.size());

        } finally {
            handler.killProcess();
        }
    }

    @Test
    public void timeout() throws Exception {

        CountDownLatch exitLatch = new CountDownLatch(1);

        Integer[] exitCode = new Integer[]{null};
        boolean[] timouted = new boolean[]{false};
        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault() {

            @Override
            public void onExit(int exitVal) {
                exitCode[0] = exitVal;
            }

            @Override
            public void onEnd() {
                exitLatch.countDown();
            }

            @Override
            public void onTimeoutException() {
                timouted[0] = true;
            }
        };

        getSpincastProcessUtils().executeAsync(handler,
                                               10,
                                               TimeUnit.MILLISECONDS,
                                               "java",
                                               "-jar",
                                               getMavenProjectJarFile().getAbsolutePath(),
                                               "0");
        try {
            exitLatch.await(1, TimeUnit.MINUTES);
            assertTrue(timouted[0]);
            assertNull(exitCode[0]);
        } finally {
            handler.killProcess();
        }
    }

    @Test
    public void endCalledWhenProcessKilledManually() throws Exception {

        Integer[] exitCode = new Integer[]{null};
        boolean[] endCalled = new boolean[]{false};
        boolean[] timouted = new boolean[]{false};
        ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault() {

            @Override
            public void onExit(int exitVal) {
                exitCode[0] = exitVal;
            }

            @Override
            public void onEnd() {
                endCalled[0] = true;
            }

            @Override
            public void onTimeoutException() {
                timouted[0] = true;
            }
        };

        getSpincastProcessUtils().executeAsync(handler,
                                               1,
                                               TimeUnit.MINUTES,
                                               "java",
                                               "-jar",
                                               getMavenProjectJarFile().getAbsolutePath(),
                                               "456");
        try {

            Thread.sleep(5000);

            assertTrue(handler.isProcessAlive());

            handler.killProcess();
            assertFalse(handler.isProcessAlive());

            assertTrue(endCalled[0]);
            assertFalse(timouted[0]);

            //==========================================
            // Not called when process explicitly killed.
            //==========================================
            assertNull(exitCode[0]);
        } finally {
            handler.killProcess(); // in case, won't do anything
        }
    }
}
