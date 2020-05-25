/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.Logs;
import com.intel.iot.ams.repository.LogDao;
import com.intel.iot.ams.security.AuthUtils;
import com.openiot.cloud.base.common.model.TokenContent;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LogService extends BaseService {
  private static final Logger logger = Logger.getLogger("LogDB");

  @Autowired private LogDao logDao;

  public List<Logs> QueryLog(
      String user, String action, String clasS, String objectId, String projectId) {
    clasS =
        (clasS.equals("Software") || clasS.equals("Template"))
            ? "%" + clasS + ""
            : "%" + clasS + "%";
    return logDao.QueryLog(user, action, clasS, objectId, projectId);
  }

  public List<Logs> QueryLogWithTimeStart(
      String user, String action, String clasS, String objectId, String projectId, Date timeStart) {
    clasS =
        (clasS.equals("Software") || clasS.equals("Template"))
            ? "%" + clasS + ""
            : "%" + clasS + "%";
    return logDao.QueryLogWithStarTime(user, action, clasS, objectId, projectId, timeStart);
  }

  public List<Logs> QueryLogWithTimEnd(
      String user, String action, String clasS, String objectId, String projectId, Date timend) {
    clasS =
        (clasS.equals("Software") || clasS.equals("Template"))
            ? "%" + clasS + ""
            : "%" + clasS + "%";
    return logDao.QueryLogWithTimEnd(user, action, clasS, objectId, projectId, timend);
  }

  public List<Logs> QueryLogBetweenTime(
      String user,
      String action,
      String clasS,
      String objectId,
      String projectId,
      Date timeStart,
      Date timend) {
    clasS =
        (clasS.equals("Software") || clasS.equals("Template"))
            ? "%" + clasS + ""
            : "%" + clasS + "%";
    return logDao.QueryLogBetweenTime(user, action, clasS, objectId, projectId, timeStart, timend);
  }

  public void LogToMysql(String action, String clasS, String details, String objectId) {
    String projectId = AuthUtils.getProjectIdFromContext();
    if (projectId != null) MDC.put("projectId", projectId);

    TokenContent token = AuthUtils.getTokenContextFromContext();
    if (token != null) {
      String user = token.getUser();
      MDC.put("user", user);
    }

    if (objectId != null) MDC.put("objectId", objectId);

    MDC.put("action", action);
    MDC.put("clasS", clasS);
    MDC.put("details", details);
    logger.warn("Log to MySQL.");
    Map map = MDC.getContext();
    if (map != null) {
      MDC.clear();
    }
  }
}
