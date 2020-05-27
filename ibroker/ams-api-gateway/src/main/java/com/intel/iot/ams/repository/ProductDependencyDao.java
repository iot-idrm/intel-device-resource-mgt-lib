/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ProductDependency;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDependencyDao extends JpaRepository<ProductDependency, Integer> {

  public List<ProductDependency> findByInstanceId(Integer instanceId);

  public List<ProductDependency> findByDependencyName(String name);
}
