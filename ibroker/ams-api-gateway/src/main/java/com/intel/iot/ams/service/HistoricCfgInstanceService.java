/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.HistoricCfgInstance;
import com.intel.iot.ams.repository.HistoricCfgInstanceDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HistoricCfgInstanceService {

  @Autowired private HistoricCfgInstanceDao historicCfgInstanceDao;

  public List<HistoricCfgInstance> findByCfgIdentifierUUID(String uuid) {
    return historicCfgInstanceDao.findByCfgUuid(uuid);
  }

  public List<HistoricCfgInstance> findByCfgInstanceId(int id) {
    return historicCfgInstanceDao.findByInstanceId(id);
  }
}
