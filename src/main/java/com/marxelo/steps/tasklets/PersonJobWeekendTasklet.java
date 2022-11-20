package com.marxelo.steps.tasklets;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersonJobWeekendTasklet implements Tasklet {
    

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
            throws Exception {

        log.warn("File not found. Today is a Saturday. Job will not fail!");
        
        return RepeatStatus.FINISHED;
    }
}