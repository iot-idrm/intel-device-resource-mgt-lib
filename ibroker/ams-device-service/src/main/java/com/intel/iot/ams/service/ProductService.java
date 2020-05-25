/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.Product;
import com.intel.iot.ams.repository.ProductDao;
import com.intel.iot.ams.repository.hibernate.ProductDaoImpl;

public class ProductService extends BaseService<Integer, Product> {

  private ProductDao productDao;

  public ProductService() {
    productDao = new ProductDaoImpl();
    super.setDao(productDao);
  }

  public Product findByUUID(String uuid) {

    return productDao.findByUUID(uuid);
  }

  public void removeByUUID(String uuid) {

    productDao.removeByUUID(uuid);
    return;
  }

  public Product findByName(String name) {
    return productDao.findByName(name);
  }

}
