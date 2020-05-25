/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import java.util.List;
import com.intel.iot.ams.entity.CfgIdentifier;
import com.intel.iot.ams.repository.CfgIdentifierDao;
import com.intel.iot.ams.repository.hibernate.CfgIdentifierDaoImpl;


public class CfgIdentifierService extends BaseService<Integer, CfgIdentifier> {

  private CfgIdentifierDao cfgIdentifierDao;

  public CfgIdentifierService() {
    cfgIdentifierDao = new CfgIdentifierDaoImpl();
    super.setDao(cfgIdentifierDao);
  }

  public CfgIdentifier findByUUID(String uuid) {

    return cfgIdentifierDao.findByUUID(uuid);
  }

  public List<CfgIdentifier> findByUserNameAndTargetType(String name, String targetType) {

    return cfgIdentifierDao.findByUserNameAndTargetType(name, targetType);
  }
}
