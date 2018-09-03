package org.spincast.plugins.attemptslimiter;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import java.time.Instant;
import java.util.Map.Entry;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.spincast.plugins.attemptslimiter.config.SpincastAttemptsLimiterPluginConfig;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTaskBase;

import com.google.inject.Inject;

public class DeleteOldAttemptsScheduledTask extends SpincastScheduledTaskBase {

    private final SpincastAttemptsLimiterPluginConfig spincastAttemptsLimiterPluginConfig;
    private final SpincastAttemptsLimiterPluginRepository spincastAttemptsLimiterPluginRepository;
    private final AttemptsManager attemptsManager;

    @Inject
    public DeleteOldAttemptsScheduledTask(SpincastAttemptsLimiterPluginConfig spincastAttemptsLimiterPluginConfig,
                                          SpincastAttemptsLimiterPluginRepository spincastAttemptsLimiterPluginRepository,
                                          AttemptsManager attemptsManager) {
        this.spincastAttemptsLimiterPluginConfig = spincastAttemptsLimiterPluginConfig;
        this.spincastAttemptsLimiterPluginRepository = spincastAttemptsLimiterPluginRepository;
        this.attemptsManager = attemptsManager;
    }

    protected SpincastAttemptsLimiterPluginConfig getSpincastAttemptsLimiterPluginConfig() {
        return this.spincastAttemptsLimiterPluginConfig;
    }

    protected SpincastAttemptsLimiterPluginRepository getSpincastAttemptsLimiterPluginRepository() {
        return this.spincastAttemptsLimiterPluginRepository;
    }

    protected AttemptsManager getAttemptsManager() {
        return this.attemptsManager;
    }

    @Override
    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger()
                             .withSchedule(simpleSchedule().withIntervalInMinutes(getSpincastAttemptsLimiterPluginConfig().getDeleteOldAttemptsScheduledTaskIntervalMinutes())
                                                           .repeatForever())
                             .build();
    }

    @Override
    protected void executeSafe(JobExecutionContext context) {
        for (Entry<String, AttemptRule> entry : getAttemptsManager().getAttemptRulesByActionName().entrySet()) {
            Instant beforeDate = Instant.now().minus(entry.getValue().getDuration());
            getSpincastAttemptsLimiterPluginRepository().deleteAttemptsOlderThan(entry.getKey(), beforeDate);
        }
    }

}
