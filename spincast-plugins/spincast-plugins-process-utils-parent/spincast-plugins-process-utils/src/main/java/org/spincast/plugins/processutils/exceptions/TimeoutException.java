package org.spincast.plugins.processutils.exceptions;

import org.spincast.plugins.processutils.ExecutionOutputStrategy;
import org.spincast.plugins.processutils.SpincastProcessUtils;
import org.spincast.plugins.processutils.SyncExecutionResult;

/**
 * Exception thrown when a program executed using
 * {@link SpincastProcessUtils#executeSync(long, java.util.concurrent.TimeUnit, String...) executeSync()}
 * exceeds the specified timeout.
 * <p>
 * Note that the {@link SyncExecutionResult result object} is available
 * through the {@link #getSyncExecutionResult()} method, so you can
 * still have access to the current output of the program, if the
 * {@link ExecutionOutputStrategy#BUFFER BUFFER} strategy was used.
 * <p>
 * Also note that the {@link SyncExecutionResult#getExitCode()} method
 * of that result object will return an <em>undefined</em>
 * value (but probably be <code>-1</code>) as the program didn't exit properly!
 */
public class TimeoutException extends Exception {

    private static final long serialVersionUID = 1L;

    private final SyncExecutionResult syncExecutionResult;

    public TimeoutException(SyncExecutionResult syncExecutionResult) {
        super("A timeout occured");
        this.syncExecutionResult = syncExecutionResult;
    }

    public SyncExecutionResult getSyncExecutionResult() {
        return this.syncExecutionResult;
    }
}
