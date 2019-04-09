package org.spincast.plugins.processutils;


public interface JarExecutionHandler {

    /**
     * Called when the process is created.
     */
    public void setJarProcess(Process jarProcess);

    /**
     * The created process.
     */
    public Process getJarProcess();

    /**
     * Called if an exception occured during the process
     * launch.
     */
    public void onException(Exception ex);

    /**
     * Called when the process exists. In general, a <code>0</code>
     * value means no error occured.
     */
    public void onExit(int exitVal);

    /**
     * Kill the jar execution process.
     */
    public void killJarProcess();

}
