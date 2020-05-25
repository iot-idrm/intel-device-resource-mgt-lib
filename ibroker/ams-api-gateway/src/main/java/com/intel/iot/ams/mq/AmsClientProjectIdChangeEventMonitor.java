/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.mq;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.intel.iot.ams.entity.AmsClient;
import com.intel.iot.ams.entity.AmsTask;
import com.intel.iot.ams.service.AmsClientService;
import com.intel.iot.ams.service.AmsTaskService;
import com.intel.iot.ams.service.ProductChangesService;
import com.intel.iot.ams.task.AmsTaskType;
import com.openiot.cloud.base.mongo.model.EventMonitor;
import com.openiot.cloud.base.mongo.model.TaskNew;
import com.openiot.cloud.sdk.service.IConnectRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class AmsClientProjectIdChangeEventMonitor {

  private static final Logger logger =
      LoggerFactory.getLogger(AmsClientProjectIdChangeEventMonitor.class);

  @Autowired private AmsClientService clientSrv;

  @Autowired private AmsTaskService taskSrv;

  @Autowired private ProductChangesService changeSrv;

  public void monitorReg() {
    // register event monitor
    EventMonitor monitor = new EventMonitor();
    monitor.setName("amsClientProjectIdChangeMonitor");

    try {
      IConnectRequest request =
          IConnectRequest.create(
              HttpMethod.POST,
              "/event-monitor",
              MediaType.APPLICATION_JSON,
              new ObjectMapper().writeValueAsBytes(monitor));
      request.send(
          (response) -> {
            HttpStatus status = response.getStatus();
            if (status == HttpStatus.OK) {
              logger.info("amsClientProjectIdChangeMonitor register successfully!");
            } else {
              logger.error("amsClientProjectIdChangeMonitor register failed!");
            }
          },
          10,
          TimeUnit.SECONDS);

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void handleTask() {

    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                IConnectRequest request =
                    IConnectRequest.create(
                        HttpMethod.GET,
                        "/task?monitor=amsClientProjectIdChangeMonitor",
                        null,
                        null);
                request.send(
                    (response) -> {
                      HttpStatus status = response.getStatus();
                      if (status == HttpStatus.OK) {
                        TaskNew task = null;
                        try {
                          task = new ObjectMapper().readValue(response.getPayload(), TaskNew.class);
                          if (task != null) {
                            String clientId = task.getTargetId();
                            String projectId = null;
                            if (task.getData() != null) {
                              projectId = new String(task.getData());
                            }
                            AmsClient client = clientSrv.findByClientUUID(clientId);
                            if (client != null) {
                              client.setProjectId(projectId);
                              clientSrv.update(client);

                              /**
                               * Raised when the client is moved into or out from the project. if
                               * the project has bound with template, this client's products status
                               * will update
                               */
                              /** Remove old product changes of this client */
                              changeSrv.removeByClientUuid(client.getClientUuid());
                              /** Create new task for this client to calculate product change */
                              AmsTask amsTask = new AmsTask();
                              amsTask.setTaskType(AmsTaskType.CALCULATE_PRODUCT_CHANGES);
                              amsTask.setTaskCreateTime(new Date());
                              JsonObject jTaskProperty = new JsonObject();
                              jTaskProperty.addProperty("client_uuid", client.getClientUuid());
                              amsTask.setTaskProperties(jTaskProperty.toString());
                              taskSrv.save(amsTask);
                            }
                            // Delete task after handling it
                            IConnectRequest delReq =
                                IConnectRequest.create(
                                    HttpMethod.DELETE, "/task?id=" + task.getId(), null, null);
                            delReq.send(null, 10, TimeUnit.SECONDS);
                          }
                        } catch (JsonParseException e) {
                          e.printStackTrace();
                        } catch (JsonMappingException e) {
                          e.printStackTrace();
                        } catch (IOException e) {
                          e.printStackTrace();
                        }
                      } else if (status == HttpStatus.NOT_FOUND) {
                        logger.info("no task to handle");
                      } else {
                        logger.error("Get Task failed, errono: " + status);
                      }
                    },
                    10,
                    TimeUnit.SECONDS);
              }
            },
            0,
            1000 * 20);
  }
}
