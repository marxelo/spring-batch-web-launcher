package com.marxelo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Configuration
public class MyJobLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyJobLauncher.class);

    @Autowired
    private SimpleJobLauncher jobLauncher;

    @Autowired
    private Job personJob;

    @Autowired
    private Job creditJob;

    @Autowired
    private Job jobStepJob;

    private JobExecution execution;

    @Autowired
    public MyJobLauncher(@Qualifier("asyncJobLauncher") SimpleJobLauncher jobLauncher, Job personJob) {
        super();
        this.jobLauncher = jobLauncher;
        this.personJob = personJob;
    }

    public CustomJobExecution run(String jobName, String fileDate, String identifier) {
        System.out.println("local: 2.1.0");
        StringBuilder errorMessage = new StringBuilder();
        String msg = null;
        String jobStatus;
        System.out.println("local: 2.2.0");

        try {
            System.out.println("local: 2.3.0");
            if (jobName.equals("personJob")) {
                System.out.println("local: 2.4.0");
                execution = jobLauncher.run(personJob, new JobParametersBuilder()
                        .addString("fileDate", fileDate)
                        .addString("identifier", identifier)
                        .toJobParameters());
                System.out.println("local: 2.5.0");
            }
            if (jobName.equals("creditJob")) {
                execution = jobLauncher.run(creditJob, new JobParametersBuilder()
                        .addString("fileDate", fileDate)
                        .addString("identifier", identifier)
                        .toJobParameters());
            } else {
                execution = jobLauncher.run(jobStepJob, new JobParametersBuilder()
                        .addString("fileDate", fileDate)
                        .addString("identifier", identifier)
                        .toJobParameters());
            }
            LOGGER.info("Job Started");
            System.out.println("local: 2.6.0");
            jobStatus = execution.getStatus().toString();
            System.out.println("local: 2.7.0");
        } catch (Exception e) {
            System.out.println("local: 2.8.0");
            errorMessage.append("Erro ao executar job. ");
            if (e.getMessage() != null) {
                errorMessage.append("Message: " + e.getMessage() + " ");
            }
            if ((e.getLocalizedMessage() != null)
                    && !(e.getMessage().toString().equals(e.getLocalizedMessage().toString()))) {
                errorMessage.append("LocalizedMessage: " + e.getLocalizedMessage() + " ");
            }
            if (e.getCause() != null) {
                errorMessage.append("Cause: " + e.getCause() + " ");
            }
            if (e.getClass() != null) {
                errorMessage.append("Exception " + e.getClass() + " ");
            }
            msg = errorMessage.toString();
            System.out.println("local: 2.9.0");

            if (e instanceof JobInstanceAlreadyCompleteException) {
                System.out.println("local: 2.10.0");
                System.out.println("msg: " + msg);
                jobStatus = "COMPLETED";
            } else if (e instanceof JobExecutionAlreadyRunningException) {
                System.out.println("local: 2.10.0");
                System.out.println("msg: " + msg);
                jobStatus = execution.getStatus().toString();
            } else {
                System.out.println("local: 2.11.0");
                jobStatus = "FAILED";
            }
        }
        System.out.println("local: 2.12.0");
        return new CustomJobExecution(jobStatus, msg);
    }

}