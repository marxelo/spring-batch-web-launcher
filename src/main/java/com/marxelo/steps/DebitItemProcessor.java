package com.marxelo.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public class DebitItemProcessor implements ItemProcessor<String, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebitItemProcessor.class);

    static Resource[] resources = new Resource[] { new ClassPathResource("data1.csv"), new ClassPathResource("data2.csv") };

    private StepExecution stepExecution;

    @Nullable
    @Override
    public String process(final String item) throws Exception {
        final ExecutionContext executionContext = stepExecution.getExecutionContext();
        final int resourceIndex = executionContext.getInt("MultiResourceItemReader.resourceIndex");
        LOGGER.info("processing item = " + item + " coming from resource = "
                + resources[resourceIndex + 1]);
        return item;
    }
}