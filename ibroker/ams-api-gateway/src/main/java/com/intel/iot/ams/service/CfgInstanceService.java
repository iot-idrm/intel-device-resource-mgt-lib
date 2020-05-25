/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.CfgInstance;
import com.intel.iot.ams.repository.CfgInstanceDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CfgInstanceService extends BaseService<Integer, CfgInstance> {

  @Autowired private CfgInstanceDao cfgInstanceDao;

  public List<CfgInstance> findByCfgIdentifierUUID(String uuid) {
    return cfgInstanceDao.findByCfgUuid(uuid);
  }

  public CfgInstance findByCfgIdentifierUUIDAndTargetId(String uuid, String targetId) {
    return cfgInstanceDao.findByCfgUuidAndTargetId(uuid, targetId);
  }

  public void save(CfgInstance instance) {
    cfgInstanceDao.save(instance);
  }

  public void update(CfgInstance instance) {
    cfgInstanceDao.save(instance);
  }

  public void delete(CfgInstance instance) {
    cfgInstanceDao.delete(instance);
  }
}
