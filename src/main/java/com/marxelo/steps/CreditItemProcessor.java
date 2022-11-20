package com.marxelo.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreditItemProcessor implements ItemProcessor<String, String> {

    @Nullable
    @Override
    public String process(final @NonNull String item) throws Exception {
        log.debug("Credit processor-processing item: " + item);
        return item;
    }
}