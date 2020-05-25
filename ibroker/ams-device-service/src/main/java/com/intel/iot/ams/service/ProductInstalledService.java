/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import java.util.List;
import com.intel.iot.ams.entity.ProductInstalled;
import com.intel.iot.ams.repository.ProductInstalledDao;
import com.intel.iot.ams.repository.hibernate.ProductInstalledDaoImpl;


public class ProductInstalledService extends BaseService<Integer, ProductInstalled> {

  private ProductInstalledDao productInstalledDao;

  public ProductInstalledService() {
    productInstalledDao = new ProductInstalledDaoImpl();
    super.setDao(productInstalledDao);
  }


  public List<ProductInstalled> findByClientUUID(String uuid) {
    return productInstalledDao.findByClientUUID(uuid);
  }

  public ProductInstalled findByClientUuidAndProductName(String clientUuid, String productName) {

    return productInstalledDao.findByClientUuidAndProductName(clientUuid, productName);
  }

  public void removeByClientUuid(String clientUuid) {
    productInstalledDao.removeByClientUuid(clientUuid);
  }
}
