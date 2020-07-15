package com.marxelo.web;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobDetailX {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobDetail.class);

    @Autowired
    private JobExplorer jobExplorer;

    public CustomJobExecution getJobDetail(String jobName, String fileDate, String identifier) {

        CustomJobExecution cje = new CustomJobExecution(jobName, fileDate, Integer.parseInt(identifier));

        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("fileDate", fileDate);
        jpb.addString("identifier", identifier);
        JobParameters jobParameters = jpb.toJobParameters();
        int JOB_INSTANCE_COUNT = 10;

        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, JOB_INSTANCE_COUNT);

        instanceForLoop: for (JobInstance jobInstance : jobInstances) {
            List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
            try {
                for (JobExecution jobExecution : jobExecutions) {
                    if (jobExecution.getJobParameters().getString("fileDate").equals(fileDate) &&
                            jobExecution.getJobParameters().getString("identifier").equals(identifier)) {
                        cje.setJobStatus(jobExecution.getStatus().toString());
                        cje.setJobExecutionDetail(jobExecution.toString());
                        if (jobExecution.getStatus().toString().equals("FAILED")) {
                            cje.setMessage(jobExecution.getExitStatus().getExitDescription());
                        }

                        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
                        try {
                            for (StepExecution stepExecution : stepExecutions) {
                                if ((stepExecution.getStepName().equals("masterCreditProcessFileStep"))
                                        || (stepExecution.getStepName().equals("masterDebitProcessFileStep"))) {

                                    cje.setStepExecutionDetail(stepExecution.toString());
                                    break instanceForLoop;
                                }
                            }
                            break instanceForLoop;
                        } catch (Exception e) {
                            if (e instanceof NullPointerException) {
                                cje.setMessage("No information found for job {" + jobName + "} with parameters "
                                        + jobParameters.toString() + ".");
                            } else {
                                cje.setMessage("Exception " + e.getClass()
                                        + " occurred while searching information on the execution of {" + jobName
                                        + "} with parameters "
                                        + jobParameters.toString());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (e instanceof NullPointerException) {
                    cje.setMessage("No information found for job {" + jobName + "} with parameters: "
                            + jobParameters.toString());
                } else {
                    cje.setMessage("Exception " + e.getClass()
                            + " occurred while searching information on the execution of {" + jobName
                            + "} with parameters "
                            + jobParameters.toString());
                }
            }
        }
        if (cje.getJobStatus() == null) {
            cje.setMessage("No information found for job {" + jobName + "} with parameters " + jobParameters.toString());

        }
        LOGGER.info(cje.toString());
        return cje;
    }
}