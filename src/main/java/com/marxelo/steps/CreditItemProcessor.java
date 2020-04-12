package com.marxelo.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.Nullable;

public class CreditItemProcessor implements ItemProcessor<String, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditItemProcessor.class);

    @Nullable
    @Override
    public String process(final String item) throws Exception {
        LOGGER.info("Credit processor-processing item: " + item);
        return item;
    }
}