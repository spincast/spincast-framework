package org.spincast.plugins.crons;

import org.quartz.spi.JobFactory;

/**
 * Factory to return cron job from the Guice context.
 */
public interface SpincastCronJobFactory extends JobFactory {
    // nothing required
}
