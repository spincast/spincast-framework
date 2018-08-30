package org.spincast.plugins.crons;

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

public class SpincastCronJobRegistrerDefault implements SpincastCronJobRegister, ServerStartedListener {

    protected final static Logger logger = LoggerFactory.getLogger(SpincastCronJobRegistrerDefault.class);

    private final Set<SpincastCronJob> cronJobs;
    private final Scheduler scheduler;
    private final SpincastConfig spincastConfig;

    @Inject
    public SpincastCronJobRegistrerDefault(@Nullable Set<SpincastCronJob> cronJobs,
                                           @Nullable Set<Set<SpincastCronJob>> cronJobsSets,
                                           Scheduler scheduler,
                                           SpincastConfig spincastConfig) {


        if (cronJobs == null) {
            cronJobs = new HashSet<>();
        }
        if (cronJobsSets != null) {
            cronJobs = new HashSet<>(cronJobs); // makes mutable
            for (Set<SpincastCronJob> cronJobsSet : cronJobsSets) {
                if (cronJobsSet != null) {
                    for (SpincastCronJob cronJob : cronJobsSet) {
                        cronJobs.add(cronJob);
                    }
                }
            }
        }

        this.cronJobs = cronJobs;
        this.scheduler = scheduler;
        this.spincastConfig = spincastConfig;
    }

    //==========================================
    // This will be called once the server is
    // started successfully.
    //
    // We need to register the crons AFTER the
    // server is started so we are sure for example
    // any migrations file would have been applied.
    //==========================================
    @Override
    public void serverStartedSuccessfully() {
        if (!getSpincastConfig().isTestingMode() || registerCronJobInTestingMode()) {
            registerBoundedCronJobs();
        }
    }

    protected Set<SpincastCronJob> getCronJobs() {
        return this.cronJobs;
    }

    protected Scheduler getScheduler() {
        return this.scheduler;
    }

    protected SpincastConfig getSpincastConfig() {
        return this.spincastConfig;
    }

    @Override
    public void registerCronJob(SpincastCronJob cronJob) {
        try {
            JobDetail jobDetail = newJob(cronJob.getClass()).withIdentity(cronJob.getClass().getSimpleName()).build();

            // test
            System.out.println(jobDetail.getKey());

            getScheduler().scheduleJob(jobDetail, cronJob.getTrigger());
            logger.info("Cron job \"" + cronJob.getClass().getSimpleName() + "\" registered.");
        } catch (Exception ex) {
            throw SpincastStatics.runtimize(ex);
        }
    }

    public void registerBoundedCronJobs() {
        for (SpincastCronJob cronJob : getCronJobs()) {
            registerCronJob(cronJob);
        }
    }

    protected boolean registerCronJobInTestingMode() {
        return false;
    }

}
