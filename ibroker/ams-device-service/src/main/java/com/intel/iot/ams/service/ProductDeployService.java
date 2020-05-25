/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import java.util.List;
import com.intel.iot.ams.entity.ProductDeploy;
import com.intel.iot.ams.repository.ProductDeployDao;
import com.intel.iot.ams.repository.hibernate.ProductDeployDaoImpl;

public class ProductDeployService extends BaseService<Integer, ProductDeploy> {

  private ProductDeployDao productSettingDao;

  public ProductDeployService() {
    productSettingDao = new ProductDeployDaoImpl();
    super.setDao(productSettingDao);
  }

  public List<ProductDeploy> findByClientUUID(String uuid) {
    return productSettingDao.findByClientUUID(uuid);
  }

  public List<ProductDeploy> findByDeviceId(String deviceId) {
    return productSettingDao.findByDeviceId(deviceId);
  }

  public ProductDeploy findByClientUuidAndProductName(String clientUuid, String productName) {
    return productSettingDao.findByClientUuidAndProductName(clientUuid, productName);
  }
}
