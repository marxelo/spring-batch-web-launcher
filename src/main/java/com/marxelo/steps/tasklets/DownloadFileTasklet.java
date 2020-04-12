package com.marxelo.steps.tasklets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@StepScope
public class DownloadFileTasklet implements Tasklet {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadFileTasklet.class);

    @Value("#{jobParameters['fileDate']}")
    private String fileDate;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
            throws Exception {
        LOGGER.info("File Date in tasklet............: " + fileDate);
        System.out.println("File Date in tasklet............: " + fileDate);
        return RepeatStatus.FINISHED;
    }
}