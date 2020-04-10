package com.marxelo.steps.skippers;

import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MySkipListener {

    @OnSkipInRead
    public void onSkipInRead(Throwable throwable) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Record skipped in reader.\n");
        errorMessage.append("Message: " + throwable.getMessage() + "\n");
        errorMessage.append("Exception: " + throwable.getClass() + "\n");
        if (throwable.getLocalizedMessage() != null) {
            errorMessage.append("Localized Message: " + throwable.getLocalizedMessage() + "\n");
        }
        if (throwable.getCause() != null) {
            errorMessage.append("Exception Cause: " + throwable.getCause() + "\n");
        }
        if (throwable instanceof FlatFileParseException) {
            FlatFileParseException ffpe = (FlatFileParseException) throwable;
            errorMessage.append("Skipped record/line: " + ffpe.getLineNumber() + "\n");
            errorMessage.append("Faulty Record: >>" + ffpe.getInput() + "<<\n");
        }
        LOGGER.warn("{}", errorMessage.toString());

    }

    @OnSkipInWrite
    public void onSkipInWrite(String item, Throwable throwable) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Record skipped in Writer. Message " + throwable.getMessage());
        errorMessage.append("Message: " + throwable.getMessage() + "\n");
        errorMessage.append("Exception: " + throwable.getClass() + "\n");
        if (throwable.getLocalizedMessage() != null) {
            errorMessage.append("Localized Message: " + throwable.getLocalizedMessage() + "\n");
        }
        if (throwable.getCause() != null) {
            errorMessage.append("Exception Cause: " + throwable.getCause() + "\n");
        }
        errorMessage.append("MasterDebitTransaction: " + item.toString());
        LOGGER.warn("{}", errorMessage.toString());
    }

    @OnSkipInProcess
    public void onSkipInProcess(String item, Throwable throwable) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Message: " + throwable.getMessage() + "\n");
        errorMessage.append("Exception: " + throwable.getClass() + "\n");
        if (throwable.getLocalizedMessage() != null) {
            errorMessage.append("Localized Message: " + throwable.getLocalizedMessage() + "\n");
        }
        if (throwable.getCause() != null) {
            errorMessage.append("Exception Cause: " + throwable.getCause() + "\n");
        }
        errorMessage.append("MasterDebitTransaction: " + item.toString());
        LOGGER.warn("{}", errorMessage.toString());

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MySkipListener.class);
}
