/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.ProductChanges;
import com.intel.iot.ams.repository.ProductChangesDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductChangesService extends BaseService<Integer, ProductChanges> {

  @Autowired private ProductChangesDao productChangesDao;

  public List<ProductChanges> findByClientUUID(String uuid) {
    return productChangesDao.findByClientUuid(uuid);
  }

  public ProductChanges findByClientUuidAndProductName(String clientUuid, String productName) {
    return productChangesDao.findByClientUuidAndProductName(clientUuid, productName);
  }

  public void removeByClientUuid(String clientUuid) {
    productChangesDao.removeByClientUuid(clientUuid);
  }

  public void removeByClientUuidAndProductName(String clientUuid, String productName) {
    productChangesDao.removeByClientUuidAndProductName(clientUuid, productName);
  }

  public List<ProductChanges> findAll() {
    return productChangesDao.findAll();
  }
}
