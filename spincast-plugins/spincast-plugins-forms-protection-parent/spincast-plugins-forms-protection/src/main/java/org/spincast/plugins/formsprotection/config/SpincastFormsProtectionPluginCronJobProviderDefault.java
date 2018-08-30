package org.spincast.plugins.formsprotection.config;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.plugins.crons.SpincastCronJob;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitDeleteOldProtectionIdsCron;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionFilter;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionRepository;

import com.google.common.base.Optional;
import com.google.inject.Inject;

public class SpincastFormsProtectionPluginCronJobProviderDefault implements SpincastFormsProtectionPluginCronJobProvider {


    protected final static Logger logger = LoggerFactory.getLogger(SpincastFormsProtectionPluginCronJobProviderDefault.class);

    private final SpincastFormsProtectionConfig spincastFormsProtectionConfig;
    private final SpincastFormsDoubleSubmitDeleteOldProtectionIdsCron deleteDoubleSubmitOldProtectionIdsCron;
    private final Optional<SpincastFormsDoubleSubmitProtectionRepository> spincastFormsDoubleSubmitProtectionRepositoryOptional;

    @Inject
    public SpincastFormsProtectionPluginCronJobProviderDefault(SpincastFormsProtectionConfig spincastFormsProtectionConfig,
                                                               SpincastFormsDoubleSubmitDeleteOldProtectionIdsCron deleteDoubleSubmitOldProtectionIdsCron,
                                                               Optional<SpincastFormsDoubleSubmitProtectionRepository> spincastFormsDoubleSubmitProtectionRepositoryOptional) {
        this.spincastFormsProtectionConfig = spincastFormsProtectionConfig;
        this.deleteDoubleSubmitOldProtectionIdsCron = deleteDoubleSubmitOldProtectionIdsCron;
        this.spincastFormsDoubleSubmitProtectionRepositoryOptional = spincastFormsDoubleSubmitProtectionRepositoryOptional;
    }

    protected SpincastFormsProtectionConfig getSpincastFormsProtectionConfig() {
        return this.spincastFormsProtectionConfig;
    }

    protected SpincastFormsDoubleSubmitDeleteOldProtectionIdsCron getDeleteDoubleSubmitOldProtectionIdsCron() {
        return this.deleteDoubleSubmitOldProtectionIdsCron;
    }

    protected Optional<SpincastFormsDoubleSubmitProtectionRepository> getSpincastFormsDoubleSubmitProtectionRepositoryOptional() {
        return this.spincastFormsDoubleSubmitProtectionRepositoryOptional;
    }

    @Override
    public Set<SpincastCronJob> get() {

        Set<SpincastCronJob> cronjobs = new HashSet<SpincastCronJob>();

        //==========================================
        // Delete Double Submit protection ids cron only
        // enabled if an implementation for 
        // SpincastFormsDoubleSubmitProtectionRepositoryOptional
        // was bound.
        //==========================================
        if (getSpincastFormsDoubleSubmitProtectionRepositoryOptional().orNull() != null &&
            getSpincastFormsProtectionConfig().autoRegisterDeleteOldDoubleSubmitProtectionIdsCronJob()) {
            cronjobs.add(getDeleteDoubleSubmitOldProtectionIdsCron());
        } else {
            logger.warn("No implementation bound for " + SpincastFormsDoubleSubmitProtectionFilter.class.getName() + " ... " +
                        "The " + SpincastFormsDoubleSubmitDeleteOldProtectionIdsCron.class.getName() + " cron " +
                        "won't be registered...");
        }


        return cronjobs;
    }

}
