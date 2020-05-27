/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDao extends JpaRepository<Product, Integer> {

  public Product findByUuid(String uuid);

  public Product findByName(String name);

  public Product findByUuidAndName(String uuid, String name);

  public List<Product> findByVendor(String vendor);

  public List<Product> findByCategory(Integer category);

  public List<Product> findByVendorAndCategory(String vendor, Integer category);

  public void removeByUuid(String uuid);

  public void removeByName(String name);
}
