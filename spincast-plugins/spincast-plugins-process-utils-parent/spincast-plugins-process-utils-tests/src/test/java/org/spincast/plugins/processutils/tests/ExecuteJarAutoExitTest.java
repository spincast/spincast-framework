package org.spincast.plugins.processutils.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.processutils.JarExecutionHandlerDefault;
import org.spincast.plugins.processutils.MavenProjectGoal;
import org.spincast.plugins.processutils.SpincastProcessUtils;
import org.spincast.plugins.processutils.SpincastProcessUtilsPlugin;
import org.spincast.testing.core.utils.SpincastTestingUtils;
import org.spincast.testing.core.utils.TrueChecker;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ExecuteJarAutoExitTest extends NoAppTestingBase {

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
    public void executeJarExit0() throws Exception {

        int[] exitCode = new int[]{-1};
        JarExecutionHandlerDefault handler = new JarExecutionHandlerDefault() {

            @Override
            public void onExit(int exitVal) {
                exitCode[0] = exitVal;
            }
        };

        getSpincastProcessUtils().executeJar(getMavenProjectJarFile().getAbsolutePath(),
                                             Lists.newArrayList("0"),
                                             handler);
        try {

            SpincastTestingUtils.waitForTrue(new TrueChecker() {

                @Override
                public boolean check() {
                    return exitCode[0] > -1;
                }
            }, 5000);

            assertEquals(0, exitCode[0]);

        } finally {
            handler.killJarProcess();
        }
    }

    @Test
    public void executeJarExit1() throws Exception {

        int[] exitCode = new int[]{-1};
        JarExecutionHandlerDefault handler = new JarExecutionHandlerDefault() {

            @Override
            public void onExit(int exitVal) {
                exitCode[0] = exitVal;
            }
        };

        getSpincastProcessUtils().executeJar(getMavenProjectJarFile().getAbsolutePath(),
                                             Lists.newArrayList("1"),
                                             handler);
        try {

            SpincastTestingUtils.waitForTrue(new TrueChecker() {

                @Override
                public boolean check() {
                    return exitCode[0] > -1;
                }
            }, 5000);

            assertEquals(1, exitCode[0]);

        } finally {
            handler.killJarProcess();
        }
    }

    @Test
    public void executeJarExit2() throws Exception {

        int[] exitCode = new int[]{-1};
        JarExecutionHandlerDefault handler = new JarExecutionHandlerDefault() {

            @Override
            public void onExit(int exitVal) {
                exitCode[0] = exitVal;
            }
        };

        getSpincastProcessUtils().executeJar(getMavenProjectJarFile().getAbsolutePath(),
                                             Lists.newArrayList("2"),
                                             handler);
        try {

            SpincastTestingUtils.waitForTrue(new TrueChecker() {

                @Override
                public boolean check() {
                    return exitCode[0] > -1;
                }
            }, 5000);

            assertEquals(2, exitCode[0]);

        } finally {
            handler.killJarProcess();
        }
    }

    @Test
    public void executeJarExitException() throws Exception {

        int[] exitCode = new int[]{-1};
        JarExecutionHandlerDefault handler = new JarExecutionHandlerDefault() {

            @Override
            public void onExit(int exitVal) {
                exitCode[0] = exitVal;
            }
        };

        getSpincastProcessUtils().executeJar(getMavenProjectJarFile().getAbsolutePath(),
                                             Lists.newArrayList("100"), // 100 => throw exception
                                             handler);
        try {

            SpincastTestingUtils.waitForTrue(new TrueChecker() {

                @Override
                public boolean check() {
                    return exitCode[0] > -1;
                }
            }, 5000);

            assertEquals(1, exitCode[0]);

        } finally {
            handler.killJarProcess();
        }
    }

    @Test
    public void executeJarStandardExit() throws Exception {

        int[] exitCode = new int[]{-1};
        JarExecutionHandlerDefault handler = new JarExecutionHandlerDefault() {

            @Override
            public void onExit(int exitVal) {
                exitCode[0] = exitVal;
            }
        };

        getSpincastProcessUtils().executeJar(getMavenProjectJarFile().getAbsolutePath(),
                                             Lists.newArrayList("1000"), // 1000 => standard exit
                                             handler);
        try {

            SpincastTestingUtils.waitForTrue(new TrueChecker() {

                @Override
                public boolean check() {
                    return exitCode[0] > -1;
                }
            }, 5000);

            assertEquals(0, exitCode[0]);

        } finally {
            handler.killJarProcess();
        }
    }

}
