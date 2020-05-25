/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import java.util.List;
import com.intel.iot.ams.entity.ProductProperty;
import com.intel.iot.ams.repository.ProductPropertyDao;
import com.intel.iot.ams.repository.hibernate.ProductPropertyDaoImpl;

public class ProductPropertyService extends BaseService<Integer, ProductProperty> {

  private ProductPropertyDao productPropertyDao;

  public ProductPropertyService() {
    productPropertyDao = new ProductPropertyDaoImpl();
    super.setDao(productPropertyDao);
  }

  public List<ProductProperty> findByName(String name) {
    return productPropertyDao.findByName(name);
  }

  public void removeByName(String name) {
    productPropertyDao.removeByName(name);
    return;
  }


}
