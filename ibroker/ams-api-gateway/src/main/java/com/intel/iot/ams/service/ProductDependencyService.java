/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.ProductDependency;
import com.intel.iot.ams.repository.ProductDependencyDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductDependencyService extends BaseService<Integer, ProductDependency> {

  @Autowired private ProductDependencyDao productDependencyDao;

  public List<ProductDependency> findByInstanceId(Integer instanceId) {
    return productDependencyDao.findByInstanceId(instanceId);
  }

  public List<ProductDependency> findByDependName(String name) {
    return productDependencyDao.findByDependencyName(name);
  }

  public void save(ProductDependency pd) {
    productDependencyDao.save(pd);
  }
}
