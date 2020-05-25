/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intel.iot.ams.entity.Logs;
import com.intel.iot.ams.security.AuthUtils;
import com.intel.iot.ams.service.LogService;
import com.openiot.cloud.base.common.model.TokenContent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ams_user_cloud")
public class LogsAPIs {
  @Autowired private LogService logServ;

  @RequestMapping(value = "/ams/v1/logs", produces = "application/json", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<String> getLogs(
      @RequestParam(value = "action", required = false) String action,
      @RequestParam(value = "clasS", required = false) String clasS,
      @RequestParam(value = "objectId", required = false) String objectId,
      @RequestParam(value = "timeStart", required = false) String timeStart,
      @RequestParam(value = "timEnd", required = false) String timEnd)
      throws ParseException {

    action = action == null ? "" : action;
    clasS = clasS == null ? "" : clasS;
    objectId = objectId == null ? "" : objectId;

    String projectId = AuthUtils.getProjectIdFromContext();
    projectId = projectId == null ? "" : projectId;
    TokenContent token = AuthUtils.getTokenContextFromContext();
    String user = token == null ? "" : token.getUser();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    List<Logs> logList = null;
    if (timeStart == null & timEnd == null) {
      logList = logServ.QueryLog(user, action, clasS, objectId, projectId);
    } else if (timEnd == null) { // find with start time
      Date timestart = simpleDateFormat.parse(timeStart);
      logList = logServ.QueryLogWithTimeStart(user, action, clasS, objectId, projectId, timestart);
    } else if (timeStart == null) { // find with end time
      Date timend = simpleDateFormat.parse(timEnd);
      logList = logServ.QueryLogWithTimEnd(user, action, clasS, objectId, projectId, timend);
    } else { // find between time period
      Date timestart = simpleDateFormat.parse(timeStart);
      Date timend = simpleDateFormat.parse(timEnd);
      logList =
          logServ.QueryLogBetweenTime(user, action, clasS, objectId, projectId, timestart, timend);
    }

    JsonArray logArr = new JsonArray();
    for (Logs log : logList) {
      JsonObject logObj = new JsonObject();
      logObj.addProperty("user", log.getUser());
      logObj.addProperty("action", log.getAction());
      logObj.addProperty("clasS", log.getClasS());
      logObj.addProperty("objectId", log.getObjectId());
      logObj.addProperty("details", log.getDetails());
      logObj.addProperty("projectId", log.getProjectId());
      logObj.addProperty("time", log.getTime().toString());
      logArr.add(logObj);
    }
    return new ResponseEntity<String>(logArr.toString(), HttpStatus.OK);
  }
}
