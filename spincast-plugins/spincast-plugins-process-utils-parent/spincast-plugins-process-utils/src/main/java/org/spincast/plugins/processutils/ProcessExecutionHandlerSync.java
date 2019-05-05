package org.spincast.plugins.processutils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessExecutionHandlerSync extends ProcessExecutionHandlerDefault {

    protected static final Logger logger = LoggerFactory.getLogger(ProcessExecutionHandlerSync.class);

    private Integer exitCode = null;
    private Exception launchException = null;
    private boolean timeoutException = false;
    private final CountDownLatch latch;
    private final ExecutionOutputStrategy executionOutputStrategy;
    private List<String> outLines;
    private List<String> errLines;

    /**
     * @param latch to be decremented when the process
     * exits or when an error occurs.
     */
    public ProcessExecutionHandlerSync(CountDownLatch latch,
                                       ExecutionOutputStrategy executionOutputStrategy) {
        this.latch = latch;
        if (executionOutputStrategy == null) {
            executionOutputStrategy = ExecutionOutputStrategy.NONE;
        }
        this.executionOutputStrategy = executionOutputStrategy;
    }

    protected CountDownLatch getLatch() {
        return this.latch;
    }

    protected ExecutionOutputStrategy getExecutionOutputStrategy() {
        return this.executionOutputStrategy;
    }

    public Integer getExitCode() {
        return this.exitCode;
    }

    public Exception getLaunchException() {
        return this.launchException;
    }

    public boolean isTimeoutException() {
        return this.timeoutException;
    }

    public List<String> getOutLines() {
        if (this.outLines == null) {
            this.outLines = new ArrayList<String>();
        }
        return this.outLines;
    }

    public List<String> getErrLines() {
        if (this.errLines == null) {
            this.errLines = new ArrayList<String>();
        }
        return this.errLines;
    }

    @Override
    public void onExit(int exitCode) {
        this.exitCode = exitCode;

        try {
            super.onExit(exitCode);
        } finally {
            getLatch().countDown();
        }
    }

    @Override
    public void onLaunchException(Exception ex) {
        this.launchException = ex;

        try {
            super.onLaunchException(ex);
        } finally {
            getLatch().countDown();
        }
    }

    @Override
    public void onTimeoutException() {
        this.timeoutException = true;

        try {
            super.onTimeoutException();
        } finally {
            getLatch().countDown();
        }
    }

    @Override
    public void onSystemOut(String line) {
        if (getExecutionOutputStrategy() == ExecutionOutputStrategy.SYSTEM) {
            System.out.println(line);
        } else if (getExecutionOutputStrategy() == ExecutionOutputStrategy.BUFFER) {
            getOutLines().add(line);
        } else if (getExecutionOutputStrategy() == ExecutionOutputStrategy.NONE) {
            // do nothing
        } else {
            throw new RuntimeException("Unmanaged " + ExecutionOutputStrategy.class.getSimpleName() + ": " +
                                       getExecutionOutputStrategy());
        }
    }

    @Override
    public void onSystemErr(String line) {
        if (getExecutionOutputStrategy() == ExecutionOutputStrategy.SYSTEM) {
            System.err.println(line);
        } else if (getExecutionOutputStrategy() == ExecutionOutputStrategy.BUFFER) {
            getErrLines().add(line);
        } else if (getExecutionOutputStrategy() == ExecutionOutputStrategy.NONE) {
            // do nothing
        } else {
            throw new RuntimeException("Unmanaged " + ExecutionOutputStrategy.class.getSimpleName() + ": " +
                                       getExecutionOutputStrategy());
        }
    }

    public SyncExecutionResult getSyncExecutionResult() {
        int exitCode = getExitCode() != null ? getExitCode() : -1;
        SyncExecutionResult result = new SyncExecutionResultDefault(exitCode,
                                                                    getOutLines(),
                                                                    getErrLines());
        return result;
    }

}
