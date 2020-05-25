/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import java.util.List;
import com.intel.iot.ams.entity.ClientCurrentCfg;
import com.intel.iot.ams.repository.ClientCurrentCfgDao;
import com.intel.iot.ams.repository.hibernate.ClientCurrentCfgDaoImpl;

public class ClientCurrentCfgService extends BaseService<Integer, ClientCurrentCfg> {

  private ClientCurrentCfgDao dao;

  public ClientCurrentCfgService() {
    dao = new ClientCurrentCfgDaoImpl();
    super.setDao(dao);
  }

  public List<ClientCurrentCfg> findByClientId(Integer clientId) {
    return dao.findByClientId(clientId);
  }

  public void removeByClientId(Integer clientId) {
    dao.removeByClientId(clientId);
  }

  public ClientCurrentCfg
      findByClientIdAndProductNameAndTargetTypeAndTargetIdAndPathName(Integer clientId, String pn,
                                                                      String tt, String ti,
                                                                      String path) {
    return dao.findByClientIdAndProductNameAndTargetTypeAndTargetIdAndPathName(clientId,
                                                                               pn,
                                                                               tt,
                                                                               ti,
                                                                               path);
  }

}
