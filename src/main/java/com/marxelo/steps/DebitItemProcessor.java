package com.marxelo.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.Nullable;

public class DebitItemProcessor implements ItemProcessor<String, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebitItemProcessor.class);

    @Nullable
    @Override
    public String process(final String item) throws Exception {
        LOGGER.info("Debit processor-processing item: " + item);
        return item;
    }
}