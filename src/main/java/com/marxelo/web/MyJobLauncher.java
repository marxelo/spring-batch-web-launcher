package com.marxelo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
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

    @Autowired
    private Job creditJob;

    private JobExecution execution;

    // String msg = null;

    public ExecutionRequestResponse run(String jobName, String fileDate, String timeString) {
        StringBuilder errorMessage = new StringBuilder();
        String msg = null;
        String jobStatus;
        try {
            if (jobName.equals("personJob")) {
                execution = jobLauncher.run(personJob,
                        new JobParametersBuilder()
                                .addString("fileDate", fileDate)
                                .addString("timeString", timeString)
                                .toJobParameters());
            } else {
                execution = jobLauncher.run(creditJob,
                        new JobParametersBuilder()
                                .addString("fileDate", fileDate)
                                .addString("timeString", timeString)
                                .toJobParameters());
            }
            LOGGER.info("Job Started");
            System.out.println("Execution status: " + execution.getExitStatus());
            System.out.println("Execution status: " + execution.getStatus());
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
                errorMessage.append(e.getClass() + "\n");
            }
            msg = errorMessage.toString();
            System.out.println(msg);
            msg = msg.replace("\n", " ");
            jobStatus = "FAILED";
        }
        return new ExecutionRequestResponse(jobStatus, msg);
    }

}
