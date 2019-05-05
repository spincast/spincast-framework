package org.spincast.plugins.processutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.core.utils.SpincastStatics;
import org.spincast.core.utils.SpincastUtils;
import org.spincast.plugins.processutils.exceptions.LaunchException;
import org.spincast.plugins.processutils.exceptions.TimeoutException;
import org.spincast.shaded.org.apache.commons.io.FileUtils;
import org.spincast.shaded.org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

public class SpincastProcessUtilsDefault implements SpincastProcessUtils {

    protected static final Logger logger = LoggerFactory.getLogger(SpincastProcessUtilsDefault.class);

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
    public void executeAsync(ProcessExecutionHandler handler, String... cmdArgs) {
        executeAsync(handler, -1, null, cmdArgs != null ? Arrays.asList(cmdArgs) : null);
    }

    @Override
    public void executeAsync(ProcessExecutionHandler handler, long timeoutAmount, TimeUnit timeoutUnit, String... cmdArgs) {
        executeAsync(handler, timeoutAmount, timeoutUnit, cmdArgs != null ? Arrays.asList(cmdArgs) : null);
    }

    @Override
    public void executeAsync(ProcessExecutionHandler handler, List<String> cmdArgs) {
        executeAsync(handler, -1, null, cmdArgs);
    }

