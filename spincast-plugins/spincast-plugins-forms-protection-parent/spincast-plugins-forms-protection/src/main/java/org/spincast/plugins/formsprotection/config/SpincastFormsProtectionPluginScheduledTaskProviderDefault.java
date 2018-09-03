package org.spincast.plugins.formsprotection.config;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitDeleteOldProtectionIdsScheduledTask;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionFilter;
import org.spincast.plugins.formsprotection.doublesubmit.SpincastFormsDoubleSubmitProtectionRepository;
import org.spincast.plugins.scheduledtasks.SpincastScheduledTask;

import com.google.common.base.Optional;
import com.google.inject.Inject;

public class SpincastFormsProtectionPluginScheduledTaskProviderDefault implements
                                                                       SpincastFormsProtectionPluginScheduledTaskProvider {


    protected final static Logger logger =
            LoggerFactory.getLogger(SpincastFormsProtectionPluginScheduledTaskProviderDefault.class);

    private final SpincastFormsProtectionConfig spincastFormsProtectionConfig;
    private final SpincastFormsDoubleSubmitDeleteOldProtectionIdsScheduledTask deleteDoubleSubmitOldProtectionIdsScheduledTask;
    private final Optional<SpincastFormsDoubleSubmitProtectionRepository> spincastFormsDoubleSubmitProtectionRepositoryOptional;

    @Inject
    public SpincastFormsProtectionPluginScheduledTaskProviderDefault(SpincastFormsProtectionConfig spincastFormsProtectionConfig,
                                                                     SpincastFormsDoubleSubmitDeleteOldProtectionIdsScheduledTask deleteDoubleSubmitOldProtectionIdsScheduledTask,
                                                                     Optional<SpincastFormsDoubleSubmitProtectionRepository> spincastFormsDoubleSubmitProtectionRepositoryOptional) {
        this.spincastFormsProtectionConfig = spincastFormsProtectionConfig;
        this.deleteDoubleSubmitOldProtectionIdsScheduledTask = deleteDoubleSubmitOldProtectionIdsScheduledTask;
        this.spincastFormsDoubleSubmitProtectionRepositoryOptional = spincastFormsDoubleSubmitProtectionRepositoryOptional;
    }

    protected SpincastFormsProtectionConfig getSpincastFormsProtectionConfig() {
        return this.spincastFormsProtectionConfig;
    }

    protected SpincastFormsDoubleSubmitDeleteOldProtectionIdsScheduledTask getDeleteDoubleSubmitOldProtectionIdsScheduledTask() {
        return this.deleteDoubleSubmitOldProtectionIdsScheduledTask;
    }

    protected Optional<SpincastFormsDoubleSubmitProtectionRepository> getSpincastFormsDoubleSubmitProtectionRepositoryOptional() {
        return this.spincastFormsDoubleSubmitProtectionRepositoryOptional;
    }

    @Override
    public Set<SpincastScheduledTask> get() {

        Set<SpincastScheduledTask> scheduledTasks = new HashSet<SpincastScheduledTask>();

        //==========================================
        // Delete Double Submit protection ids scheduled task only
        // enabled if an implementation for 
        // SpincastFormsDoubleSubmitProtectionRepositoryOptional
        // was bound.
        //==========================================
        if (getSpincastFormsDoubleSubmitProtectionRepositoryOptional().orNull() != null &&
            getSpincastFormsProtectionConfig().autoRegisterDeleteOldDoubleSubmitProtectionIdsScheduledTask()) {
            scheduledTasks.add(getDeleteDoubleSubmitOldProtectionIdsScheduledTask());
        } else {
            logger.warn("No implementation bound for " + SpincastFormsDoubleSubmitProtectionFilter.class.getName() + " ... " +
                        "The " + SpincastFormsDoubleSubmitDeleteOldProtectionIdsScheduledTask.class.getName() +
                        " scheduled task won't be registered...");
        }


        return scheduledTasks;
    }

}
