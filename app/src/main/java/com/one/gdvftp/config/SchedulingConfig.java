package com.one.gdvftp.config;

import com.one.gdvftp.service.ContractService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Log4j2
public class SchedulingConfig {

  @Autowired
  private ContractService service;

  @Scheduled(cron = "${cron.expression}")
  public void scheduleFixedDelayTask() {
    log.info("scheduled task for writeZentralrufRecords");
    service.writeZentralrufRecords("test/", 10);
  }

}
