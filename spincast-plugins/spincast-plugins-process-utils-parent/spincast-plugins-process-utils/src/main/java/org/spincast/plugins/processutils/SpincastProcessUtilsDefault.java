package org.spincast.plugins.processutils;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.shaded.org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

public class SpincastProcessUtilsDefault implements SpincastProcessUtils {

    private final SpincastConfig spincastConfig;
    private final SpincastUtils spincastUtils;
    private final TemplatingEngine templatingEngine;

    @Inject
    public SpincastProcessUtilsDefault(SpincastConfig spincastConfig,
                                       SpincastUtils spincastUtils,
                                       TemplatingEngine templatingEngine) {
        this.spincastConfig = spincastConfig;
        this.spincastUtils = spincastUtils;
        this.templatingEngine = templatingEngine;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    protected SpincastUtils getSpincastUtils() {
        return this.spincastUtils;
    }

    protected TemplatingEngine getTemplatingEngine() {
        return this.templatingEngine;
    }

    @Override
    public File executeGoalOnExternalMavenProject(ResourceInfo projectRootInfo,
                                                  MavenProjectGoal mavenGoal) {
        return executeGoalOnExternalMavenProject(projectRootInfo, mavenGoal, null);
    }

    @Override
    public File executeGoalOnExternalMavenProject(ResourceInfo projectRootInfo,
                                                  MavenProjectGoal mavenGoal,
                                                  Map<String, Object> pomParams) {

        try {
            File projectTargetDir = null;

            //==========================================
            // Copies the project from the classpath to
            // the file system, if required.
            //==========================================
            if (!projectRootInfo.isClasspathResource()) {
                projectTargetDir = new File(projectRootInfo.getPath());
            } else {
                projectTargetDir = new File(getSpincastConfig().getTempDir(), UUID.randomUUID().toString());
                getSpincastUtils().copyClasspathDirToFileSystem(projectRootInfo.getPath(), projectTargetDir);
            }

            File pomFile = new File(projectTargetDir, "pom.xml");
            if (!pomFile.isFile()) {
                throw new RuntimeException("The project's pom.xml was not found: " + pomFile.getAbsolutePath());
            }

            //==========================================
            // Tweak the pom.xml
            //==========================================
            if (pomParams != null && pomParams.size() > 0) {
                String pomContent = FileUtils.readFileToString(pomFile, "UTF-8");
                pomContent = getTemplatingEngine().evaluate(pomContent, pomParams);
                FileUtils.write(pomFile, pomContent, "UTF-8");
            }

            //==========================================
            // Execute the specified goal
            //==========================================
            InvocationRequest request = new DefaultInvocationRequest();
            request.setPomFile(pomFile);
            request.setGoals(Collections.singletonList(mavenGoal.getValue()));
            Invoker invoker = new DefaultInvoker();
            invoker.execute(request);

            return projectTargetDir;

        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public void executeJar(String jarFilePath,
                           List<String> args,
                           JarExecutionHandler handler) {
        executeJar("java", jarFilePath, args, handler);
    }

    @Override
    public void executeJar(String javaBinPath,
                           String jarFilePath,
                           List<String> args,
                           JarExecutionHandler handler) {

        if (handler == null) {
            throw new RuntimeException("The handler can't be null");
        }

        boolean[] exceptionOccured = {false};
        Thread jarExecutionThread = new Thread(new Runnable() {

            @Override
            public void run() {
                Process jarProcess = null;
                try {

                    final List<String> cmdArgs = new ArrayList<String>();
                    cmdArgs.add(0, javaBinPath);
                    cmdArgs.add(1, "-jar");
                    cmdArgs.add(2, jarFilePath);
                    if (args != null && args.size() > 0) {
                        cmdArgs.addAll(args);
                    }

                    ProcessBuilder pb = new ProcessBuilder(cmdArgs);
                    pb.redirectOutput(Redirect.INHERIT);
                    pb.redirectError(Redirect.INHERIT);
                    jarProcess = pb.start();
                    handler.setJarProcess(jarProcess);
                    jarProcess.waitFor();

                    int exitVal = jarProcess.exitValue();
                    handler.onExit(exitVal);

                } catch (Exception ex) {
                    exceptionOccured[0] = true;
                    handler.onException(ex);
                }
            }
        });

        jarExecutionThread.start();

        while (!exceptionOccured[0] && handler.getJarProcess() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //...
            }
        }
    }

}
