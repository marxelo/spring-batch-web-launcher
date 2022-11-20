package com.marxelo.configuration;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.lang.Nullable;

public class NumberInfoDecider implements JobExecutionDecider {

    private static final String NOTIFY = "NOTIFY";
    private static final String QUIET = "QUIET";

    private boolean shouldNotify() {
        return true;
    }

    @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, @Nullable StepExecution stepExecution) {
        if (shouldNotify()) {
            return new FlowExecutionStatus(NOTIFY);
        } else {
            return new FlowExecutionStatus(QUIET);
        }
    }
}
