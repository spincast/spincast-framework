package org.spincast.plugins.crons;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;

/**
 * A cron job (scheduled task) to run.
 */
public interface SpincastCronJob extends Job {

    /**
     * When to run this cron job?
     */
    public Trigger getTrigger();

    /**
     * The actions to run.
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException;

}
