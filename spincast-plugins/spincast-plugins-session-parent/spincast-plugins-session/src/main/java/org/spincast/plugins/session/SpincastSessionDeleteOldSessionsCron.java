package org.spincast.plugins.session;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.plugins.crons.SpincastCronJobBase;
import org.spincast.plugins.session.config.SpincastSessionConfig;

import com.google.inject.Inject;

public class SpincastSessionDeleteOldSessionsCron extends SpincastCronJobBase {

    protected final Logger logger = LoggerFactory.getLogger(SpincastSessionDeleteOldSessionsCron.class);

    private final SpincastSessionConfig spincastSessionConfig;
    private final SpincastSessionManager spincastSessionManager;

    @Inject
    public SpincastSessionDeleteOldSessionsCron(SpincastSessionConfig spincastSessionConfig,
                                                SpincastSessionManager spincastSessionManager) {
        this.spincastSessionConfig = spincastSessionConfig;
        this.spincastSessionManager = spincastSessionManager;
    }

    protected SpincastSessionConfig getSpincastSessionConfig() {
        return this.spincastSessionConfig;
    }

    protected SpincastSessionManager getSpincastSessionManager() {
        return this.spincastSessionManager;
    }

    @Override
    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger()
                             .startNow()
                             .withSchedule(simpleSchedule().withIntervalInMinutes(getSpincastSessionConfig().getDeleteOldSessionsCronRunEveryNbrMinutes())
                                                           .repeatForever())
                             .build();
    }

    @Override
    public void executeSafe(JobExecutionContext context) {
        getSpincastSessionManager().deleteOldInactiveSession(getSpincastSessionConfig().getSessionMaxInactiveMinutes());
    }

}
