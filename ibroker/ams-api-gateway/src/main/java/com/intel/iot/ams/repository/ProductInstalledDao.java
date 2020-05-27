/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ProductInstalled;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductInstalledDao extends JpaRepository<ProductInstalled, Integer> {

  public List<ProductInstalled> findByClientUuid(String uuid);

  public List<ProductInstalled> findByProductName(String productName);

  @Query("SELECT count(p) FROM ProductInstalled p WHERE p.clientUuid = ?1")
  public int getProductNumByClientUuid(String uuid);

  public ProductInstalled findByClientUuidAndProductName(String clientUuid, String productName);

  public void deleteByClientUuid(String clientUuid);

  public List<ProductInstalled> findByClientUuidAndProjectId(String uuid, String projectId);

  public List<ProductInstalled> findByProductNameAndProjectId(String productName, String projectId);

  @Query("SELECT count(p) FROM ProductInstalled p WHERE p.clientUuid = ?1 and p.projectId = ?2")
  public int getProductNumByClientUuid(String uuid, String projectId);

  public ProductInstalled findByClientUuidAndProductNameAndProjectId(
      String clientUuid, String productName, String projectId);
}
