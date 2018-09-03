package org.spincast.plugins.scheduledtasks;

import static org.quartz.JobBuilder.newJob;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spincast.core.config.SpincastConfig;
import org.spincast.core.server.ServerStartedListener;
import org.spincast.core.utils.SpincastStatics;

import com.google.inject.Inject;

public class SpincastScheduledTaskRegistrerDefault implements SpincastScheduledTaskRegister, ServerStartedListener {

    protected final static Logger logger = LoggerFactory.getLogger(SpincastScheduledTaskRegistrerDefault.class);

    private final Set<SpincastScheduledTask> scheduledTasks;
    private final Scheduler scheduler;
    private final SpincastConfig spincastConfig;

    @Inject
    public SpincastScheduledTaskRegistrerDefault(@Nullable Set<SpincastScheduledTask> scheduledTasks,
                                                 @Nullable Set<Set<SpincastScheduledTask>> scheduledTaskSets,
                                                 Scheduler scheduler,
                                                 SpincastConfig spincastConfig) {


        if (scheduledTasks == null) {
            scheduledTasks = new HashSet<>();
        }
        if (scheduledTaskSets != null) {
            scheduledTasks = new HashSet<>(scheduledTasks); // makes mutable
            for (Set<SpincastScheduledTask> scheduledTaskJobsSet : scheduledTaskSets) {
                if (scheduledTaskJobsSet != null) {
                    for (SpincastScheduledTask scheduledTask : scheduledTaskJobsSet) {
                        scheduledTasks.add(scheduledTask);
                    }
                }
            }
        }

        this.scheduledTasks = scheduledTasks;
        this.scheduler = scheduler;
        this.spincastConfig = spincastConfig;
    }

    //==========================================
    // This will be called once the server is
    // started successfully.
    //
    // We need to register the Scheduled Tasks AFTER the
    // server is started so we are sure for example
    // any migrations file would have been applied.
    //==========================================
    @Override
    public void serverStartedSuccessfully() {
        if (!getSpincastConfig().isTestingMode() || registerScheduledTasksInTestingMode()) {
            registerBoundedScheduledTasks();
        }
    }

    protected Set<SpincastScheduledTask> getScheduledTasks() {
        return this.scheduledTasks;
    }

    protected Scheduler getScheduler() {
        return this.scheduler;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    @Override
    public void registerScheduledTask(SpincastScheduledTask scheduledTask) {
        try {
            JobDetail jobDetail = newJob(scheduledTask.getClass()).withIdentity(scheduledTask.getClass().getSimpleName()).build();
            getScheduler().scheduleJob(jobDetail, scheduledTask.getTrigger());
            logger.info("Scheduled Task \"" + scheduledTask.getClass().getSimpleName() + "\" registered.");
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    public void registerBoundedScheduledTasks() {
        for (SpincastScheduledTask scheduledTask : getScheduledTasks()) {
            registerScheduledTask(scheduledTask);
        }
    }

    protected boolean registerScheduledTasksInTestingMode() {
        return false;
    }

}
