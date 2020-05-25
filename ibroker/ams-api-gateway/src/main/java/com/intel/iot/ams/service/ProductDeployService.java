/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.ProductDeploy;
import com.intel.iot.ams.repository.ProductDeployDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductDeployService extends BaseService<Integer, ProductDeploy> {

  @Autowired private ProductDeployDao productSettingDao;

  public List<ProductDeploy> findByClientUUID(String uuid) {
    return productSettingDao.findByClientUuid(uuid);
  }

  public List<ProductDeploy> findByDeviceId(String deviceId) {
    return productSettingDao.findByProductDeviceId(deviceId);
  }

  public List<ProductDeploy> findByProductName(String productName) {
    return productSettingDao.findByProductName(productName);
  }

  public ProductDeploy findByClientUuidAndProductName(String clientUuid, String productName) {
    return productSettingDao.findByClientUuidAndProductName(clientUuid, productName);
  }

  public void removeByClientUUID(String uuid) {
    productSettingDao.removeByClientUuid(uuid);
  }

  public void removeByDeviceId(String deviceId) {
    productSettingDao.removeByProductDeviceId(deviceId);
  }

  public void removeByClientUuidAndProductName(String clientUuid, String productName) {
    productSettingDao.removeByClientUuidAndProductName(clientUuid, productName);
  }

  public void removeByDeviceIdAndProductName(String deviceId, String productName) {
    productSettingDao.removeByProductDeviceIdAndProductName(deviceId, productName);
  }

  public void removeByProductName(String uuid) {
    productSettingDao.removeByProductName(uuid);
  }

  public void removeByProductNameAndVersion(String uuid, String version) {
    productSettingDao.removeByProductNameAndVersion(uuid, version);
  }

  public List<ProductDeploy> findAll(Integer offset, Integer limit) {
    return productSettingDao.findAll(PageRequest.of(offset / limit, limit)).getContent();
  }

  public List<ProductDeploy> findByClientUUID(String uuid, String projectId) {
    return productSettingDao.findByClientUuidAndProjectId(uuid, projectId);
  }

  public List<ProductDeploy> findByProductName(String productName, String projectId) {
    return productSettingDao.findByProductNameAndProjectId(productName, projectId);
  }

  public ProductDeploy findByClientUuidAndProductName(
      String clientUuid, String productName, String projectId) {
    return productSettingDao.findByClientUuidAndProductNameAndProjectId(
        clientUuid, productName, projectId);
  }

  public List<ProductDeploy> findAll(Integer offset, Integer limit, String projectId) {
    return productSettingDao.findByProjectId(PageRequest.of(offset / limit, limit));
  }

  public void save(ProductDeploy deploy) {
    productSettingDao.save(deploy);
  }
}
