package com.example.batch;

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
    private final String jobStatus;
}