/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.ProductInstance;
import com.intel.iot.ams.repository.ProductInstanceDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductInstanceService extends BaseService<Integer, ProductInstance> {

  @Autowired private ProductInstanceDao productInstanceDao;

  public List<ProductInstance> findByName(String name) {

    return productInstanceDao.findByProductName(name);
  }

  public List<ProductInstance> findByNameAndVersion(String name, String version) {
    return productInstanceDao.findByProductNameAndVersion(name, version);
  }

  public ProductInstance findByNameAndVersionAndCpuAndPlatformAndOs(
      String name, String version, String cpu, String platform, String os) {
    return productInstanceDao.findByProductNameAndVersionAndCpuAndPlatformAndOs(
        name, version, cpu, platform, os);
  }

  public List<ProductInstance> findByNameAndVersionAndCpuAndPlatformAndOsAndSystemAndBits(
      String name,
      String version,
      String cpu,
      String platform,
      String os,
      String system,
      String bits) {
    return productInstanceDao.findByProductNameAndVersionAndCpuAndPlatformAndOsAndSystemAndBits(
        name, version, cpu, platform, os, system, bits);
  }

  public List<ProductInstance> findByNameAndVersionAndCpuAndPlatformAndOsAndBits(
      String name, String version, String cpu, String platform, String os, String bits) {
    return productInstanceDao.findByProductNameAndVersionAndCpuAndPlatformAndOsAndBits(
        name, version, cpu, platform, os, bits);
  }

  public void removeByName(String name) {
    productInstanceDao.removeByProductName(name);
  }

  public void removeByNameAndVersion(String name, String version) {
    productInstanceDao.removeByProductNameAndVersion(name, version);
  }

  public List<String> getVersionsByProductName(String name) {
    return productInstanceDao.getVersionsByProductName(name);
  }

  public List<ProductInstance> findByNameAndVersionAndCpu(String name, String version, String cpu) {
    return productInstanceDao.findByProductNameAndVersionAndCpu(name, version, cpu);
  }

  public void save(ProductInstance instance) {
    productInstanceDao.save(instance);
  }

  public ProductInstance findById(Integer fromId) {
    return productInstanceDao.findById(fromId).orElse(null);
  }
}
