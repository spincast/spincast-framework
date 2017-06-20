package org.spincast.website.maven;

import java.io.File;
import java.io.FilenameFilter;

import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

/**
 * Script ran at the "prepare-package" phase, when building
 * the website.
 */
public class SpincastMavenPreparePackage extends SpincastMavenScriptBase {

    /**
     * Main method
     */
    public static void main(String[] args) {
        Spincast.configure()
                .init(args);
    }

    private final SpincastUtils spincastUtils;
    private String projectVersion;

    /**
     * Constructor
     */
    @Inject
    public SpincastMavenPreparePackage(@MainArgs String[] mainArgs,
                                       SpincastUtils spincastUtils) {
        super(mainArgs);
        this.spincastUtils = spincastUtils;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected String getProjectVersion() {
        if (this.projectVersion == null) {
            if (getMainArgs().length < 2) {
                sendException("The " + SpincastMavenPreparePackage.class.getName() +
                              " class expect the version of the project " +
                              "to be passed as the second parameter: <argument>${project.version}</argument>");
            }
            this.projectVersion = getMainArgs()[1].trim();
        }

        return this.projectVersion;
    }

    @Override
    @Inject
    protected void init() {
        super.init();

        log("Starting 'prepare-package' phase Spincast script.");

        addQuickStartToWebsite();
        addDemoAppsToWebsite();
    }

    /**
     * Adds a zipped version of the Quick Start to the website.
     */
    protected void addQuickStartToWebsite() {

        File quickStartProjectRoot = new File(getProjectBaseDir().getAbsolutePath() + "/../spincast-quickstart");
        File targetZipFile = new File(getProjectBuildOutputDir().getAbsolutePath() +
                                      "/public/quickstart/spincast-quick-start.zip");
        File targetZipFileSrc = new File(getProjectBaseDir().getAbsolutePath() +
                                         "/src/main/resources/public/quickstart/spincast-quick-start.zip");

        addAppToWebsite(quickStartProjectRoot,
                        targetZipFile,
                        targetZipFileSrc,
                        "org.spincast",
                        "spincast-quickstart",
                        "1.0.0-SNAPSHOT");
    }

    /**
     * Adds a zipped version of the demo apps to the website.
     */
    protected void addDemoAppsToWebsite() {

        try {

            log("Start adding the demo apps to the website.");

            //==========================================
            // Demo apps root dir
            //==========================================
            File demoAppsRootDir = new File(getProjectBaseDir().getAbsolutePath() + "/demo-apps");
            if (!demoAppsRootDir.isDirectory()) {
                throw new RuntimeException("Root directory for demo apps not found : " + demoAppsRootDir.getAbsolutePath());
            }

            String[] appDirNames = demoAppsRootDir.list(new FilenameFilter() {

                @Override
                public boolean accept(File current, String name) {
                    return new File(current, name).isDirectory();
                }
            });
            for (String appDirName : appDirNames) {
                File demoAppRootDir = new File(demoAppsRootDir + "/" + appDirName);
                if (new File(demoAppRootDir.getAbsolutePath() + "/pom.xml").isFile()) {

                    String artifactId = "spincast-demos-" + appDirName;

                    String zipFileName = artifactId + ".zip";
                    File targetZipFile = new File(getProjectBuildOutputDir().getAbsolutePath() +
                                                  "/public/demo-apps/" + zipFileName);

                    File targetZipFileSrc = new File(getProjectBaseDir().getAbsolutePath() +
                                                     "/src/main/resources/public/demo-apps/" + zipFileName);

                    addAppToWebsite(demoAppRootDir,
                                    targetZipFile,
                                    targetZipFileSrc,
                                    "org.spincast.demos",
                                    artifactId,
                                    getProjectVersion());
                }
            }

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    protected void addAppToWebsite(File appRootDir,
                                   File targetZipFile,
                                   File targetZipFileSrc,
                                   String groupId,
                                   String artifactId,
                                   String version) {
        try {

            log("Start adding the app " + appRootDir + " to the website...");

            log("Replacing content in pom.xml...");

            //==========================================
            // Copies the app content in a temp directory
            //==========================================
            File targetDir = new File(getProjectBuildDir().getAbsolutePath() + "/" + artifactId);

            FileUtils.copyDirectory(new File(appRootDir.getAbsolutePath() + "/src"),
                                    new File(targetDir.getAbsolutePath() + "/src"));

            File variaDir = new File(appRootDir.getAbsolutePath() + "/varia");
            if (variaDir.isDirectory()) {
                FileUtils.copyDirectory(variaDir, new File(targetDir.getAbsolutePath() + "/varia"));
            }

            File pomTarget = new File(targetDir.getAbsolutePath() + "/pom.xml");
            FileUtils.copyFile(new File(appRootDir.getAbsolutePath() + "/pom.xml"),
                               pomTarget);

            File readme = new File(appRootDir.getAbsolutePath() + "/readme.md");
            File readmeTarget = new File(targetDir.getAbsolutePath() + "/readme.md");
            if (readme.isFile()) {
                FileUtils.copyFile(readme, readmeTarget);
            }

            File gitIgnore = new File(appRootDir.getAbsolutePath() + "/.gitignore");
            if (gitIgnore.isFile()) {
                FileUtils.copyFile(gitIgnore, new File(targetDir.getAbsolutePath() + "/.gitignore"));
            }
            
            File configFile = new File(appRootDir.getAbsolutePath() + "/app-config.yaml");
            if (configFile.isFile()) {
                FileUtils.copyFile(configFile, new File(targetDir.getAbsolutePath() + "/app-config.yaml"));
            }

            //==========================================
            // Replaces some sections in the pom.xml
            //==========================================
            String pomContent = FileUtils.readFileToString(pomTarget, "UTF-8");

            String projectVersion = getProjectVersion();
            pomContent = pomContent.replace("${project.version}", projectVersion);

            // @formatter:off
            String cleanCoords = "<groupId>" + groupId + "</groupId>\n" +
                                 "    <artifactId>" + artifactId + "</artifactId>\n" +
                                 "    <version>" + version + "</version>";
            // @formatter:on

            pomContent = pomContent.replaceAll("(?s)<!-- SPINCAST_COORDINATES -->.*<!-- /SPINCAST_COORDINATES -->", cleanCoords);

            pomContent = pomContent.replace("<!--$NO-MVN-MAN-VER$-->", "");

            String snapshotRepo = "";
            if (projectVersion.endsWith("-SNAPSHOT")) {
                snapshotRepo = "$1";
            }
            pomContent =
                    pomContent.replaceAll("(?s)<!-- SPINCAST_SNAPSHOTS_REPO -->(.*)<!-- /SPINCAST_SNAPSHOTS_REPO -->",
                                          snapshotRepo);

            FileUtils.writeStringToFile(pomTarget, pomContent, "UTF-8");

            //==========================================
            // Replaces some sections in the readme.md
            //==========================================
            if (readme.isFile()) {
                String readmeContent = FileUtils.readFileToString(readmeTarget, "UTF-8");
                readmeContent = readmeContent.replace("${project.version}", projectVersion);
                FileUtils.writeStringToFile(readmeTarget, readmeContent, "UTF-8");
            }

            //==========================================
            // Zips the modified app
            //==========================================
            getSpincastUtils().zipDirectory(targetDir, targetZipFile, true);

            //==========================================
            // Copies the zip to the source directory too, so it
            // is available when running from an IDE.
            //==========================================
            FileUtils.copyFile(targetZipFile, targetZipFileSrc);

            log("App .zip file generated and added to the website.");

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

}
