package org.spincast.plugins.processutils;

/**
 * What to do with the output of the created processes?
 */
public enum ExecutionOutputStrategy {
    /**
     * The output is ignored, discarded.
     */
    NONE,

    /**
     * The output is sent to
     * {@link System#out} or
     * {@link System#err}.
     */
    SYSTEM,

    /**
     * The output is buffered so it is
     * available when the process ends.
     */
    BUFFER

}
