package org.spincast.plugins.processutils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ResourceInfo;
import org.spincast.plugins.processutils.exceptions.LaunchException;
import org.spincast.plugins.processutils.exceptions.TimeoutException;

/**
 * Utilities related to processes, external programs and
 * projects manipulation.
 */
public interface SpincastProcessUtils {

    /**
     * Execute the specified <code>goal</code> on an external
     * Maven project.
     * <p>
     * If the project is located on the classpath, it
     * first copies it to the file system (in a temp folder).
     *
     * @return the root directory of the project. This will be
     * the same as the specified path if on the file system,
     * or will be the created temp directory where the project
     * has been copied (from the classpath) otherwise.
     */
    public File executeGoalOnExternalMavenProject(ResourceInfo projectRootInfo,
                                                  MavenProjectGoal mavenGoal);

    /**
     * Execute the specified <code>goal</code> on an external
     * Maven project.
     * <p>
     * If the project is located on the classpath, it
     * first copies it to the file system (in a temp folder).
     *
     * @param pomParams Before executing the <code>goal</code>, those parameters
     * are used to replace placeholders in the project's <code>pom.xml</code>
     * using Spincast's {@link TemplatingEngine}.
     *
     * @return the root directory of the project. This will be
     * the same as the specified path if on the file system,
     * or will be the created temp directory where the project
     * has been copied (from the classpath) otherwise.
     *
     */
    public File executeGoalOnExternalMavenProject(ResourceInfo projectRootInfo,
                                                  MavenProjectGoal mavenGoal,
                                                  Map<String, Object> pomParams);

    /**
     * Execute an external program asynchronously.
     * <p>
     * The method will only return when the process is actually
     * created or if an exception occured when trying to do so.
     * <p>
     * If this creates a process that is not made
     * to exit automatically (for example it starts an
     * HTTP server), you must kill the process
     * by yourself!:
     * <pre>
     * ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault();
     * {
     *     // override some methods...
     * };
     * getSpincastProcessUtils().executeAsync(handler,
     *                                        "java",
     *                                        "-jar",
     *                                        jarFile.getAbsolutePath());
     * try {
     *     //...
     * } finally {
     *     handler.killProcess();
     * }
     * </pre>
     *
     * @param handler to get information from the created process
     * and to be able to kill it.
     */
    public void executeAsync(ProcessExecutionHandler handler,
                             String... cmdArgs);

    /**
     * Execute an external program asynchronously.
     * <p>
     * The method will only return when the process is actually
     * created or if an exception occured when trying to do so.
     * <p>
     * If this creates a process that is not made
     * to exit automatically (for example it starts an
     * HTTP server), you must kill the process
     * by yourself!:
     * <pre>
     * ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault();
     * {
     *     // override some methods...
     * };();
     * getSpincastProcessUtils().executeAsync(handler,
     *                                        "java",
     *                                        "-jar",
     *                                        jarFile.getAbsolutePath());
     * try {
     *     //...
     * } finally {
     *     handler.killProcess();
     * }
     * </pre>
     *
     * @param handler to get information from the created process
     * and to be able to kill it.
     *
     * @param timeoutAmount the amount of time the external program
     * is allowed to run before a timeout occurs. When the timeout occurs:
     * <ul>
     * <li>
     * The process is killed
     * </li>
     * <li>
     * {@link ProcessExecutionHandler#onTimeoutException() onTimeoutException()} is called
     * </li>
     * </ul>
     * Note that {@link ProcessExecutionHandler#onExit(int) onExit()} will not be called
     * if a timeout occurs!
     *
     * @param timeoutUnit the timeout unit.
     */
    public void executeAsync(ProcessExecutionHandler handler,
                             long timeoutAmount,
                             TimeUnit timeoutUnit,
                             String... cmdArgs);

    /**
     * Execute an external program asynchronously.
     * <p>
     * The method will only return when the process is actually
     * created or if an exception occured when trying to do so.
     * <p>
     * If this creates a process that is not made
     * to exit automatically (for example it starts an
     * HTTP server), you must kill the process
     * by yourself!:
     * <pre>
     * ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault();
     * {
     *     // override some methods...
     * };
     * getSpincastProcessUtils().executeAsync(handler,
     *                                        "java",
     *                                        "-jar",
     *                                        jarFile.getAbsolutePath());
     * try {
     *     //...
     * } finally {
     *     handler.killProcess();
     * }
     * </pre>
     *
     * @param handler to get information from the created process
     * and to be able to kill it.
     *
     */
    public void executeAsync(ProcessExecutionHandler handler,
                             List<String> cmdArgs);

