package org.spincast.plugins.scheduledtasks;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SpincastScheduledTaskBase implements SpincastScheduledTask {

    protected final Logger logger = LoggerFactory.getLogger(SpincastScheduledTaskBase.class);

    private volatile boolean running = false;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        if (this.running) {
            this.logger.debug("Scheduled Task " + this.getClass().getSimpleName() + " is still running, returning...");
            return;
        }
        this.running = true;

        try {
            this.logger.debug("Scheduled Task " + this.getClass().getSimpleName() + " starting...");

            executeSafe(context);

        } finally {
            this.running = false;
            this.logger.trace("Scheduled Task " + this.getClass().getSimpleName() + " done.");
        }
    }

    @Override
    public abstract Trigger getTrigger();

    /**
     * To override to implement the actual ScheduledTask action.
     * <p>
     * Will only be called if the ScheduledTask isn't already running.
     */
    protected abstract void executeSafe(JobExecutionContext context);


}
