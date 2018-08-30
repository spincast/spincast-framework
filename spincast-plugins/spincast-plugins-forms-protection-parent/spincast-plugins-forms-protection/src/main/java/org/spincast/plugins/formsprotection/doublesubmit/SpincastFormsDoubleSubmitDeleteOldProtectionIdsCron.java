package org.spincast.plugins.formsprotection.doublesubmit;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.plugins.crons.SpincastCronJobBase;
import org.spincast.plugins.formsprotection.config.SpincastFormsProtectionConfig;

import com.google.common.base.Optional;
import com.google.inject.Inject;

public class SpincastFormsDoubleSubmitDeleteOldProtectionIdsCron extends SpincastCronJobBase {

    protected final Logger logger = LoggerFactory.getLogger(SpincastFormsDoubleSubmitDeleteOldProtectionIdsCron.class);

    private final SpincastFormsProtectionConfig spincastFormsDoubleSubmitProtectionConfig;
    private final SpincastFormsDoubleSubmitProtectionRepository spincastFormsDoubleSubmitProtectionRepository;

    @Inject
    public SpincastFormsDoubleSubmitDeleteOldProtectionIdsCron(SpincastFormsProtectionConfig spincastFormsDoubleSubmitProtectionConfig,
                                                               Optional<SpincastFormsDoubleSubmitProtectionRepository> spincastFormsDoubleSubmitProtectionRepositoryOptional) {
        this.spincastFormsDoubleSubmitProtectionConfig = spincastFormsDoubleSubmitProtectionConfig;
        this.spincastFormsDoubleSubmitProtectionRepository = spincastFormsDoubleSubmitProtectionRepositoryOptional.orNull();
    }

    protected SpincastFormsProtectionConfig getSpincastFormsDoubleSubmitProtectionConfig() {
        return this.spincastFormsDoubleSubmitProtectionConfig;
    }

    protected SpincastFormsDoubleSubmitProtectionRepository getSpincastFormsDoubleSubmitProtectionRepository() {
        return this.spincastFormsDoubleSubmitProtectionRepository;
    }

    @Override
    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger()
                             .startNow()
                             .build();
    }

    @Override
    public void executeSafe(JobExecutionContext context) {
        getSpincastFormsDoubleSubmitProtectionRepository().deleteOldFormsProtectionIds(deletedOldProtectionIdsAfterXMinutes());
    }

    protected int deletedOldProtectionIdsAfterXMinutes() {

        //==========================================
        // Deletes the protection ids older than 
        // the age a form is valid for.
        //==========================================
        int formValidForMinutes = getSpincastFormsDoubleSubmitProtectionConfig().getFormDoubleSubmitFormValidForNbrMinutes();
        return formValidForMinutes + 5;

    }
}
