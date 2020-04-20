package com.marxelo.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
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

    private JobExecution execution;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    public MyJobLauncher(@Qualifier("asyncJobLauncher") SimpleJobLauncher jobLauncher, Job personJob) {
        super();
        this.jobLauncher = jobLauncher;
        this.personJob = personJob;
    }

    public ExecutionRequest run(String jobName, String fileDate, String sequencial) {

        StringBuilder errorMessage = new StringBuilder();
        String msg = null;
        String jobStatus;
        var activeExecutions = jobExplorer.findRunningJobExecutions(jobName);
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 3);

        for (JobInstance jobInstance : jobInstances) {
            List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
            for (JobExecution jobExecution : jobExecutions) {
                // Make sure the execution id is the current id
                // Then get the list of stepExecutions from jobExecution
                // jobExecution.getJobParameters()
                System.out.println("Exec to string+++++++: " + jobExecution.toString());
                System.out.println("Execution Status.......: " +
                        jobExecution.getExitStatus());

            }
        }
        System.out.println(activeExecutions + "<<<<<<<<<<<<<<<<<<");
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
            String str = execution.getStatus().toString();
            System.out.println("exc est................................: " + str);
            if (e instanceof JobInstanceAlreadyCompleteException || e instanceof JobExecutionAlreadyRunningException) {
                jobStatus = execution.getStatus().toString();
            } else {
                jobStatus = "FAILED";
            }
        }
        return new ExecutionRequest(jobStatus, msg);
    }

    public ExecutionRequest getJobDetail(String jobName, String fileDate, String sequencial) {

        StringBuilder errorMessage = new StringBuilder();
        String msg = null;
        String jobStatus;
        var activeExecutions = jobExplorer.findRunningJobExecutions(jobName);
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("fileDate", fileDate);
        jpb.addString("sequencial", sequencial);
        JobParameters jobParameters = jpb.toJobParameters();

        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 3);

        for (JobInstance jobInstance : jobInstances) {
            List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
            for (JobExecution jobExecution : jobExecutions) {
                // Make sure the execution id is the current id
                // Then get the list of stepExecutions from jobExecution
                // jobExecution.getJobParameters()
                if (jobExecution.getJobParameters().toString().equals(jobParameters.toString())) {
                    System.out.println("ÉÉÉÉÉÉÉÉÉÉÉÉ IGUAL");
                    return new ExecutionRequest(jobExecution.getStatus().toString(), "ffddf");
                } else {
                    System.out.println(jobExecution.getJobParameters() + "<---->" + jobParameters);
                }
                System.out.println("Exec to string+++++++: " + jobExecution.toString());
                System.out.println("Execution Status.......: " +
                        jobExecution.getExitStatus());

            }
        }

        return new ExecutionRequest("NOT FOUND", "NOT FOUNDX");
    }

}
