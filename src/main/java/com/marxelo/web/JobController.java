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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JobController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyJobLauncher.class);

  @Autowired
  MyJobLauncher myJobLauncher;

  @Autowired
  JobDetail jobDetail;

  @GetMapping("/request")
  public String requestExecutionForm(Model model) {

    model.addAttribute("executionRequest", new ExecutionRequest());
    return "request";
  }

  @RequestMapping(value = "/response", method = RequestMethod.POST, params = "action=execute")
  // @PostMapping("/submit")
  // public String processJobExecutionRequest(@ModelAttribute ExecutionRequest executionRequest) {
  public String processJobExecutionRequest(@Valid ExecutionRequest executionRequest, BindingResult bindingResult, Model mode) {

    String fileDate = executionRequest.getFileDate().replaceAll("[^0-9]", "");
    String jobName = executionRequest.getJobName();
    String sequencial = executionRequest.getSequencial() + "";
    executionRequest.setMessage(null);

    if (bindingResult.hasErrors()) {
      return "request";
    }

    if (!JobNameIsValid(jobName)) {
      executionRequest.setMessage("JobName não informado");
      return "request";
    }

    if (!FileDateIsValid(fileDate)) {
      executionRequest.setMessage("Data do arquivo não informada");
      return "request";
    }

    ExecutionRequest ere = myJobLauncher.run(jobName, fileDate, sequencial);
    LOGGER.info(ere.getJobStatus());
    executionRequest.setJobStatus(ere.getJobStatus());
    executionRequest.setMessage(ere.getMessage());

    if (ere.getMessage() == null) {
      executionRequest.setMessage("Aguarde conclusão do job!");
      return "request";
    }

    return "response";
  }

  @RequestMapping(value = "/response", method = RequestMethod.POST, params = "action=detail")
  // @PostMapping("/jobDetail")
  public String jobDetail(@Valid ExecutionRequest executionRequest, BindingResult bindingResult, Model mode) {
    // public String jobDetail(@ModelAttribute ExecutionRequest executionRequest) {

    String fileDate = executionRequest.getFileDate().replaceAll("[^0-9]", "");
    String jobName = executionRequest.getJobName();
    String sequencial = executionRequest.getSequencial() + "";
    executionRequest.setMessage(null);

    if (!JobNameIsValid(jobName)) {
      executionRequest.setMessage("JobName não informado");
      return "request";
    }

    if (!FileDateIsValid(fileDate)) {
      executionRequest.setMessage("Data do arquivo não informada");
      return "request";
    }

    ExecutionRequest ere = jobDetail.getJobDetail(jobName, fileDate, sequencial);
    LOGGER.info(ere.getJobStatus());
    executionRequest.setJobStatus(ere.getJobStatus());
    executionRequest.setMessage(ere.getMessage());
    executionRequest.setJobExecutionDetail(ere.getJobExecutionDetail());
    executionRequest.setStepExecutionDetail(ere.getStepExecutionDetail());

    return "response";
  }

  @GetMapping("/startJob")
  public JobExecutionRequest jer(
      @RequestParam(value = "jobName", defaultValue = "creditJob") String jobName,
      @RequestParam(value = "fileDate") String fileDate,
      @RequestParam(value = "sequencial", defaultValue = "0") String sequencial) {

    if (!JobNameIsValid(jobName)) {
      return new JobExecutionRequest("Invalid job name. Informe no formato /startJob?jobName=xxxx&fileDate=YYYYMMdd");
    }

    if (!FileDateIsValid(fileDate)) {
      return new JobExecutionRequest("Invalid date. Informe no formato /startJob?jobName=xxxx&fileDate=YYYYMMdd");
    }

    ExecutionRequest ere = myJobLauncher.run(jobName, fileDate, sequencial);
    LOGGER.info(ere.getJobStatus());
    return new JobExecutionRequest(jobName, fileDate, ere.getJobStatus(), ere.getMessage());
  }

  @GetMapping({ "", "/", "/**", "index", "index.html" })
  // as by default Spring maps unknown urls to "/**"
  public String notFound404() {
    return "redirect:request";
    // return new JobExecutionRequest("Bad request. Informe no formato /startJob?jobName=xxxx&fileDate=YYYYMMdd");
  }

  public Boolean JobNameIsValid(String jobName) {
    return (jobName.equals("creditJob") || jobName.equals("personJob"));
  }

  public Boolean FileDateIsValid(String fileDate) {
    return (GenericValidator.isDate(fileDate, "yyyyMMdd", true));
  }

}
