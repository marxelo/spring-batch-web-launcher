package com.marxelo.web.controller;

import javax.validation.Valid;

import com.marxelo.web.JobDetail;
import com.marxelo.web.MyJobLauncher;
import com.marxelo.web.model.CustomJobExecution;

import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@CrossOrigin
public class JobController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyJobLauncher.class);

  @Autowired
  MyJobLauncher myJobLauncher;

  @Autowired
  JobDetail jobDetail;

  @GetMapping("/job-manager")
  public String formSubmission(Model model) {

    model.addAttribute("customJobExecution", new CustomJobExecution());
    return "request";
  }

  @RequestMapping(value = "/job-manager", method = RequestMethod.POST, params = "action=submit")
  public String submitJob(@Valid CustomJobExecution customJobExecution, BindingResult bindingResult, Model mode) {

    String fileDate = customJobExecution.getFileDate().replaceAll("[^0-9]", "");
    String jobName = customJobExecution.getJobName();
    String identifier = customJobExecution.getIdentifier() + "";
    customJobExecution.setMessage(null);

    if (bindingResult.hasErrors()) {
      return "request";
    }

    if (!JobNameIsValid(jobName)) {
      customJobExecution.setMessage("JobName não informado");
      return "request";
    }

    if (!FileDateIsValid(fileDate)) {
      customJobExecution.setMessage("Data do arquivo não informada");
      return "request";
    }

    CustomJobExecution cje = myJobLauncher.run(jobName, fileDate, identifier);
    LOGGER.info(cje.getJobStatus());
    customJobExecution.setJobStatus(cje.getJobStatus());
    customJobExecution.setMessage(cje.getMessage());

    if (cje.getMessage() == null) {
      customJobExecution.setMessage("Aguarde conclusão do job!");
      return "request";
    }

    return "response";
  }

  @RequestMapping(value = "/job-manager", method = RequestMethod.POST, params = "action=detail")
  public String detailJob(@Valid CustomJobExecution customJobExecution, BindingResult bindingResult, Model mode) {

    String fileDate = customJobExecution.getFileDate().replaceAll("[^0-9]", "");
    String jobName = customJobExecution.getJobName();
    String identifier = customJobExecution.getIdentifier() + "";
    customJobExecution.setMessage(null);

    if (bindingResult.hasErrors()) {
      return "request";
    }

    if (!JobNameIsValid(jobName)) {
      customJobExecution.setMessage("JobName não informado");
      return "request";
    }

    if (!FileDateIsValid(fileDate)) {
      customJobExecution.setMessage("Data do arquivo não informada");
      return "request";
    }

    CustomJobExecution cje = jobDetail.getJobDetail(jobName, fileDate, identifier);
    LOGGER.info(cje.getJobStatus());
    customJobExecution.setJobStatus(cje.getJobStatus());
    customJobExecution.setMessage(cje.getMessage());
    customJobExecution.setJobExecutionDetail(cje.getJobExecutionDetail());
    customJobExecution.setStepExecutionDetail(cje.getStepExecutionDetail());

    return "response";
  }

  @GetMapping("/startJob")
  public CustomJobExecution jer(@RequestParam(value = "jobName", defaultValue = "creditJob") String jobName,
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

  @GetMapping({ "", "/", "/**", "index", "index.html" })
  // as by default Spring maps unknown urls to "/**"
  public String notFound404() {
    return "redirect:job-manager";
    // return new CustomJobExecution("Bad request. Informe no formato
    // /startJob?jobName=xxxx&fileDate=YYYYMMdd");
  }

  public Boolean JobNameIsValid(String jobName) {
    return (jobName.equals("creditJob") || jobName.equals("personJob") || jobName.equals("jobStepJob"));
  }

  public Boolean FileDateIsValid(String fileDate) {
    return (GenericValidator.isDate(fileDate, "yyyyMMdd", true));
  }

}
