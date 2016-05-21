package org.spincast.website.maven;

import java.io.File;

import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.ISpincastUtils;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.guice.SpincastDefaultGuiceModule;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Script ran at the "prepare-package" phase, when building
 * the website.
 */
public class SpincastMavenPreparePackage extends SpincastMavenScriptBase {

    /**
     * Main method
     */
    public static void main(String[] args) {

        Injector guice = Guice.createInjector(new SpincastDefaultGuiceModule(args));
        SpincastMavenPreparePackage script = guice.getInstance(SpincastMavenPreparePackage.class);
        script.start();
    }

    private final ISpincastUtils spincastUtils;
    private String projectVersion;

    /**
     * Constructor
     */
    @Inject
    public SpincastMavenPreparePackage(@MainArgs String[] mainArgs,
                                       ISpincastUtils spincastUtils) {
        super(mainArgs);
        this.spincastUtils = spincastUtils;
    }

    protected ISpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected String getProjectVersion() {
        if(this.projectVersion == null) {
            if(getMainArgs().length < 2) {
                sendException("The " + SpincastMavenPreparePackage.class.getName() +
                              " class expect the version of the project " +
                              "to be passed as the second parameter: <argument>${project.version}</argument>");
            }
            this.projectVersion = getMainArgs()[1].trim();
        }

        return this.projectVersion;
    }

    @Override
    protected void init() {
        super.init();
        getProjectVersion();
    }

    /**
     * Starts the script
     */
    protected void start() {

        log("Starting 'prepare-package' phase Spincast script.");

        addQuickStartToWebsite();
    }

    /**
     * Adds a zipped version of the Quick Start to the website.
     */
    protected void addQuickStartToWebsite() {

        try {

            log("Start adding the Quick Start to the website.");

            log("Replacing content in the Quick Start's pom.xml...");

            //==========================================
            // Copies the Quick Start content in a temp directory
            //==========================================
            File targetDir = new File(getProjectBuildDir().getAbsolutePath() + "/spincast-quickstart");

            File quickStartProjectRoot = new File(getProjectBaseDir().getAbsolutePath() + "/../spincast-quickstart");

            FileUtils.copyDirectory(new File(quickStartProjectRoot.getAbsolutePath() + "/src"),
                                    new File(targetDir.getAbsolutePath() + "/src"));

            FileUtils.copyDirectory(new File(quickStartProjectRoot.getAbsolutePath() + "/varia"),
                                    new File(targetDir.getAbsolutePath() + "/varia"));

            File pomTarget = new File(targetDir.getAbsolutePath() + "/pom.xml");
            FileUtils.copyFile(new File(quickStartProjectRoot.getAbsolutePath() + "/pom.xml"),
                               pomTarget);

            //==========================================
            // Replaces some sections in the pom.xml
            //==========================================
            String pomContent = FileUtils.readFileToString(pomTarget, "UTF-8");

            String projectVersion = getProjectVersion();
            pomContent = pomContent.replace("${project.version}", projectVersion);

            // @formatter:off
            String cleanCoords = "<groupId>org.spincast</groupId>\n" +
                                 "    <artifactId>spincast-quickstart</artifactId>\n" +
                                 "    <version>1.0.0-SNAPSHOT</version>";
            // @formatter:on

            pomContent = pomContent.replaceAll("(?s)<!-- SPINCAST_COORDINATES -->.*<!-- /SPINCAST_COORDINATES -->", cleanCoords);

            String snapshotRepo = "";
            if(projectVersion.endsWith("-SNAPSHOT")) {
                snapshotRepo = "$1";
            }
            pomContent =
                    pomContent.replaceAll("(?s)<!-- SPINCAST_SNAPSHOTS_REPO -->(.*)<!-- /SPINCAST_SNAPSHOTS_REPO -->",
                                          snapshotRepo);

            FileUtils.writeStringToFile(pomTarget, pomContent, "UTF-8");

            //==========================================
            // Zips the modified Quick Start and adds
            // it to the build output dir.
            //==========================================
            File targetZipFile = new File(getProjectBuildOutputDir().getAbsolutePath() +
                                          "/public/quickstart/spincast-quick-start.zip");

            getSpincastUtils().zipDirectory(targetDir, targetZipFile, true);

            //==========================================
            // Copies the zip to the source directory too, so it
            // is available when running from an IDE.
            //==========================================
            FileUtils.copyFile(targetZipFile, new File(getProjectBaseDir().getAbsolutePath() +
                                                       "/src/main/resources/public/quickstart/spincast-quick-start.zip"));

            log("Quick Start .zip file generated and added to the website.");

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }
}
