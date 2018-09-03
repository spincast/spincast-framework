package org.spincast.plugins.scheduledtasks;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;

/**
 * A scheduled task to run.
 */
public interface SpincastScheduledTask extends Job {

    /**
     * When to run this task?
     */
    public Trigger getTrigger();

    /**
     * The actions to run.
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException;

}
