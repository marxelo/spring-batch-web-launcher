package com.marxelo.web;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
public class ExecutionRequest {

    @NotNull
    @Size(min = 1, max = 32, message = "Selecione um job")
    private String jobName;

    @NotNull
    @Size(min = 10, max = 10, message = "Selecione uma data")
    // @Pattern(regexp = "^[0-9][0-9]{4}-^[0-9][0-9]{2}-^[0-9][0-9]{2}$", message = "Selecione um data")
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