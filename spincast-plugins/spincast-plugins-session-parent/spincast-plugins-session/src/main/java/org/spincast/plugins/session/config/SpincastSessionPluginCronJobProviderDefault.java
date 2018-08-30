package org.spincast.plugins.session.config;

import java.util.HashSet;
import java.util.Set;

import org.spincast.plugins.crons.SpincastCronJob;
import org.spincast.plugins.session.SpincastSessionDeleteOldSessionsCron;

import com.google.inject.Inject;


public class SpincastSessionPluginCronJobProviderDefault implements SpincastSessionPluginCronJobProvider {

    private final SpincastSessionConfig spincastSessionConfig;
    private final SpincastSessionDeleteOldSessionsCron spincastSessionDeleteOldSessionsCron;

    @Inject
    public SpincastSessionPluginCronJobProviderDefault(SpincastSessionConfig spincastSessionConfig,
                                                       SpincastSessionDeleteOldSessionsCron spincastSessionDeleteOldSessionsCron) {
        this.spincastSessionConfig = spincastSessionConfig;
        this.spincastSessionDeleteOldSessionsCron = spincastSessionDeleteOldSessionsCron;
    }

    protected SpincastSessionConfig getSpincastSessionConfig() {
        return this.spincastSessionConfig;
    }

    protected SpincastSessionDeleteOldSessionsCron getSpincastSessionDeleteOldSessionsCron() {
        return this.spincastSessionDeleteOldSessionsCron;
    }

    @Override
    public Set<SpincastCronJob> get() {

        Set<SpincastCronJob> cronjobs = new HashSet<SpincastCronJob>();

        if (isAutoRegisterCronJobToDeleteOldSessions()) {
            cronjobs.add(getSpincastSessionDeleteOldSessionsCron());
        }

        return cronjobs;
    }

    protected boolean isAutoRegisterCronJobToDeleteOldSessions() {
        return true;
    }

}
