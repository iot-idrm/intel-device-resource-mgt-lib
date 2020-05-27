/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ProductDeploy;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDeployDao extends JpaRepository<ProductDeploy, Integer> {

  public List<ProductDeploy> findByClientUuid(String uuid);

  public List<ProductDeploy> findByProductDeviceId(String deviceId);

  public List<ProductDeploy> findByProductName(String productName);

  public ProductDeploy findByClientUuidAndProductName(String clientUuid, String productName);

  public void removeByClientUuid(String uuid);

  public void removeByProductDeviceId(String deviceId);

  public void removeByClientUuidAndProductName(String clientUuid, String productName);

  public void removeByProductDeviceIdAndProductName(String deviceId, String productName);

  public void removeByProductName(String uuid);

  public void removeByProductNameAndVersion(String uuid, String version);

  public List<ProductDeploy> findByClientUuidAndProjectId(String uuid, String projectId);

  public List<ProductDeploy> findByProductNameAndProjectId(String productName, String projectId);

  public ProductDeploy findByClientUuidAndProductNameAndProjectId(
      String clientUuid, String productName, String projectId);

  public List<ProductDeploy> findByProjectId(Pageable pageable);
}
