package com.marxelo.web;

import com.marxelo.web.JobExecutionRequest;

import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyJobLauncher.class);

  @Autowired
  MyJobLauncher myJobLauncher;

  @GetMapping("/startJob")
  public JobExecutionRequest jer(@RequestParam(value = "jobName", defaultValue = "creditJob") String jobName,
      @RequestParam(value = "fileDate") String fileDate) {
    if ((!jobName.equals("creditJob")) && !jobName.equals("debitJob") && !jobName.equals("personJob")) {
      LOGGER.info(jobName);
      return new JobExecutionRequest(jobName, fileDate, "Invalid job name");
    }
    LOGGER.info(jobName);

    if (!GenericValidator.isDate(fileDate, "yyyyMMdd", true)) {
      LOGGER.info(fileDate);
      return new JobExecutionRequest(jobName, fileDate,
          "Invalid date. Informe no formato /startJob?jobName=xxxx&?fileDate=YYYYMMdd");
    }
    LOGGER.info(fileDate);

    String jobStatus = myJobLauncher.run(fileDate);
    LOGGER.info(jobStatus);
    return new JobExecutionRequest(jobName, fileDate, jobStatus);
  }

  @GetMapping({ "", "/", "/**", "index", "index.html" })
  // as by default Spring maps unknown urls to "/**"
  public String help() {
    LOGGER.info("Informe no formato /startJob?jobName=xxxx&?fileDate=YYYYMMddxx");
    return "Informe no formato /startJob?jobName=xxxx&?fileDate=YYYYMMdd";
  }

}
