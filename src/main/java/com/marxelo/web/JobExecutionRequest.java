package com.marxelo.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class JobExecutionRequest {

    private String jobName;
    private String fileDate;
    private String jobStatus;
    private String message;

    /**
     * @param message
     */
    public JobExecutionRequest(String message) {
        this.message = message;
    }

    /**
     * @param jobName
     * @param fileDate
     * @param jobStatus
     */
    public JobExecutionRequest(String jobName, String fileDate, String jobStatus) {
        this.jobName = jobName;
        this.fileDate = fileDate;
        this.jobStatus = jobStatus;
    }

    
    



}