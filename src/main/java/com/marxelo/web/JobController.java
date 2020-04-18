package com.marxelo.web;

import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JobController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyJobLauncher.class);

  @Autowired
  MyJobLauncher myJobLauncher;

  @GetMapping("/request-job")
  public String requestExecutionForm(Model model) {

    model.addAttribute("executionRequest", new ExecutionRequest());
    return "request-job";
  }

  @PostMapping("/start-job")
  public String saveProjectSubmission(@ModelAttribute ExecutionRequest executionRequest) {

    String fileDate = executionRequest.getFileDate().replaceAll("[^0-9]", "");
    String jobName = executionRequest.getJobName();
    String sequencial = executionRequest.getSequencial() + "";

    ExecutionRequest ere = myJobLauncher.run(jobName, fileDate, sequencial);
    LOGGER.info(ere.getJobStatus());
    executionRequest.setJobStatus(ere.getJobStatus());
    executionRequest.setMessage(ere.getMessage());

    return "result";
  }

  @GetMapping("/startJob")
  public JobExecutionRequest jer(
      @RequestParam(value = "jobName", defaultValue = "creditJob") String jobName,
      @RequestParam(value = "fileDate") String fileDate,
      @RequestParam(value = "time", defaultValue = "000000") String sequencial) {

    if ((!jobName.equals("creditJob")) && !jobName.equals("debitJob") && !jobName.equals("personJob")) {
      LOGGER.info(jobName);
      return new JobExecutionRequest("Invalid job name. Informe no formato /startJob?jobName=xxxx&fileDate=YYYYMMdd");
    }
    LOGGER.info(jobName);

    if (!GenericValidator.isDate(fileDate, "yyyyMMdd", true)) {
      LOGGER.info(fileDate);
      return new JobExecutionRequest("Invalid date. Informe no formato /startJob?jobName=xxxx&fileDate=YYYYMMdd");
    }
    LOGGER.info(fileDate);

    ExecutionRequest ere = myJobLauncher.run(jobName, fileDate, sequencial);
    LOGGER.info(ere.getJobStatus());
    return new JobExecutionRequest(jobName, fileDate, ere.getJobStatus(), ere.getMessage());
  }

  @GetMapping({ "", "/", "/**", "index", "index.html" })
  // as by default Spring maps unknown urls to "/**"
  public JobExecutionRequest notFound404() {

    return new JobExecutionRequest("Bad request. Informe no formato /startJob?jobName=xxxx&fileDate=YYYYMMdd");
  }

}
