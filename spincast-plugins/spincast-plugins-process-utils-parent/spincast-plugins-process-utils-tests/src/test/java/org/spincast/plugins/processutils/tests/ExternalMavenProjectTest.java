package org.spincast.plugins.processutils.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.spincast.core.guice.SpincastPlugin;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.processutils.MavenProjectGoal;
import org.spincast.plugins.processutils.SpincastProcessUtils;
import org.spincast.plugins.processutils.SpincastProcessUtilsPlugin;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.testing.defaults.NoAppTestingBase;

import com.google.inject.Inject;

public class ExternalMavenProjectTest extends NoAppTestingBase {

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

    protected String getSpincastVersionToUse() {
        return "1.3.1-SNAPSHOT";
    }

    @Test
    public void fromClasspath() throws Exception {
        File projectDir = getSpincastProcessUtils().executeGoalOnExternalMavenProject(new ResourceInfo("/externalMavenProject",
                                                                                                       true),
                                                                                      MavenProjectGoal.PACKAGE,
                                                                                      SpincastStatics.map("spincastVersion",
                                                                                                          getSpincastVersionToUse()));

        File pomFile = new File(projectDir, "pom.xml");
        File jarFile = new File(projectDir, "target/spincast-test-" + getSpincastVersionToUse() + ".jar");
        assertTrue(pomFile.isFile());
        assertTrue(jarFile.isFile());

        String pomContent = FileUtils.readFileToString(pomFile, "UTF-8");
        assertFalse(pomContent.contains("<version>{{ spincastVersion }}</version>"));
        assertTrue(pomContent.contains("<version>" + getSpincastVersionToUse() + "</version>"));
    }

    @Test
    public void fromFileSystem() throws Exception {

        File projectDir = createTestingDir();

        getSpincastUtils().copyClasspathDirToFileSystem("/externalMavenProject", projectDir);
        assertTrue(new File(projectDir, "pom.xml").isFile());
        assertFalse(new File(projectDir, "target/spincast-test-" + getSpincastVersionToUse() + ".jar").isFile());

        File projectDir2 =
                getSpincastProcessUtils().executeGoalOnExternalMavenProject(new ResourceInfo(projectDir.getAbsolutePath(),
                                                                                             false),
                                                                            MavenProjectGoal.INSTALL,
                                                                            SpincastStatics.map("spincastVersion",
                                                                                                getSpincastVersionToUse()));

        assertEquals(projectDir.getAbsolutePath(), projectDir2.getAbsolutePath());

        File pomFile = new File(projectDir, "pom.xml");
        assertTrue(pomFile.isFile());
        assertTrue(new File(projectDir, "target/spincast-test-" + getSpincastVersionToUse() + ".jar").isFile());

        String pomContent = FileUtils.readFileToString(pomFile, "UTF-8");
        assertFalse(pomContent.contains("<version>{{ spincastVersion }}</version>"));
        assertTrue(pomContent.contains("<version>" + getSpincastVersionToUse() + "</version>"));
    }

}
