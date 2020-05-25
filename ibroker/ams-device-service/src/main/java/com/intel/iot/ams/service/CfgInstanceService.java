/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import java.util.List;
import com.intel.iot.ams.entity.CfgInstance;
import com.intel.iot.ams.repository.CfgInstanceDao;
import com.intel.iot.ams.repository.hibernate.CfgInstanceDaoImpl;

public class CfgInstanceService extends BaseService<Integer, CfgInstance> {

  private CfgInstanceDao cfgInstanceDao;

  public CfgInstanceService() {
    cfgInstanceDao = new CfgInstanceDaoImpl();
    super.setDao(cfgInstanceDao);
  }

  public List<CfgInstance> findByCfgIdentifierUUID(String uuid) {
    return cfgInstanceDao.findByCfgIdentifierUUID(uuid);
  }

  public CfgInstance findByCfgIdentifierUUIDAndTargetId(String uuid, String targetId) {
    return cfgInstanceDao.findByCfgIdentifierUUIDAndTargetId(uuid, targetId);
  }
}
