package org.spincast.plugins.crons;

import org.spincast.core.server.ServerStartedListener;

/**
 * Gets all cronjobs that were bound in Guice's context
 * and register them with the Scheduler.
 */
public interface SpincastCronJobRegister extends ServerStartedListener {

    /**
     * Register a {@link SpincastCronJob} on the
     * Scheduler.
     */
    public void registerCronJob(SpincastCronJob cronJob);

}
