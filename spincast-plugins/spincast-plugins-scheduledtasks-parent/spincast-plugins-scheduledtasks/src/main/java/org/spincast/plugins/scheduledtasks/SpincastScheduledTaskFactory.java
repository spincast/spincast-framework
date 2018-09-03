package org.spincast.plugins.scheduledtasks;

import org.quartz.spi.JobFactory;

/**
 * Factory to return ScheduledTask from the Guice context.
 */
public interface SpincastScheduledTaskFactory extends JobFactory {
    // nothing required
}
