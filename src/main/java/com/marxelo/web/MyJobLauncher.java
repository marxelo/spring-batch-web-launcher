package com.marxelo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
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

    private JobExecution execution;

    @Autowired
    public MyJobLauncher(@Qualifier("asyncJobLauncher") SimpleJobLauncher jobLauncher, Job personJob) {
        super();
        this.jobLauncher = jobLauncher;
        this.personJob = personJob;
    }

    public ExecutionRequestResponse run(String jobName, String fileDate, String sequencial) {
        StringBuilder errorMessage = new StringBuilder();
        String msg = null;
        String jobStatus;
        try {
            if (jobName.equals("personJob")) {
                execution = jobLauncher.run(personJob,
                        new JobParametersBuilder()
                                .addString("fileDate", fileDate)
                                .addString("sequencial", sequencial)
                                .toJobParameters());
            } else {
                execution = jobLauncher.run(creditJob,
                        new JobParametersBuilder()
                                .addString("fileDate", fileDate)
                                .addString("sequencial", sequencial)
                                .toJobParameters());
            }
            LOGGER.info("Job Started");
            jobStatus = execution.getStatus().toString();
        } catch (Exception e) {
            // e.printStackTrace();
            errorMessage.append("Erro ao executar job.\n");
            if (e.getMessage() != null) {
                errorMessage.append("Message: " + e.getMessage() + "\n");
            }
            if ((e.getLocalizedMessage() != null)
                    && !(e.getMessage().toString().equals(e.getLocalizedMessage().toString()))) {
                errorMessage.append("LocalizedMessage: " + e.getLocalizedMessage() + "\n");
            }
            if (e.getCause() != null) {
                errorMessage.append("Cause: " + e.getCause() + "\n");
            }
            if (e.getClass() != null) {
                errorMessage.append("Exception " + e.getClass() + "\n");
            }
            msg = errorMessage.toString();
            System.out.println(msg);
            msg = msg.replace("\n", " ");
            jobStatus = "FAILED";
        }
        return new ExecutionRequestResponse(jobStatus, msg);
    }

}
