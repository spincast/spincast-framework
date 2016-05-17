package org.spincast.website.maven;

import java.io.File;

import com.google.inject.Inject;

/**
 * Base class for scripts running durring a Maven building
 * phase.
 */
public abstract class SpincastMavenScriptBase {

    private final String[] mainArgs;
    private File projectBaseDir;
    private File projectBuildDir;
    private File projectBuildOutputDir;
    private File mavenHome;

    /**
     * Constructor
     */
    public SpincastMavenScriptBase(String[] mainArgs) {
        this.mainArgs = mainArgs;
    }

    @Inject
    protected void init() {

        // Validate paths
        getProjectBaseDir();
        getMavenInstallatinRoot();
    }

    protected String[] getMainArgs() {
        return this.mainArgs;
    }

    protected File getProjectBaseDir() {
        if(this.projectBaseDir == null) {

            if(getMainArgs().length == 0) {
                sendException("The " + SpincastMavenPreparePackageRelease.class.getName() +
                              " class expect the base directory of the project " +
                              "to be passed as the first parameter:  <argument>${project.basedir}</argument>");
            }
            String baseDirPath = getMainArgs()[0];
            this.projectBaseDir = new File(baseDirPath);

            if(!this.projectBaseDir.isDirectory()) {
                sendException("The first parameter must be the base directory of the project. The specified directory " +
                              "doesn't exist: " + baseDirPath);
            }
        }
        return this.projectBaseDir;
    }

    protected File getProjectBuildDir() {
        if(this.projectBuildDir == null) {
            this.projectBuildDir = new File(getProjectBaseDir().getAbsolutePath() + "/target");
        }
        return this.projectBuildDir;
    }

    protected File getProjectBuildOutputDir() {
        if(this.projectBuildOutputDir == null) {
            this.projectBuildOutputDir = new File(getProjectBuildDir().getAbsolutePath() + "/classes");
        }
        return this.projectBuildOutputDir;
    }

    protected File getMavenInstallatinRoot() {
        if(this.mavenHome == null) {

            String mavenHomePath = System.getenv("M2_HOME");
            if(mavenHomePath == null) {
                sendException("The 'M2_HOME' environment variable is required to locate the Maven home.");
            }

            this.mavenHome = new File(mavenHomePath);
            if(!this.mavenHome.isDirectory()) {
                sendException("Maven home specified by M2_HOME not found: " + mavenHomePath);
            }

        }
        return this.mavenHome;
    }

    protected void sendException(String message) {

        StringBuilder builder = new StringBuilder();
        builder.append("\n!\n!\n! ==========================================\n");
        builder.append("! SPINCAST MAVEN BUILD ERROR:\n!\n");
        builder.append("! ").append(message);
        builder.append("\n! ==========================================\n!\n!\n");

        throw new RuntimeException(builder.toString());
    }

    protected void log(String message) {
        System.out.println("[INFO-SPINCAST] " + message);
    }

}