    /**
     * Execute an external program asynchronously.
     * <p>
     * The method will only return when the process is actually
     * created or if an exception occured when trying to do so.
     * <p>
     * If this creates a process that is not made
     * to exit automatically (for example it starts an
     * HTTP server), you must kill the process
     * by yourself!:
     * <pre>
     * ProcessExecutionHandlerDefault handler = new ProcessExecutionHandlerDefault();
     * {
     *     // override some methods...
     * };
     * getSpincastProcessUtils().executeAsync(handler,
     *                                        "java",
     *                                        "-jar",
     *                                        jarFile.getAbsolutePath());
     * try {
     *     //...
     * } finally {
     *     handler.killProcess();
     * }
     * </pre>
     *
     * @param handler to get information from the created process
     * and to be able to kill it.
     *
     * @param timeoutAmount the amount of time the external program
     * is allowed to run before a timeout occurs. When the timeout occurs:
     * <ul>
     * <li>
     * The process is killed
     * </li>
     * <li>
     * {@link ProcessExecutionHandler#onTimeoutException() onTimeoutException()} is called
     * </li>
     * </ul>
     * Note that {@link ProcessExecutionHandler#onExit(int) onExit()} will not be called
     * if a timeout occurs!
     *
     * @param timeoutUnit the timeout unit.
     */
    public void executeAsync(ProcessExecutionHandler handler,
                             long timeoutAmount,
                             TimeUnit timeoutUnit,
                             List<String> cmdArgs);

    /**
     * Execute an external program synchronously.
     * <p>
     * By default {@link ExecutionOutputStrategy#SYSTEM SYSTEM} is
     * used as the output strategy so it is printed to
     * {@link System#out} and {@link System#err}.
     *
     * @param timeoutAmount the amount of time the external program
     * is allowed to run before a timeout occurs. When the timeout occurs:
     * <ul>
     * <li>
     * The process is killed
     * </li>
     * <li>
     * A {@link TimeoutException} exception is thrown.
     * </li>
     * </ul>
     *
     * @param timeoutUnit the timeout unit.
     *
     * @return an object contaning the information about the execution of the
     * program.
     *
     * @throws TimeoutException if the specified timeout is
     * exceeded.
     * @throws LaunchException if the program can't be launched properly.
     *
     */
    public SyncExecutionResult executeSync(long timeoutAmount,
                                           TimeUnit timeoutUnit,
                                           String... cmdArgs) throws LaunchException, TimeoutException;

    /**
     * Execute an external program synchronously.
     * <p>
     * By default {@link ExecutionOutputStrategy#SYSTEM SYSTEM} is
     * used as the output strategy so it is printed to
     * {@link System#out} and {@link System#err}.
     *
     * @param timeoutAmount the amount of time the external program
     * is allowed to run before a timeout occurs. When the timeout occurs:
     * <ul>
     * <li>
     * The process is killed
     * </li>
     * <li>
     * A {@link TimeoutException} exception is thrown.
     * </li>
     * </ul>
     *
     * @param timeoutUnit the timeout unit.
     *
     * @return an object contaning the information about the execution of the
     * program.
     *
     * @throws TimeoutException if the specified timeout is
     * exceeded.
     * @throws LaunchException if the program can't be launched properly.
     */
    public SyncExecutionResult executeSync(long timeoutAmount,
                                           TimeUnit timeoutUnit,
                                           List<String> cmdArgs) throws LaunchException, TimeoutException;

    /**
     * Execute an external program synchronously.
     * <p>
     * By default {@link ExecutionOutputStrategy#SYSTEM SYSTEM} is
     * used as the output strategy so it is printed to
     * {@link System#out} and {@link System#err}.
     *
     * @param timeoutAmount the amount of time the external program
     * is allowed to run before a timeout occurs. When the timeout occurs:
     * <ul>
     * <li>
     * The process is killed.
     * </li>
     * <li>
     * A {@link TimeoutException} exception is thrown.
     * </li>
     * </ul>
     *
     * @param timeoutUnit the timeout unit.
     *
     * @param executionOutputStrategy what should be done with the
     * output of the executed program?
     *
     * @return an object contaning the information about the execution of the
     * program.
     *
     * @throws TimeoutException if the specified timeout is
     * exceeded.
     * @throws LaunchException if the program can't be launched properly.
     */
    public SyncExecutionResult executeSync(long timeoutAmount,
                                           TimeUnit timeoutUnit,
                                           ExecutionOutputStrategy executionOutputStrategy,
                                           String... cmdArgs) throws LaunchException, TimeoutException;

    /**
     * Execute an external program synchronously.
     * <p>
     * By default {@link ExecutionOutputStrategy#SYSTEM SYSTEM} is
     * used as the output strategy so it is printed to
     * {@link System#out} and {@link System#err}.
     *
     * @param timeoutAmount the amount of time the external program
     * is allowed to run before a timeout occurs. When the timeout occurs:
     * <ul>
     * <li>
     * The process is killed.
     * </li>
     * <li>
     * A {@link TimeoutException} exception is thrown.
     * </li>
     * </ul>
     *
     * @param timeoutUnit the timeout unit.
     *
     * @param executionOutputStrategy what should be done with the
     * output of the executed program?
     *
     * @return an object contaning the information about the execution of the
     * program.
     *
     * @throws TimeoutException if the specified timeout is
     * exceeded.
     * @throws LaunchException if the program can't be launched properly.
     */
    public SyncExecutionResult executeSync(long timeoutAmount,
                                           TimeUnit timeoutUnit,
                                           ExecutionOutputStrategy executionOutputStrategy,
                                           List<String> cmdArgs) throws LaunchException, TimeoutException;

}
