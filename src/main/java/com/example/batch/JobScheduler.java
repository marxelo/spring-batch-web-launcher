package com.example.batch;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
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
public class JobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job creditJob;

    private JobExecution execution;

    String msg = null;

    @Value("${spring.main.web_environment}")
    private boolean webEnv;

    public String run(String processingDate) {
        System.out.println("webEnv: " + webEnv);

        LOGGER.info("Processing date> " + processingDate);
        try {
            execution = jobLauncher.run(creditJob,
                    new JobParametersBuilder().addString("processingDate", processingDate).toJobParameters());
            LOGGER.info("Job Started");
            System.out.println("Execution status: " + execution.getStatus());
            msg = execution.getStatus().toString();
        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        } catch (JobRestartException e) {
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
        } catch (JobParametersInvalidException e) {
            e.printStackTrace();
        } finally {
            if (msg == null) {
                msg = "Job Error";

            }
        }
        return msg;
    }

    public void runBatchJob() throws JobExecutionAlreadyRunningException, JobRestartException,
            JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        LOGGER.info("start runBatchJob");
        jobLauncher.run(creditJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());

    }

}
