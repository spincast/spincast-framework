package org.spincast.plugins.processutils;

/**
 * Handler to be inform of an exit code, an
 * exception and to be able to kill the started process.
 */
public interface ProcessExecutionHandler {

    /**
     * Internal use.
     * <p>
     * Will be called automatically
     * when the process is created.
     */
    public void setProcessAndProcessKiller(Process process, ProcessKiller processKiller);

    /**
     * Returns <code>true</code> if the process is alive;
     */
    public boolean isProcessAlive();

    /**
     * Called if an exception occured during the launch
     * of the program.
     */
    public void onLaunchException(Exception ex);

    /**
     * Called if the execution of the program
     * exceeds the specified timeout.
     */
    public void onTimeoutException();

    /**
     * Called when the process exits. In general, a <code>0</code>
     * value means that no error occured.
     * <p>
     * Note that this method will <em>not</em>
     * be called if there is an exception while
     * launching the program! In that case,
     * {@link #onLaunchException(Exception)}
     * will be called instead.
     */
    public void onExit(int exitVal);

    /**
     * Always called at the end, whether the process
     * exited or an exception occured.
     */
    public void onEnd();

    /**
     * Called when a line is written to the standard
     * output by the created process.
     */
    public void onSystemOut(String line);

    /**
     * Called when a line is written to the standard
     * errors by the created process.
     */
    public void onSystemErr(String line);

    /**
     * Kill the process.
     */
    public void killProcess();



}
