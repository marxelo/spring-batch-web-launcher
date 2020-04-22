package com.marxelo.web;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class CustomJobExecution {

    @NotNull
    @Size(min = 1, max = 32, message = "Selecione um job")
    private String jobName;

    @NotNull
    @Size(min = 10, max = 10, message = "Selecione uma data")
    private String fileDate;

    @NotNull(message = "Informe um sequencial")
    @Min(0)
    @Max(99)
    private int sequencial;

    private String jobStatus;
    private String jobExecutionDetail;
    private String StepExecutionDetail;
    private String message;

    /**
     * @param jobName
     * @param fileDate
     * @param sequencial
     */
    public CustomJobExecution(String jobName, String fileDate, int sequencial) {
        this.jobName = jobName;
        this.fileDate = fileDate;
        this.sequencial = sequencial;
    }

    /**
     * @param jobStatus
     * @param message
     */
    public CustomJobExecution(String jobStatus, String message) {
        this.jobStatus = jobStatus;
        this.message = message;
    }

    /**
     * @param message
     */
    public CustomJobExecution(String message) {
        this.message = message;
    }

    /**
     * @param jobName
     * @param fileDate
     * @param jobStatus
     * @param message
     */
    public CustomJobExecution(@NotNull @Size(min = 1, max = 32, message = "Selecione um job") String jobName,
            @NotNull @Size(min = 10, max = 10, message = "Selecione uma data") String fileDate, String jobStatus,
            String message) {
        this.jobName = jobName;
        this.fileDate = fileDate;
        this.jobStatus = jobStatus;
        this.message = message;
    }

}