/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.AmsTask;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AmsTaskDao extends JpaRepository<AmsTask, Integer> {

  public List<AmsTask> findByTaskPriority(int priority);

  public AmsTask findByTaskProperties(String taskProperties);

  /**
   * cause TaskProperties's format is JsonString, so use Contain (as well as like) here is more
   * secure { "client_uuid"："xxxx" } input parameter taskProperties is the value of clientUuid, that
   * is xxxx.
   */
  public List<AmsTask> findByTaskPropertiesContaining(String taskProperties);

  /**
   * Delete AmsTask records directly. Much more efficient than FIND THEN DELETE specific : Directly
   * - 15ms delete 5000 records. Find & Delete - 393ms delete 5000 records.
   */
  @Transactional
  @Modifying
  @Query("DELETE FROM AmsTask task WHERE task.taskProperties LIKE %?1%")
  public void deleteByTaskProperties(String taskProperties);
}
