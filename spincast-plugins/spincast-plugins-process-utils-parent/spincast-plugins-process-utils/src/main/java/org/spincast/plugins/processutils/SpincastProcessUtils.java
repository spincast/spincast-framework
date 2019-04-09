package org.spincast.plugins.processutils;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.spincast.core.templating.TemplatingEngine;
import org.spincast.core.utils.ResourceInfo;

/**
 * Utilities related to processes and external projects
 * manipulation.
 */
public interface SpincastProcessUtils {

    /**
     * Execute the specified goal on an external Maven project.
     * <p>
     * If the project is located on the classpath, it
     * first copies it to the file system (in a temp folder).
     *
     * @return the root directory of the project. This will be
     * the same as the specified path if on the file system,
     * or will be the created temp directory where the project from the
     * classpath has been copied otherwise.
     */
    public File executeGoalOnExternalMavenProject(ResourceInfo projectRootInfo,
                                                  MavenProjectGoal mavenGoal);

    /**
     * Execute the specified goal on an external Maven project.
     * <p>
     * If the project is located on the classpath, it
     * first copies it to the file system (in a temp folder).
     *
     * @param pomParams Before executing the goal, those parameters
     * are used to replace placeholders in the project's <code>pom.xml</code>
     * using Spincast's {@link TemplatingEngine}.
     *
     * @return the root directory of the project. This will be
     * the same as the specified path if on the file system,
     * or will be the created temp directory where the project from the
     * classpath has been copied otherwise.
     */
    public File executeGoalOnExternalMavenProject(ResourceInfo projectRootInfo,
                                                  MavenProjectGoal mavenGoal,
                                                  Map<String, Object> pomParams);

    /**
     * Run an executable <code>.jar</code> by passing the
     * specified arguments.
     * <p>
     * If the process is not made to die automatically
     * (for example it starts an HTTP server), you must
     * kill the process by yourself!:
     * <pre>
     * JarExecutionHandlerDefault handler = new JarExecutionHandlerDefault();
     * getSpincastProcessUtils().executeJar(jarFile.getAbsolutePath(),
     *                                      Lists.newArrayList(),
     *                                      handler);
     * try {
     *     //...
     * } finally {
     *     handler.killJarProcess();
     * }
     * </pre>
     */
    public void executeJar(String jarFilePath,
                           List<String> args,
                           JarExecutionHandler handler);

    /**
     * Run an executable <code>.jar</code> by passing the
     * specified arguments.
     * <p>
     * If the process is not made to die automatically
     * (for example it starts an HTTP server), you must
     * kill the process by yourself!:
     * <pre>
     * JarExecutionHandlerDefault handler = new JarExecutionHandlerDefault();
     * getSpincastProcessUtils().executeJar(jarFile.getAbsolutePath(),
     *                                      Lists.newArrayList(),
     *                                      handler);
     * try {
     *     //...
     * } finally {
     *     handler.killJarProcess();
     * }
     * </pre>
     *
     * @param javaBinPath The path to the <code>java</code>
     * executable to use.
     */
    public void executeJar(String javaBinPath,
                           String jarFilePath,
                           List<String> args,
                           JarExecutionHandler handler);



}
