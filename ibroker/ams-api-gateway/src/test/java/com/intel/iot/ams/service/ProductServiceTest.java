/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.intel.iot.ams.entity.Product;
import com.intel.iot.ams.repository.ProductDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceTest {
  @Autowired private ProductService productService;
  @Autowired private ProductDao productDao;

  private Product productA = null;

  @Before
  public void setUp() throws Exception {
    productA = new Product();
    productA.setUuid("jujube");
    productA.setName("jostaberry");
    productA.setCategory(29);
    productA.setVendor("blackberry");
    productA.setDefaultVersion("1.0");

    if (productDao.findByUuid(productA.getUuid()) == null) {
      productDao.saveAndFlush(productA);
    }
  }

  @After
  public void tearDown() throws Exception {
    productDao.delete(productA);
  }

  @Test
  public void findByUUID() {
    assertThat(productService.findByUUID(productA.getUuid()))
        .isNotNull()
        .hasFieldOrPropertyWithValue("name", productA.getName())
        .hasFieldOrPropertyWithValue("category", productA.getCategory());
  }

  @Test
  public void findByName() {}

  @Test
  public void findByUuidAndName() {}

  @Test
  public void findByVendorAndCategory() {}

  @Test
  public void findAll() {}

  @Test
  public void saveOrUpdate() {}

  @Test
  public void update() {}
}
