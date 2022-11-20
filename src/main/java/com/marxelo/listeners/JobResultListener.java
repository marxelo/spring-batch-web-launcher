package com.marxelo.listeners;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marxelo.models.dtos.StepExecutionSummary;

import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;

@Slf4j
public class JobResultListener implements JobExecutionListener {

  public void beforeJob(JobExecution jobExecution) {
    log.info("Called BEFORE job " + jobExecution.getJobInstance().getJobName());
  }

  public void afterJob(JobExecution jobExecution) {
    log.info("Called AFTER job " + jobExecution.getJobInstance().getJobName());

    if (!jobExecution.getStepExecutions().isEmpty()) {
      for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
        if (!stepExecution.getExitStatus().getExitCode().toString().equals("COMPLETED")) {
          jobExecution.setExitStatus(new ExitStatus("WARNING", stepExecution.getStepName() +
              " ended with " + stepExecution.getExitStatus()));
        }
      }
    }

    printJobSummary(jobExecution);
  }

  private void printJobSummary(JobExecution jobExecution) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    log.info("logging Execution summary");

    log.info("jobInstance: " + gson.toJson(jobExecution.getJobInstance()));

    log.info("jobParameters: " + gson.toJson(jobExecution.getJobParameters()));

    for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
      log.info(gson.toJson(new StepExecutionSummary(stepExecution)) + ",");
    }

    if (!jobExecution.getExitStatus().getExitCode().equals("COMPLETED")) {
      log.warn("Job " + jobExecution.getJobInstance().getJobName().toUpperCase() +
          " ended with status " + jobExecution.getStatus() + " and exitStatus " +
          jobExecution.getExitStatus().getExitCode());

      log.warn("Reason: " + jobExecution.getExitStatus().getExitDescription());
    } else {
      log.info("Job " + jobExecution.getJobInstance().getJobName().toUpperCase() +
          " ended with status " + jobExecution.getStatus() + " and exitStatus " +
          jobExecution.getExitStatus().getExitCode());
    }

  }
}
