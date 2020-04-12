package com.marxelo.steps.skippers;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;

public class MySkipPolicy implements SkipPolicy {

    private static final int MAX_SKIP_COUNT = 999999;

    @Override
    public boolean shouldSkip(Throwable throwable, int skipCount)
            throws SkipLimitExceededException {

        if (skipCount < MAX_SKIP_COUNT) {
            if ((throwable instanceof FlatFileParseException)
                    || (throwable instanceof NumberFormatException)
                    || (throwable instanceof NullPointerException)
                    || (throwable instanceof StringIndexOutOfBoundsException)
                    || (throwable instanceof ArithmeticException)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }
}
