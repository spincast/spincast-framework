package org.spincast.plugins.session.config;

import java.util.HashSet;
import java.util.Set;

import org.spincast.plugins.scheduledtasks.SpincastScheduledTask;
import org.spincast.plugins.session.SpincastSessionDeleteOldSessionsScheduledTask;

import com.google.inject.Inject;


public class SpincastSessionPluginScheduledTaskProviderDefault implements SpincastSessionPluginScheduledTaskProvider {

    private final SpincastSessionConfig spincastSessionConfig;
    private final SpincastSessionDeleteOldSessionsScheduledTask spincastSessionDeleteOldSessionsScheduledTask;

    @Inject
    public SpincastSessionPluginScheduledTaskProviderDefault(SpincastSessionConfig spincastSessionConfig,
                                                       SpincastSessionDeleteOldSessionsScheduledTask spincastSessionDeleteOldSessionsScheduledTask) {
        this.spincastSessionConfig = spincastSessionConfig;
        this.spincastSessionDeleteOldSessionsScheduledTask = spincastSessionDeleteOldSessionsScheduledTask;
    }

    protected SpincastSessionConfig getSpincastSessionConfig() {
        return this.spincastSessionConfig;
    }

    protected SpincastSessionDeleteOldSessionsScheduledTask getSpincastSessionDeleteOldSessionsScheduledTask() {
        return this.spincastSessionDeleteOldSessionsScheduledTask;
    }

    @Override
    public Set<SpincastScheduledTask> get() {

        Set<SpincastScheduledTask> scheduledTasks = new HashSet<SpincastScheduledTask>();

        if (isAutoRegisterScheduledTaskToDeleteOldSessions()) {
            scheduledTasks.add(getSpincastSessionDeleteOldSessionsScheduledTask());
        }

        return scheduledTasks;
    }

    protected boolean isAutoRegisterScheduledTaskToDeleteOldSessions() {
        return true;
    }

}
