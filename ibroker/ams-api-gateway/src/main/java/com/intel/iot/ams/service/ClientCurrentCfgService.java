/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.ClientCurrentCfg;
import com.intel.iot.ams.repository.ClientCurrentCfgDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClientCurrentCfgService extends BaseService<Integer, ClientCurrentCfg> {

  @Autowired private ClientCurrentCfgDao ClientCurrentCfgDao;

  public List<ClientCurrentCfg> findByClientShortId(int clientId) {
    return ClientCurrentCfgDao.findByClientId(clientId);
  }

  public ClientCurrentCfg findByClientIdAndProductNameAndTargetTypeAndTargetIdAndPathName(
      Integer clientId, String pn, String tt, String ti, String path) {
    return ClientCurrentCfgDao.findByClientIdAndProductNameAndTargetTypeAndTargetIdAndPathName(
        clientId, pn, tt, ti, path);
  }
}
