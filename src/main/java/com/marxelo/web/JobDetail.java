package com.marxelo.web;

import java.util.Collection;
import java.util.List;

import com.marxelo.web.model.CustomJobExecution;

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
public class JobDetail {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyJobLauncher.class);

    @Autowired
    private JobExplorer jobExplorer;

    public CustomJobExecution getJobDetail(String jobName, String fileDate, String identifier) {

        CustomJobExecution cje = new CustomJobExecution(jobName, fileDate, Integer.parseInt(identifier));

        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("fileDate", fileDate);
        jpb.addString("identifier", identifier);
        JobParameters jobParameters = jpb.toJobParameters();
        int JOB_INSTANCE_COUNT = 30;

        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, JOB_INSTANCE_COUNT);

        instanceForLoop: for (JobInstance jobInstance : jobInstances) {

            List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);

            for (JobExecution jobExecution : jobExecutions) {

                if (jobExecution.getJobParameters().toString().equals(jobParameters.toString())) {
                    cje.setJobStatus(jobExecution.getStatus().toString());
                    cje.setJobExecutionDetail(jobExecution.toString());
                    if (jobExecution.getStatus().toString().equals("FAILED")) {
                        cje.setMessage(jobExecution.getExitStatus().getExitDescription());
                    }

                    Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
                    for (StepExecution stepExecution : stepExecutions) {

                        if ((stepExecution.getStepName().equals("personStep"))
                                || (stepExecution.getStepName().equals("creditStep"))) {

                            cje.setStepExecutionDetail(stepExecution.toString());
                            break instanceForLoop;
                        }
                    }
                }
            }
        }

        if (cje.getJobStatus() == null) {
            cje.setMessage("No information found for job {" + jobName + "} with parameters " + jobParameters.toString());
            LOGGER.info(cje.toString());
        }

        return cje;
    }
}