package org.spincast.plugins.processutils;

import java.util.List;

/**
 * The result of the syncronous execution of an external program,
 * made using
 * {@link SpincastProcessUtils#executeSync(long, java.util.concurrent.TimeUnit, String...) executeSync}.
 */
public interface SyncExecutionResult {

    /**
     * The exit code of the program.
     */
    public int getExitCode();

    /**
     * The System standard output produced by the
     * executed program.
     * <p>
     * Note that those lines will be available only
     * if the {@link ExecutionOutputStrategy#BUFFER} strategy
     * is used!
     */
    public List<String> getSystemOutLines();

    /**
     * The System standard errors produced by the
     * executed program.
     * <p>
     * Note that those lines will be available only
     * if the {@link ExecutionOutputStrategy#BUFFER} strategy
     * is used!
     */
    public List<String> getSystemErrLines();

}
