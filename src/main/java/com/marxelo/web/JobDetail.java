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
public class JobDetail {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyJobLauncher.class);

    @Autowired
    private JobExplorer jobExplorer;

    public ExecutionRequest getJobDetail(String jobName, String fileDate, String sequencial) {

        ExecutionRequest er = new ExecutionRequest(jobName, fileDate, Integer.parseInt(sequencial));

        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("fileDate", fileDate);
        jpb.addString("sequencial", sequencial);
        JobParameters jobParameters = jpb.toJobParameters();
        int JOB_INSTANCE_COUNT = 30;

        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, JOB_INSTANCE_COUNT);

        instanceForLoop: for (JobInstance jobInstance : jobInstances) {

            List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);

            for (JobExecution jobExecution : jobExecutions) {

                if (jobExecution.getJobParameters().toString().equals(jobParameters.toString())) {
                    er.setJobStatus(jobExecution.getStatus().toString());
                    er.setJobExecutionDetail(jobExecution.toString());
                    if (jobExecution.getStatus().toString().equals("FAILED")) {
                        er.setMessage(jobExecution.getExitStatus().getExitDescription());
                    }

                    Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
                    for (StepExecution stepExecution : stepExecutions) {

                        if ((stepExecution.getStepName().equals("personStep"))
                                || (stepExecution.getStepName().equals("creditStep"))) {

                            er.setStepExecutionDetail(stepExecution.toString());
                            break instanceForLoop;
                        }
                    }
                }
            }
        }

        if (er.getJobStatus() == null) {
            er.setMessage("No information found for job {" + jobName + "} with parameters " + jobParameters.toString());
            LOGGER.info(er.toString());
        }

        return er;
    }
}