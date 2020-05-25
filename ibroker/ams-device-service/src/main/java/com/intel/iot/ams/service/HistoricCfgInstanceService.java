/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import java.util.List;
import com.intel.iot.ams.entity.HistoricCfgInstance;
import com.intel.iot.ams.repository.HistoricCfgInstanceDao;
import com.intel.iot.ams.repository.hibernate.HistoricCfgInstanceDaoImpl;

public class HistoricCfgInstanceService extends BaseService<Integer, HistoricCfgInstance> {

  private HistoricCfgInstanceDao historicCfgInstanceDao;

  public HistoricCfgInstanceService() {
    historicCfgInstanceDao = new HistoricCfgInstanceDaoImpl();
    super.setDao(historicCfgInstanceDao);
  }

  public List<HistoricCfgInstance> findByCfgIdentifierUUID(String uuid) {
    return historicCfgInstanceDao.findByCfgIdentifierUUID(uuid);
  }

  public List<HistoricCfgInstance> findByCfgInstanceId(int id) {
    return historicCfgInstanceDao.findByCfgInstanceId(id);
  }
}
