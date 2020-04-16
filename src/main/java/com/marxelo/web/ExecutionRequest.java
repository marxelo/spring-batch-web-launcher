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
    
    private int sequencial;
    private String jobName;
    private String fileDate;
    

}