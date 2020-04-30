package com.marxelo.web;

import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class JobController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyJobLauncher.class);

  String BAD_REQUEST_MESSAGE = "Invalid job name. Informe no formato /job/{action}/{jobName}/{fileDate/{identifier}";
  String ACTION_START = "start";
  String ACTION_DETAIL = "detail";

  @Autowired
  MyJobLauncher myJobLauncher;

  @Autowired
  JobDetail jobDetail;

  @GetMapping("/job/{action}/{jobName}/{fileDate}/{identifier}")
  public CustomJobExecution jer(
      @PathVariable("action") String action,
      @PathVariable("jobName") String jobName,
      @PathVariable("fileDate") String fileDate,
      @PathVariable("identifier") String identifier) {

    CustomJobExecution response = new CustomJobExecution();
    response.setJobName(jobName);
    response.setFileDate(fileDate);

    if ((!action.equals(ACTION_START)) && (!action.equals(ACTION_DETAIL))) {
      response.setMessage("Invalid action request. " + BAD_REQUEST_MESSAGE);
      return response;
    }

    if (!JobNameIsValid(jobName)) {
      response.setMessage("Invalid jobname. " + BAD_REQUEST_MESSAGE);
      return response;
    }

    if (!FileDateIsValid(fileDate)) {
      response.setMessage("Invalid fileDate. " + BAD_REQUEST_MESSAGE);
      return response;
    }

    if (!isInteger(identifier)) {
      response.setMessage("Identifier is not an integer. " + BAD_REQUEST_MESSAGE);
      return response;
    }

    response.setIdentifier(Integer.parseInt(identifier));

    CustomJobExecution cje = new CustomJobExecution();

    if (action.equals(ACTION_START)) {
      cje = myJobLauncher.run(jobName, fileDate, identifier);
    } else {
      cje = jobDetail.getJobDetail(jobName, fileDate, identifier);
    }

    response.setJobExecutionDetail(cje.getJobExecutionDetail());
    response.setStepExecutionDetail(cje.getStepExecutionDetail());
    response.setJobStatus(cje.getJobStatus());
    response.setMessage(cje.getMessage());

    LOGGER.info(cje.getJobStatus());
    return response;
  }

  @GetMapping({ "/job/cronJobStart" })
  public String cronJob() {
    return "job submitted";
  }

  @GetMapping({ "/**" })
  public String notFound404() {
    return BAD_REQUEST_MESSAGE;
  }

  public Boolean JobNameIsValid(String jobName) {
    return (jobName.equals("creditJob") || jobName.equals("personJob"));
  }

  public Boolean FileDateIsValid(String fileDate) {
    return (GenericValidator.isDate(fileDate, "yyyyMMdd", true));
  }

  public Boolean isInteger(String identifier) {
    try {
      Integer.parseInt(identifier);
    } catch (NumberFormatException e) {
      return false;
    } catch (NullPointerException e) {
      return false;
    }
    return true;
  }

}
