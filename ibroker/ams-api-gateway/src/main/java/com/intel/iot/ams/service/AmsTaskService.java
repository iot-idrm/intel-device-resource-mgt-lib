/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.AmsTask;
import com.intel.iot.ams.repository.AmsTaskDao;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AmsTaskService extends BaseService<Integer, AmsTask> {

  @Autowired private AmsTaskDao amsTaskDao;

  public List<AmsTask> findByTaskPriority(int priority) {
    return amsTaskDao.findByTaskPriority(priority);
  }

  public void removeByTaskProperty(String taskProperties) {
    Optional.ofNullable(amsTaskDao.findByTaskPropertiesContaining(taskProperties))
        .ifPresent(amsTaskDao::deleteAll);
    /**
     * Find Then Delete can achieve the same goal as the following Delete directly function. Choose
     * 1 is fine.
     */
    // amsTaskDao.deleteByTaskProperties(taskProperties);
  }

  public void save(AmsTask task) {
    amsTaskDao.save(task);
  }
}
