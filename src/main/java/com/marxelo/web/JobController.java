package com.marxelo.web;

import javax.validation.Valid;

import com.marxelo.web.models.JobExecutionRequest;

import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
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

  @PutMapping("/job/execution-request")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public ResponseEntity<Void> startJob(@Valid @RequestBody JobExecutionRequest jobExecutionRequest) {

    System.out.println(jobExecutionRequest);

    if (!JobNameIsValid(jobExecutionRequest.getJobName())) {
      System.out.println("local: 1.0");
      return ResponseEntity.badRequest().build();
    }

    if (jobExecutionRequest.getJobParms().isEmpty()) {
      System.out.println("local: 2.0");
      return ResponseEntity.badRequest().build();
    }

    if (!jobExecutionRequest.getJobParms().get(0).getParmName().equals("fileDate")) {
      System.out.println("local: 3.0");
      return ResponseEntity.badRequest().build();
    }

    if (!FileDateIsValid(jobExecutionRequest.getJobParms().get(0).getParmValue())) {
      System.out.println("local: 4.0");
      return ResponseEntity.badRequest().build();
    }

    if (!jobExecutionRequest.getJobParms().get(1).getParmName().equals("identifier")) {
      System.out.println("local: 5.0");
      return ResponseEntity.badRequest().build();
    }

    if (!isValidIdentifier(jobExecutionRequest.getJobParms().get(1).getParmValue())) {
      System.out.println("local: 6.0");
      return ResponseEntity.badRequest().build();
    }
    System.out.println("local: 7.0");
    String jobName = jobExecutionRequest.getJobName();
    System.out.println("local: 7.1");
    String fileDate = jobExecutionRequest.getJobParms().get(0).getParmValue();
    System.out.println("local: 7.2");
    String identifier = jobExecutionRequest.getJobParms().get(1).getParmValue();
    System.out.println("local: 7.3");

    CustomJobExecution cje = myJobLauncher.run(jobName, fileDate, identifier);
    System.out.println("local: 8.0");
    LOGGER.info(cje.getJobStatus());
    // customJobExecution.setJobStatus(cje.getJobStatus());
    // customJobExecution.setMessage(cje.getMessage());

    if (cje.getMessage() != null) {
      System.out.println(cje.getMessage());
      return ResponseEntity.badRequest().build();
    }
    System.out.println("local: 10.0");
    return ResponseEntity.accepted().build();
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

  public Boolean isValidIdentifier(String identifier) {
    try {
      return (Integer.parseInt(identifier) >= 0 && Integer.parseInt(identifier) <= 100);
    } catch (NumberFormatException e) {
      return false;
    } catch (NullPointerException e) {
      return false;
    }
  }
}
