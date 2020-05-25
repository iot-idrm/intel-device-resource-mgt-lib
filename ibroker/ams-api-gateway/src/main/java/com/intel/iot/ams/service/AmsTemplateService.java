/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.AmsTemplate;
import com.intel.iot.ams.repository.AmsTemplateDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AmsTemplateService extends BaseService<Integer, AmsTemplate> {

  @Autowired private AmsTemplateDao amsTemplateDao;

  public AmsTemplate findByName(String name) {
    return amsTemplateDao.findByName(name);
  }

  public void removeByName(String name) {
    amsTemplateDao.removeByName(name);
    return;
  }

  public List<AmsTemplate> findAll() {
    return amsTemplateDao.findAll();
  }

  public void save(AmsTemplate t) {
    amsTemplateDao.save(t);
  }

  public void update(AmsTemplate t) {
    amsTemplateDao.save(t);
  }
}
