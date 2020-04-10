package com.marxelo.steps.tasklets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class DownloadFileTasklet implements Tasklet {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadFileTasklet.class);

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
            throws Exception {
        LOGGER.info("Utilizando a data de processamento = ");
        return RepeatStatus.FINISHED;
    }
}