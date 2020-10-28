package com.marxelo.configuration.scheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class CreditJobScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreditJobScheduler.class);

    @Autowired
    private SimpleJobLauncher jobLauncher;

    @Autowired
    private Job creditJob;

    private boolean enabled = true;

    @Scheduled(cron = "${batch.cron.schedule.credit-job}")
    public void runBatchJob()
            throws JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException,
            JobParametersInvalidException {
        System.out.println("start creditJob");

        if (enabled) {
          jobLauncher.run(creditJob, new JobParametersBuilder()
                    .addString("fileDate", "20201023")
                    .addString("identifier", "0")
                    .addDate("Date", new Date())
                    .toJobParameters());
                    System.out.println("end CreditJob");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
