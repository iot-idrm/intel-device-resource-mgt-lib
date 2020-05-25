/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.ClientCfgCheckPoint;
import com.intel.iot.ams.repository.ClientCfgCheckPointDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClientcfgCheckPointService extends BaseService<Integer, ClientCfgCheckPoint> {

  @Autowired private ClientCfgCheckPointDao ClientCfgCheckPointDao;

  public List<ClientCfgCheckPoint> findByClientId(Integer clientId) {
    return ClientCfgCheckPointDao.findByClientId(clientId);
  }

  public void removeByClientId(Integer clientId) {
    ClientCfgCheckPointDao.removeByClientId(clientId);
  }
}
