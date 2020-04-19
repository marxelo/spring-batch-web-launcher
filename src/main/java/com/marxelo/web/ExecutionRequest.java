package com.marxelo.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class ExecutionRequest {

    private String jobName;
    private String fileDate;
    private int sequencial;
    private String jobStatus;
    private String message;

    /**
     * @param jobName
     * @param fileDate
     * @param sequencial
     */
    public ExecutionRequest(String jobName, String fileDate, int sequencial) {
        this.jobName = jobName;
        this.fileDate = fileDate;
        this.sequencial = sequencial;
    }

    /**
     * @param jobStatus
     * @param message
     */
    public ExecutionRequest(String jobStatus, String message) {
        this.jobStatus = jobStatus;
        this.message = message;
    }

}