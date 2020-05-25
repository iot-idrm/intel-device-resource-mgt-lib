/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.ProductInstalled;
import com.intel.iot.ams.repository.ProductInstalledDao;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductInstalledService extends BaseService<Integer, ProductInstalled> {

  @Autowired private ProductInstalledDao productInstalledDao;

  public List<ProductInstalled> findByClientUUID(String uuid) {
    return productInstalledDao.findByClientUuid(uuid);
  }

  public List<ProductInstalled> findByProductName(String productName) {
    return productInstalledDao.findByProductName(productName);
  }

  public int getProductNumByClientUuid(String uuid) {
    return Optional.ofNullable(uuid)
        .map(clientUuid -> productInstalledDao.getProductNumByClientUuid(clientUuid))
        .orElse(0);
  }

  public ProductInstalled findByClientUuidAndProductName(String clientUuid, String productName) {
    return productInstalledDao.findByClientUuidAndProductName(clientUuid, productName);
  }

  public void deleteByClientUuid(String clientUuid) {
    productInstalledDao.deleteByClientUuid(clientUuid);
  }

  public List<ProductInstalled> findByClientUUID(String uuid, String projectId) {
    return productInstalledDao.findByClientUuidAndProjectId(uuid, projectId);
  }

  public List<ProductInstalled> findByProductName(String productName, String projectId) {
    return productInstalledDao.findByProductNameAndProjectId(productName, projectId);
  }

  public int getProductNumByClientUuid(String uuid, String projectId) {
    return productInstalledDao.getProductNumByClientUuid(uuid, projectId);
  }

  public ProductInstalled findByClientUuidAndProductName(
      String clientUuid, String productName, String projectId) {
    return productInstalledDao.findByClientUuidAndProductNameAndProjectId(
        clientUuid, productName, projectId);
  }
}
