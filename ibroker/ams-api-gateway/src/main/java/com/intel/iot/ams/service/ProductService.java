/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.Product;
import com.intel.iot.ams.repository.ProductDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService extends BaseService<Integer, Product> {

  @Autowired private ProductDao productDao;

  public Product findByUUID(String uuid) {
    return productDao.findByUuid(uuid);
  }

  public Product findByName(String name) {
    return productDao.findByName(name);
  }

  public Product findByUuidAndName(String uuid, String name) {
    return productDao.findByUuidAndName(uuid, name);
  }

  public void removeByUUID(String uuid) {
    productDao.removeByUuid(uuid);
    return;
  }

  public void removeByName(String name) {
    productDao.removeByName(name);
    return;
  }

  public List<Product> findByVendor(String vendor) {
    return productDao.findByVendor(vendor);
  }

  public List<Product> findByCategory(Integer category) {

    return productDao.findByCategory(category);
  }

  public List<Product> findByVendorAndCategory(String vendor, Integer category) {

    return productDao.findByVendorAndCategory(vendor, category);
  }

  public List<Product> findAll() {
    return productDao.findAll();
  }

  public void saveOrUpdate(Product p) {
    productDao.save(p);
  }

  public void update(Product p) {
    productDao.save(p);
  }
}
