package com.marxelo.configuration;

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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class MyJobLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyJobLauncher.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job personJob;

    private JobExecution execution;

    String msg = null;

    public String run(String fileDate) {

        LOGGER.info("File date............: " + fileDate);
        try {
            execution = jobLauncher.run(personJob,
                    new JobParametersBuilder().addString("fileDate", fileDate).toJobParameters());
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

}
