/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.AmsTemplate;
import com.intel.iot.ams.repository.AmsTemplateDao;
import com.intel.iot.ams.repository.hibernate.AmsTemplateDaoImpl;

public class AmsTemplateService extends BaseService<Integer, AmsTemplate> {

  private AmsTemplateDao amsTemplateDao;

  public AmsTemplateService() {
    amsTemplateDao = new AmsTemplateDaoImpl();
    super.setDao(amsTemplateDao);
  }

  public AmsTemplate findByName(String name) {
    return amsTemplateDao.findByName(name);
  }

  public void removeByName(String name) {
    amsTemplateDao.removeByName(name);
    return;
  }
}
