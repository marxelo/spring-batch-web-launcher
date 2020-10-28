package com.marxelo.configuration.scheduler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class PersonJobScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonJobScheduler.class);

    @Autowired
    private SimpleJobLauncher jobLauncher;

    @Autowired
    private Job visaJob;

    @Value("${batch.visa-job.enabled:false}")
    private boolean enabled = true;

    @Scheduled(cron = "${batch.cron.schedule.visa-job}")
    public void runBatchJob()
            throws JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException,
            JobParametersInvalidException {
        System.out.println("start personJob");

        if (enabled) {
          jobLauncher.run(visaJob, new JobParametersBuilder()
                    .addString("fileDate", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString())
                    .addString("identifier", "0")
                    .toJobParameters());
                    System.out.println("end personJob");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
