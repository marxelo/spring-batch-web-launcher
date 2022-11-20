package com.marxelo.steps.tasklets;

import com.marxelo.utils.DateUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class DownloadPersonFileTasklet implements Tasklet {

        @Override
        public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
                        throws Exception {

                String fileDate = (String) chunkContext.getStepContext().getJobParameters()
                                .get("fileDate");
                Path path = Paths.get("src/main/resources/pessoas-" + fileDate + ".txt");

                if (Files.notExists(path) && DateUtils.isWeekend(fileDate)) {

                        log.info("File Notfound on weekend day");

                        chunkContext.getStepContext().getStepExecution().setExitStatus(
                                        new ExitStatus("FILENOTFOUND", "Weekeend processing"));
                }
                
                return RepeatStatus.FINISHED;
        }
}
