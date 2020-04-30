package com.marxelo.web;

import javax.validation.Valid;

import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class JobController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyJobLauncher.class);

  @Autowired
  MyJobLauncher myJobLauncher;

  @Autowired
  JobDetail jobDetail;

  @GetMapping("/job/start")
  public CustomJobExecution jer(
      @RequestParam(value = "jobName", defaultValue = "creditJob") String jobName,
      @RequestParam(value = "fileDate") String fileDate,
      @RequestParam(value = "identifier", defaultValue = "0") String identifier) {

    if (!JobNameIsValid(jobName)) {
      return new CustomJobExecution("Invalid job name. Informe no formato /startJob?jobName=xxxx&fileDate=YYYYMMdd");
    }

    if (!FileDateIsValid(fileDate)) {
      return new CustomJobExecution("Invalid date. Informe no formato /startJob?jobName=xxxx&fileDate=YYYYMMdd");
    }

    CustomJobExecution ere = myJobLauncher.run(jobName, fileDate, identifier);
    LOGGER.info(ere.getJobStatus());
    return new CustomJobExecution(jobName, fileDate, ere.getJobStatus(), ere.getMessage());
  }

  @GetMapping("/job/detail")
  public CustomJobExecution jed(
      @RequestParam(value = "jobName", defaultValue = "creditJob") String jobName,
      @RequestParam(value = "fileDate") String fileDate,
      @RequestParam(value = "identifier", defaultValue = "0") String identifier) {

    if (!JobNameIsValid(jobName)) {
      return new CustomJobExecution("Invalid job name. Informe no formato /startJob?jobName=xxxx&fileDate=YYYYMMdd");
    }

    if (!FileDateIsValid(fileDate)) {
      return new CustomJobExecution("Invalid date. Informe no formato /startJob?jobName=xxxx&fileDate=YYYYMMdd");
    }

    CustomJobExecution ere = jobDetail.getJobDetail(jobName, fileDate, identifier);
    LOGGER.info(ere.getJobStatus());
    return new CustomJobExecution(jobName, fileDate, Integer.parseInt(identifier), ere.getJobStatus(), ere.getJobExecutionDetail(), ere.getStepExecutionDetail(), ere.getMessage());
  }

  @GetMapping({ "/job/cronJobStart" })
  public String cronJob() {
    return "job submitted";
  }

  @GetMapping({ "/**" })
  public String notFound404() {
    return "Bad request. Informe no formato /startJob?jobName=xxxx&fileDate=YYYYMMdd";
  }

  public Boolean JobNameIsValid(String jobName) {
    return (jobName.equals("creditJob") || jobName.equals("personJob"));
  }

  public Boolean FileDateIsValid(String fileDate) {
    return (GenericValidator.isDate(fileDate, "yyyyMMdd", true));
  }

}
