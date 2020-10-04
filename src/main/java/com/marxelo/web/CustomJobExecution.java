package com.marxelo.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    /**
     * identifier - just a distinguisher to make possible to re-execute a COMPLETED job with the same fileDate
     */
    @NotNull(message = "Informe um Identificador")
    @Min(0)
    @Max(99)
    private int identifier;

    private String jobStatus;
    private String jobExecutionDetail;
    private String StepExecutionDetail;
    private String message;

    /**
     * @param jobName
     * @param fileDate
     * @param identifier
     */
    public CustomJobExecution(String jobName, String fileDate, int identifier) {
        this.jobName = jobName;
        this.fileDate = fileDate;
        this.identifier = identifier;
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