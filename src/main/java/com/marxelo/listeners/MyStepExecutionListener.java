package com.marxelo.listeners;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
       log.info("Called BEFORE step " + stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Called AFTER step " + stepExecution.getStepName());
        log.info(stepExecution.getStepName() + " Summary: " + stepExecution.getSummary());
        return ExitStatus.COMPLETED;
    }
}