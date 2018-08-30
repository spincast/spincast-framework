package org.spincast.plugins.crons;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SpincastCronJobBase implements SpincastCronJob {

    protected final Logger logger = LoggerFactory.getLogger(SpincastCronJobBase.class);

    private volatile boolean running = false;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        if (this.running) {
            this.logger.debug("Cron " + this.getClass().getSimpleName() + " is still running, returning...");
            return;
        }
        this.running = true;

        try {
            this.logger.debug("Cron " + this.getClass().getSimpleName() + " starting...");

            executeSafe(context);

        } finally {
            this.running = false;
            this.logger.trace("Cron " + this.getClass().getSimpleName() + " done.");
        }
    }

    @Override
    public abstract Trigger getTrigger();

    /**
     * To override to implement the actuqal cronjob action.
     * <p>
     * Will only be called if it isn't already running.
     */
    protected abstract void executeSafe(JobExecutionContext context);


}
