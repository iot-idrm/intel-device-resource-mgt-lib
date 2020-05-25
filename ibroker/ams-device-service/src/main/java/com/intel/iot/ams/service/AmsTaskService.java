/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import java.util.List;
import com.intel.iot.ams.entity.AmsTask;
import com.intel.iot.ams.repository.AmsTaskDao;
import com.intel.iot.ams.repository.hibernate.AmsTaskDaoImpl;

public class AmsTaskService extends BaseService<Integer, AmsTask> {

  private AmsTaskDao amsTaskDao;

  public AmsTaskService() {
    amsTaskDao = new AmsTaskDaoImpl();
    super.setDao(amsTaskDao);
  }

  public List<AmsTask> findByTaskPriority(int priority) {
    return amsTaskDao.findByTaskPriority(priority);
  }

  public AmsTask getTopTask() {
    return amsTaskDao.getTopTask();
  }
}
