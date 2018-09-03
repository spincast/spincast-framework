package org.spincast.plugins.formsprotection.doublesubmit;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.plugins.formsprotection.config.SpincastFormsProtectionConfig;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTaskBase;

import com.google.common.base.Optional;
import com.google.inject.Inject;

public class SpincastFormsDoubleSubmitDeleteOldProtectionIdsScheduledTask extends SpincastScheduledTaskBase {

    protected final Logger logger = LoggerFactory.getLogger(SpincastFormsDoubleSubmitDeleteOldProtectionIdsScheduledTask.class);

    private final SpincastFormsProtectionConfig spincastFormsDoubleSubmitProtectionConfig;
    private final SpincastFormsDoubleSubmitProtectionRepository spincastFormsDoubleSubmitProtectionRepository;

    @Inject
    public SpincastFormsDoubleSubmitDeleteOldProtectionIdsScheduledTask(SpincastFormsProtectionConfig spincastFormsDoubleSubmitProtectionConfig,
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
                             .withSchedule(simpleSchedule().withIntervalInMinutes(getSpincastFormsDoubleSubmitProtectionConfig().getDeleteOldDoubleSubmitProtectionIdsScheduledTaskRunEveryNbrMinutes())
                                                           .repeatForever())
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
