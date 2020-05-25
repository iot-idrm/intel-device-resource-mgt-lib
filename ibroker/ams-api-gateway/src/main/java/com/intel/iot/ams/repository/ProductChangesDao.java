/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ProductChanges;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductChangesDao extends JpaRepository<ProductChanges, Integer> {

  public List<ProductChanges> findByClientUuid(String uuid);

  public ProductChanges findByClientUuidAndProductName(String clientUuid, String productName);

  public void removeByClientUuid(String clientUuid);

  public void removeByClientUuidAndProductName(String clientUuid, String productName);
}
