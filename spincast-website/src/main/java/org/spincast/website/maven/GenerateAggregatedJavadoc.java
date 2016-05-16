package org.spincast.website.maven;

import java.io.File;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.OS;
import org.spincast.core.guice.MainArgs;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.defaults.guice.SpincastDefaultGuiceModule;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Generates the aggregated Javadoc for all 
 * Spincast modules and adds it to the website.
 * 
 * <p>
 * Called by a "exec-maven-plugin" Maven plugin.
 * </p>
 * 
 * <p>
 * Uses Spincast itself! So we have access to all the utlities if required.
 * It is currently not required to do so, but it's pretty cool and very fast,
 * so why not! Of course we don't start any HTTP server though...
 * </p>
 */
public class GenerateAggregatedJavadoc {

    /**
     * Guice module specific to this script, if required.
     */
    public static class SpincastMavenPluginPackageModule extends AbstractModule {

        @Override
        protected void configure() {
            //...
        }
    }

    /**
     * Main method
     */
    public static void main(String[] args) {

        Injector guice = Guice.createInjector(new SpincastDefaultGuiceModule(args),
                                              new SpincastMavenPluginPackageModule());

        GenerateAggregatedJavadoc spincastMavenPluginPackage =
                guice.getInstance(GenerateAggregatedJavadoc.class);
        spincastMavenPluginPackage.start();
    }

    private final String[] mainArgs;
    private File projectBaseDir;
    private File projectBuildOutputDir;
    private File mavenInstallatinRoot;
    private File javadocSourceGenerationDir;

    /**
     * Constructor
     */
    @Inject
    public GenerateAggregatedJavadoc(@MainArgs String[] mainArgs) {
        this.mainArgs = mainArgs;
    }

    protected String[] getMainArgs() {
        return this.mainArgs;
    }

    protected File getProjectBaseDir() {
        if(this.projectBaseDir == null) {

            if(getMainArgs().length == 0) {
                sendException("The " + GenerateAggregatedJavadoc.class.getName() +
                              " class expect the base directory of the project " +
                              "to be passed as the first parameter:  <arguments><argument>${project.basedir}</argument></arguments>");
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

    protected File getProjectBuildOutputDir() {
        if(this.projectBuildOutputDir == null) {

            if(getMainArgs().length < 2) {
                sendException("The " + GenerateAggregatedJavadoc.class.getName() +
                              " class expect the build output directory of the project " +
                              "to be passed as the second parameter:  <arguments><argument>${project.build.outputDirectory}</argument></arguments>");
            }
            String buildDirPath = getMainArgs()[1];
            this.projectBuildOutputDir = new File(buildDirPath);

            if(!this.projectBuildOutputDir.isDirectory()) {
                sendException("The second parameter must be the build output directory of the project. The specified directory " +
                              "doesn't exist: " + buildDirPath);
            }
        }
        return this.projectBuildOutputDir;
    }

    protected File getMavenInstallatinRoot() {
        if(this.mavenInstallatinRoot == null) {

            if(getMainArgs().length < 3) {
                sendException("The " + GenerateAggregatedJavadoc.class.getName() +
                              " class expect the Maven installation directory (M2_HOME) " +
                              "to be passed as the third parameter:  <arguments><argument>${env.M2_HOME}</argument></arguments>");
            }
            String mavenHome = getMainArgs()[2];
            this.mavenInstallatinRoot = new File(mavenHome);

            if(!this.mavenInstallatinRoot.isDirectory()) {
                sendException("The third parameter must be the Maven installation directory. The specified directory " +
                              "doesn't exist: " + mavenHome);
            }

            if(!(new File(mavenHome + "/bin/").isDirectory())) {
                sendException("The specified Maven home doesn't seem to be valid: " + mavenHome);
            }

        }
        return this.mavenInstallatinRoot;
    }

    protected File getJavadocSourceGenerationDir() {

        if(this.javadocSourceGenerationDir == null) {
            this.javadocSourceGenerationDir = new File(getProjectBaseDir().getAbsolutePath() + "/../target/site/apidocs");
        }

        return this.javadocSourceGenerationDir;
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

    /**
     * Starts the script
     */
    protected void start() {

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
