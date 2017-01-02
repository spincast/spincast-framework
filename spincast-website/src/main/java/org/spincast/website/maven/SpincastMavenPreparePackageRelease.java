package org.spincast.website.maven;

import java.io.File;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.OS;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.bootstrapping.Spincast;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

/**
 * Script ran at the "prepare-package" phase, when building
 * the website using the "release" profile.
 */
public class SpincastMavenPreparePackageRelease extends SpincastMavenScriptBase {

    /**
     * Main method
     */
    public static void main(String[] args) {
        Spincast.configure()
                .mainArgs(args)
                .init();
    }

    private File javadocSourceGenerationDir;

    /**
     * Constructor
     */
    @Inject
    public SpincastMavenPreparePackageRelease(@MainArgs String[] mainArgs) {
        super(mainArgs);
    }

    protected File getJavadocSourceGenerationDir() {

        if(this.javadocSourceGenerationDir == null) {
            this.javadocSourceGenerationDir = new File(getProjectBaseDir().getAbsolutePath() + "/../target/site/apidocs");
        }

        return this.javadocSourceGenerationDir;
    }

    /**
     * Starts the script
     */
    @Override
    @Inject
    protected void init() {

        super.init();

        log("Starting the aggregated Javadoc generation using a custom Spincast script...");

        generateAggregatedJavadoc();

        copyJavadocsToWebsite();
    }

    /**
     * Generates the aggregated Javadoc
     */
    protected void generateAggregatedJavadoc() {

        try {

            log("Generating Aggregated Javadoc...");

            FileUtils.deleteDirectory(getJavadocSourceGenerationDir());

            String mavenHome = getMavenInstallatinRoot().getCanonicalPath();
            String executable = "mvn";
            if(OS.isFamilyWindows()) {
                if(new File(mavenHome, "/bin/mvn.cmd").exists()) {
                    executable = "mvn.cmd";
                } else {
                    executable = "mvn.bat";
                }
            }

            File mvnBin = new File(mavenHome + "/bin/" + executable);
            if(!mvnBin.isFile()) {
                sendException("Maven executable not found: " + mvnBin.getCanonicalPath());
            }

            File frameworkBaseDir = new File(getProjectBaseDir().getAbsolutePath() + "/..");

            CommandLine cmdLine = new CommandLine(mvnBin.getCanonicalPath());
            cmdLine.addArgument("-f");
            cmdLine.addArgument(frameworkBaseDir.getCanonicalPath() + "/pom.xml");
            cmdLine.addArgument("javadoc:aggregate@aggregated-javadoc");
            cmdLine.addArgument("-P");
            cmdLine.addArgument("aggregatedJavadoc");

            log("Command launched to generate the aggregated Javadoc: " + cmdLine);

            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(0);
            ExecuteWatchdog watchdog = new ExecuteWatchdog(180000);
            executor.setWatchdog(watchdog);

            @SuppressWarnings("unused")
            int exitValue = executor.execute(cmdLine);

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    /**
     * Copies the Javadoc to the website
     */
    protected void copyJavadocsToWebsite() {

        try {

            File javadocSourceDir = getJavadocSourceGenerationDir();
            if(!javadocSourceDir.isDirectory()) {
                sendException("The Javadoc source directory was not found: " +
                              javadocSourceDir.getCanonicalPath());
            }

            //==========================================
            // Copies the Javadoc to the build output dir, so it
            // is included in the final website's .jar
            //==========================================
            File javadocTargetDir = new File(getProjectBuildOutputDir().getCanonicalPath() + "/public/javadoc");
            FileUtils.deleteDirectory(javadocTargetDir);

            FileUtils.copyDirectory(javadocSourceDir, javadocTargetDir);
            log("Javadoc copied to the build output dir: " + javadocTargetDir.getAbsolutePath());

            //==========================================
            // We also copy the Javadoc to the source directory of
            // the website, so it is available if we launch the
            // website from an IDE.
            // This source directory is ignored in Git.
            //==========================================
            javadocTargetDir = new File(getProjectBaseDir().getCanonicalPath() + "/src/main/resources/public/javadoc");
            FileUtils.deleteDirectory(javadocTargetDir);

            FileUtils.copyDirectory(javadocSourceDir, javadocTargetDir);
            log("Javadoc copied to the website source dir: " + javadocTargetDir.getAbsolutePath());

        } catch(Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

}
