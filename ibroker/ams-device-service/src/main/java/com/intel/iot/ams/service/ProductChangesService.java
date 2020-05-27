/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import java.util.Date;
import java.util.List;
import com.intel.iot.ams.entity.ProductChanges;
import com.intel.iot.ams.repository.ProductChangesDao;
import com.intel.iot.ams.repository.hibernate.ProductChangesDaoImpl;


public class ProductChangesService extends BaseService<Integer, ProductChanges> {

  private ProductChangesDao productChangesDao;


  public ProductChangesService() {
    productChangesDao = new ProductChangesDaoImpl();
    super.setDao(productChangesDao);
  }

  public List<ProductChanges> findByClientUUID(String uuid) {
    return productChangesDao.findByClientUUID(uuid);
  }

  public void removeByClientUuid(String clientUuid) {
    productChangesDao.removeByClientUuid(clientUuid);
  }

  public void removeByClientUuidAndProductName(String clientUuid, String productName) {
    productChangesDao.removeByClientUuidAndProductName(clientUuid, productName);
  }

  public Date getMaxEnableTime() {
    return productChangesDao.getMaxEnableTime();
  }

  public int getEnableTimeCount(Date time) {
    return productChangesDao.getEnableTimeCount(time);
  }

}
