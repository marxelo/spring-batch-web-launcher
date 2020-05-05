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
import org.springframework.web.bind.annotation.ResponseBody;

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

  @RequestMapping(value = "/job-manager", method = RequestMethod.POST)
  public String handleActionRequest(@Valid CustomJobExecution customJobExecution, BindingResult bindingResult, Model mode,
      @RequestParam("action") String action) {

    String fileDate = customJobExecution.getFileDate().replaceAll("[^0-9]", "");
    String jobName = customJobExecution.getJobName();
    String identifier = customJobExecution.getIdentifier() + "";
    customJobExecution.setMessage(null);

    if (bindingResult.hasErrors()) {
      return "request";
    }

    if (!isValidJobName(jobName)) {
      customJobExecution.setMessage("JobName não informado");
      return "request";
    }

    if (!isValidFileDate(fileDate)) {
      customJobExecution.setMessage("Data do arquivo não informada");
      return "request";
    }

    if (!isValidAction(action)) {
      customJobExecution.setMessage("Ação '" + action + "' não prevista");
      return "request";
    }

    CustomJobExecution cje = new CustomJobExecution();

    if (action.equals("submit")) {
      cje = myJobLauncher.run(jobName, fileDate, identifier);
    } else {
      cje = jobDetail.getJobDetail(jobName, fileDate, identifier);
    }

    LOGGER.info(cje.getJobStatus());
    customJobExecution.setJobStatus(cje.getJobStatus());
    customJobExecution.setMessage(cje.getMessage());
    customJobExecution.setJobExecutionDetail(cje.getJobExecutionDetail());
    customJobExecution.setStepExecutionDetail(cje.getStepExecutionDetail());

    if (cje.getMessage() == null && action.equals("submit")) {
      customJobExecution.setMessage("Aguarde conclusão do job!");
      return "request";
    }

    return "response";
  }

  @ResponseBody
  @GetMapping("/startJob")
  public CustomJobExecution jer(
      @RequestParam(value = "jobName", defaultValue = "creditJob") String jobName,
      @RequestParam(value = "fileDate") String fileDate,
      @RequestParam(value = "identifier", defaultValue = "0") String identifier) {

    if (!isValidJobName(jobName)) {
      return new CustomJobExecution("Invalid job name. Informe no formato /startJob?jobName=xxxx&fileDate=YYYYMMdd");
    }

    if (!isValidFileDate(fileDate)) {
      return new CustomJobExecution("Invalid date. Informe no formato /startJob?jobName=xxxx&fileDate=YYYYMMdd");
    }

    CustomJobExecution ere = myJobLauncher.run(jobName, fileDate, identifier);
    LOGGER.info(ere.getJobStatus());
    return new CustomJobExecution(jobName, fileDate, ere.getJobStatus(), ere.getMessage());
  }

  @GetMapping({ "/**"})
  // as by default Spring maps unknown urls to "/**"
  public String notFound404() {
    return "redirect:job-manager";
    // return new CustomJobExecution("Bad request. Informe no formato /startJob?jobName=xxxx&fileDate=YYYYMMdd");
  }

  public Boolean isValidJobName(String jobName) {
    return (jobName.equals("creditJob") || jobName.equals("personJob"));
  }

  public Boolean isValidFileDate(String fileDate) {
    return (GenericValidator.isDate(fileDate, "yyyyMMdd", true));
  }

  public Boolean isValidAction(String action) {
    return (action.equals("submit") || action.equals("detail"));
  }
}
