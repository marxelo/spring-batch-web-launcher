package com.marxelo.web;

import javax.persistence.criteria.CriteriaBuilder.Case;

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

import lombok.extern.slf4j.Slf4j;

@EnableScheduling
@Configuration
@Slf4j
public class MyJobLauncher {

    @Autowired
    private SimpleJobLauncher jobLauncher;

    @Autowired
    private Job personJob;

    @Autowired
    private Job creditJob;
    
    @Autowired
    private Job slimPersonJob;

    @Autowired
    private Job debitJob;

    @Autowired
    private Job parentJob;

    private JobExecution execution;

    @Autowired
    public MyJobLauncher(@Qualifier("asyncJobLauncher") SimpleJobLauncher jobLauncher, Job personJob) {
        super();
        this.jobLauncher = jobLauncher;
        this.personJob = personJob;
    }

    public CustomJobExecution run(String jobName, String fileDate, String identifier) {

        StringBuilder errorMessage = new StringBuilder();
        String msg = null;
        String jobStatus;

        try {
            switch (jobName) {
                case "personJob":
                    execution = jobLauncher.run(personJob, new JobParametersBuilder()
                            .addString("fileDate", fileDate)
                            .addString("identifier", identifier)
                            .toJobParameters());
                    break;
                case "creditJob":
                    execution = jobLauncher.run(creditJob, new JobParametersBuilder()
                            .addString("fileDate", fileDate)
                            .addString("identifier", identifier)
                            .toJobParameters());
                    break;
                case "debitJob":
                    execution = jobLauncher.run(debitJob, new JobParametersBuilder()
                            .addString("fileDate", fileDate)
                            .addString("identifier", identifier)
                            .toJobParameters());
                    break;
                case "slimPersonJob":
                    execution = jobLauncher.run(slimPersonJob, new JobParametersBuilder()
                            .addString("fileDate", fileDate)
                            .addString("identifier", identifier)
                            .toJobParameters());
                    break;
                default:
                    execution = jobLauncher.run(parentJob, new JobParametersBuilder()
                            .addString("fileDate", fileDate)
                            .addString("identifier", identifier)
                            .toJobParameters());
                    break;
            }

            log.info("Launching job " + jobName);
            jobStatus = execution.getStatus().toString();
        } catch (Exception e) {
            errorMessage.append("Erro ao executar job. ");
            log.error("Erro ao executar o job " + jobName, e);
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

            if (e instanceof JobInstanceAlreadyCompleteException || e instanceof JobExecutionAlreadyRunningException) {
                jobStatus = execution.getStatus().toString();
            } else {
                jobStatus = "FAILED";
            }
        }
        return new CustomJobExecution(jobStatus, msg);
    }

}