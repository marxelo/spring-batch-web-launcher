package com.marxelo.steps.tasklets;

import java.io.File;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class FileDeletingTasklet implements Tasklet, InitializingBean {

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        String formattedString = (String) chunkContext.getStepContext().getJobParameters().get("fileDate");
        String BASE_DIR = "src/main/resources/";

        Resource[] resources = {
                new FileSystemResource(
                        BASE_DIR + "pessoas-" + formattedString + ".V1.txt"),
                new FileSystemResource(
                        BASE_DIR + "pessoas-" + formattedString + ".V2.txt"),
                new FileSystemResource(
                        BASE_DIR + "pessoas-" + formattedString + ".V3.txt") };

        for (Resource r : resources) {
            File file = r.getFile();
            boolean deleted = file.delete();
            if (!deleted) {
                System.out.println("Could not delete file " + file.getPath());
                throw new UnexpectedJobExecutionException("Could not delete file " + file.getPath());
            } else {
                System.out.println("file deleted: " + file.getPath()); 
            }
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO Auto-generated method stub

    }
}