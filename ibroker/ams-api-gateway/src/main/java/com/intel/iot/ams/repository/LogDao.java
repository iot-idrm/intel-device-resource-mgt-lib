/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.Logs;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LogDao extends JpaRepository<Logs, Integer> {

  @Query(
      "SELECT log FROM Logs log WHERE log.user LIKE %?1% AND log.action LIKE %?2% "
          + "AND log.clasS LIKE ?3 AND log.objectId LIKE %?4% AND log.projectId LIKE %?5%")
  public List<Logs> QueryLog(
      String user, String action, String clasS, String objectId, String projectId);

  @Query(
      "SELECT log FROM Logs log WHERE log.user LIKE %?1% AND log.action LIKE %?2% "
          + "AND log.clasS LIKE ?3 AND log.objectId LIKE %?4% AND log.projectId LIKE %?5% "
          + "AND log.time > ?6")
  public List<Logs> QueryLogWithStarTime(
      String user, String action, String clasS, String objectId, String projectId, Date timestart);

  @Query(
      "SELECT log FROM Logs log WHERE log.user LIKE %?1% AND log.action LIKE %?2% "
          + "AND log.clasS LIKE ?3 AND log.objectId LIKE %?4% AND log.projectId LIKE %?5% "
          + "AND log.time < ?6")
  public List<Logs> QueryLogWithTimEnd(
      String user, String action, String clasS, String objectId, String projectId, Date timend);

  @Query(
      "SELECT log FROM Logs log WHERE log.user LIKE %?1% AND log.action LIKE %?2% "
          + "AND log.clasS LIKE ?3 AND log.objectId LIKE %?4% AND log.projectId LIKE %?5% "
          + "AND log.time > ?6 AND log.time < ?7")
  public List<Logs> QueryLogBetweenTime(
      String user,
      String action,
      String clasS,
      String objectId,
      String projectId,
      Date timestart,
      Date timend);
}
