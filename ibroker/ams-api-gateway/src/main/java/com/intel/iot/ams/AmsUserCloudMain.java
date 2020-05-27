/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams;

import com.intel.iot.ams.mq.AmsClientProjectIdChangeEventMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@EnableCaching
@SpringBootApplication(
    exclude = {ErrorMvcAutoConfiguration.class},
    scanBasePackages = {
      "com.intel.iot.ams",
      "com.openiot.cloud.sdk",
      "com.openiot.cloud.base.mongo",
      "com.openiot.cloud.base.influx"
    })
public class AmsUserCloudMain extends SpringBootServletInitializer {
  private static final Logger logger = LoggerFactory.getLogger(AmsUserCloudMain.class);

  @Autowired AmsClientProjectIdChangeEventMonitor monitor;

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(AmsUserCloudMain.class);
  }

  @EventListener
  public void onApplicationReady(final ApplicationReadyEvent event) {
    logger.info("\n===================AMS user cloud startup ready==========================\n");

    monitor.monitorReg();
    monitor.handleTask();
  }

  public static void main(String[] args) {
    SpringApplication.run(AmsUserCloudMain.class, args);
  }
}
