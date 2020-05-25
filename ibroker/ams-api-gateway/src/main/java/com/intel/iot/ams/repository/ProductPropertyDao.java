/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ProductProperty;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPropertyDao extends JpaRepository<ProductProperty, Integer> {

  public List<ProductProperty> findByProductName(String name);

  public void removeByProductName(String name);
}
