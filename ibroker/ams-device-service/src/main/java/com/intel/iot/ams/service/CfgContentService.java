/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.CfgContent;
import com.intel.iot.ams.repository.CfgContentDao;
import com.intel.iot.ams.repository.hibernate.CfgContentDaoImpl;


public class CfgContentService extends BaseService<Integer, CfgContent> {

  private CfgContentDao cfgContentDao;

  public CfgContentService() {
    cfgContentDao = new CfgContentDaoImpl();
    super.setDao(cfgContentDao);
  }

  public CfgContent findBySharedName(String name) {
    return cfgContentDao.findBySharedName(name);
  }

  public CfgContent findByHash(String hash) {
    return cfgContentDao.findByHash(hash);
  }
}