    @Override
    public void executeAsync(ProcessExecutionHandler handler,
                             long timeoutAmount,
                             TimeUnit timeoutUnit,
                             List<String> cmdArgs) {

        if (cmdArgs == null || cmdArgs.size() == 0) {
            throw new RuntimeException("There must be at least one command argument!");
        }

        if (handler == null) {
            throw new RuntimeException("The handler can't be null");
        }

        boolean[] processCreated = {false};
        boolean[] processEnded = {false};
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                CountDownLatch onEndLatch = new CountDownLatch(1);
                try {

                    ProcessBuilder pb = new ProcessBuilder(cmdArgs);
                    pb = configureStreams(pb);

                    String cmd = StringUtils.join(cmdArgs, " ");
                    if (timeoutAmount >= 0) {

                        String timeoutUnitFriendly = timeoutUnit.name().toLowerCase();
                        if (timeoutAmount == 1) {
                            timeoutUnitFriendly = StringUtils.removeEnd(timeoutUnitFriendly, "s");
                        }

                        logger.info("Executing command (timeout: " + timeoutAmount + " " + timeoutUnitFriendly + ") : " + cmd);

                    } else {
                        logger.info("Executing command (no timeout) : " + cmd);
                    }

                    Process process = pb.start();
                    try {
                        processCreated[0] = true;
                        startSystemOutReader(process, handler);
                        startSystemErrReader(process, handler);

                        AtomicBoolean processKillerCalled = new AtomicBoolean(false);
                        ProcessKiller processKiller = new ProcessKiller() {

                            @Override
                            public void killProcess() {
                                if (processKillerCalled.get()) {
                                    return;
                                }
                                processKillerCalled.set(true);
                                SpincastProcessUtilsDefault.this.killProcess(process);
                                try {
                                    onEndLatch.await();
                                } catch (Exception ex) {
                                    // ok
                                }
                            }
                        };

                        handler.setProcessAndProcessKiller(process, processKiller);

                        if (timeoutAmount >= 0) {

                            try {
                                boolean exitedProperly = process.waitFor(timeoutAmount, timeoutUnit);
                                if (!exitedProperly && !processKillerCalled.get()) {
                                    handler.onTimeoutException();
                                    return;
                                }
                            } catch (Exception ex) {
                                throw SpincastStatics.runtimize(ex);
                            }

                        } else {
                            process.waitFor();
                        }

                        //==========================================
                        // onExit => only if the process was not
                        // specifically killed.
                        //==========================================
                        if (!processKillerCalled.get()) {
                            int exitVal = process.exitValue();
                            handler.onExit(exitVal);
                        }

                    } finally {
                        //==========================================
                        // Make sure the process is always killed.
                        //==========================================
                        killProcess(process);
                    }

                } catch (Exception ex) {
                    handler.onLaunchException(ex);
                } finally {
                    try {
                        handler.onEnd();
                    } finally {
                        processEnded[0] = true;
                        onEndLatch.countDown();
                    }
                }
            }
        });

        thread.start();

        //==========================================
        // Wait for the process to be created or to
        // be ended before returning.
        //==========================================
        while (!processCreated[0] && !processEnded[0]) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //...
            }
        }
    }

    protected void killProcess(Process process) {
        if (process == null || !process.isAlive()) {
            return;
        }

        try {
            logger.info("Killing the process...");

            process.destroy();

            int secondNbr = 0;
            while (secondNbr++ < 10) {
                if (!process.isAlive()) {
                    break;
                }
                Thread.sleep(1000);
            }

            //==========================================
            // Force
            //==========================================
            if (process.isAlive()) {
                process.destroyForcibly();
            }
            logger.info("Process killed.");
        } catch (Exception ex) {

            //==========================================
            // Last chance before an exception
            //==========================================
            try {
                Thread.sleep(100);
            } catch (Exception ex2) {
                // ...
            }
            if (!process.isAlive()) {
                return;
            }

            throw new RuntimeException("Unable to kill the process properly", ex);
        }
    }


    /**
     * Configure input/output streams
     */
    protected ProcessBuilder configureStreams(ProcessBuilder pb) {
        pb = pb.redirectInput(Redirect.PIPE);
        pb = pb.redirectOutput(Redirect.PIPE);
        pb = pb.redirectError(Redirect.PIPE);
        return pb;
    }

    protected void startSystemOutReader(Process process, ProcessExecutionHandler handler) {
        InputStream inputStream = process.getInputStream();
        if (inputStream != null) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    Scanner sc = new Scanner(new BufferedReader(new InputStreamReader(inputStream)));
                    try {
                        while (sc.hasNextLine()) {
                            handler.onSystemOut(sc.nextLine());
                        }
                    } finally {
                        SpincastStatics.closeQuietly(sc);
                    }
                }
            }).start();
        }
    }

    protected void startSystemErrReader(Process process, ProcessExecutionHandler handler) {
        InputStream inputStream = process.getErrorStream();
        if (inputStream != null) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    Scanner sc = new Scanner(new BufferedReader(new InputStreamReader(inputStream)));
                    try {
                        while (sc.hasNextLine()) {
                            handler.onSystemErr(sc.nextLine());
                        }
                    } finally {
                        SpincastStatics.closeQuietly(sc);
                    }
                }
            }).start();
        }
    }

    protected void waitForStreamsToBeEmpty(Thread systemOutReaderThread, Thread systemErrReaderThread) {

        try {
            if (systemOutReaderThread != null) {
                systemOutReaderThread.join();
            }
            if (systemErrReaderThread != null) {
                systemErrReaderThread.join();
            }
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    @Override
    public SyncExecutionResult executeSync(long timeoutAmount, TimeUnit timeoutUnit,
                                           String... cmdArgs) throws LaunchException,
                                                              TimeoutException {
        return executeSync(timeoutAmount, timeoutUnit, cmdArgs != null ? Arrays.asList(cmdArgs) : null);
    }


    @Override
    public SyncExecutionResult executeSync(long timeoutAmount,
                                           TimeUnit timeoutUnit,
                                           List<String> cmdArgs) throws LaunchException,
                                                                 TimeoutException {
        return executeSync(timeoutAmount,
                           timeoutUnit,
                           ExecutionOutputStrategy.SYSTEM,
                           cmdArgs);
    }

    @Override
    public SyncExecutionResult executeSync(long timeoutAmount, TimeUnit timeoutUnit,
                                           ExecutionOutputStrategy executionOutputStrategy,
                                           String... cmdArgs) throws LaunchException, TimeoutException {
        return executeSync(timeoutAmount,
                           timeoutUnit,
                           executionOutputStrategy,
                           cmdArgs != null ? Arrays.asList(cmdArgs) : null);
    }

    @Override
    public SyncExecutionResult executeSync(long timeoutAmount,
                                           TimeUnit timeoutUnit,
                                           ExecutionOutputStrategy executionOutputStrategy,
                                           List<String> cmdArgs) throws LaunchException, TimeoutException {

        if (timeoutAmount <= 0) {
            throw new RuntimeException("The timeoutAmount must be greater than 0.");
        }
        Objects.requireNonNull(timeoutUnit, "The timeoutUnit can't be NULL");

        ProcessExecutionHandlerSync handler = null;
        try {
            CountDownLatch latch = new CountDownLatch(1);

            handler = new ProcessExecutionHandlerSync(latch, executionOutputStrategy);
            executeAsync(handler, timeoutAmount, timeoutUnit, cmdArgs);

            latch.await();

            SyncExecutionResult syncExecutionResult = handler.getSyncExecutionResult();

            if (handler.getLaunchException() != null) {
                throw new LaunchException(handler.getLaunchException());
            } else if (handler.isTimeoutException()) {
                throw new TimeoutException(syncExecutionResult);
            }

            return syncExecutionResult;

        } catch (LaunchException | TimeoutException ex) {
            throw ex;
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        } finally {
            if (handler != null) {
                handler.killProcess();
            }
        }
    }

}
