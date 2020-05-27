/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import java.util.List;
import com.intel.iot.ams.entity.ClientCfgCheckPoint;
import com.intel.iot.ams.repository.ClientCfgCheckPointDao;
import com.intel.iot.ams.repository.hibernate.ClientCfgCheckPointDaoImpl;

public class ClientcfgCheckPointService extends BaseService<Integer, ClientCfgCheckPoint> {

  private ClientCfgCheckPointDao dao;

  public ClientcfgCheckPointService() {
    dao = new ClientCfgCheckPointDaoImpl();
    super.setDao(dao);
  }

  public List<ClientCfgCheckPoint> findByClientId(Integer clientId) {
    return dao.findByClientId(clientId);
  }

  public void removeByClientId(Integer clientId) {
    dao.removeByClientId(clientId);
  }

}
