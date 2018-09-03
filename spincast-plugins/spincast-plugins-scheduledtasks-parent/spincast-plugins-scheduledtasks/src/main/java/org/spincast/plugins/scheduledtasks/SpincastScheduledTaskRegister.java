package org.spincast.plugins.scheduledtasks;

import org.spincast.core.server.ServerStartedListener;

/**
 * Gets all Scheduled Tasks that were bound in Guice's context
 * and register them with the Scheduler.
 */
public interface SpincastScheduledTaskRegister extends ServerStartedListener {

    /**
     * Register a {@link SpincastScheduledTask} on the
     * Scheduler.
     */
    public void registerScheduledTask(SpincastScheduledTask task);

}
