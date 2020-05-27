/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.ProductProperty;
import com.intel.iot.ams.repository.ProductPropertyDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductPropertyService extends BaseService<Integer, ProductProperty> {

  @Autowired private ProductPropertyDao productPropertyDao;

  public List<ProductProperty> findByName(String name) {
    return productPropertyDao.findByProductName(name);
  }

  public void removeByName(String name) {

    productPropertyDao.removeByProductName(name);
    return;
  }

  public void save(ProductProperty property) {
    productPropertyDao.save(property);
  }
}
