package org.spincast.plugins.processutils;

import java.util.ArrayList;
import java.util.List;

public class SyncExecutionResultDefault implements SyncExecutionResult {

    private final int exitCode;
    private final List<String> outLines;
    private final List<String> errLines;

    public SyncExecutionResultDefault(int exitCode,
                                      List<String> outLines,
                                      List<String> errLines) {
        this.exitCode = exitCode;

        if (outLines == null) {
            outLines = new ArrayList<String>();
        }
        this.outLines = outLines;

        if (errLines == null) {
            errLines = new ArrayList<String>();
        }
        this.errLines = errLines;
    }

    @Override
    public int getExitCode() {
        return this.exitCode;
    }

    @Override
    public List<String> getSystemOutLines() {
        return this.outLines;
    }

    @Override
    public List<String> getSystemErrLines() {
        return this.errLines;
    }

    @Override
    public String toString() {
        return "Exit code: " + getExitCode();
    }

}
