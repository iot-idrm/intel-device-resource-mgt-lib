/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.ProductDownloadPackage;
import com.intel.iot.ams.repository.ProductDownloadPackageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductDownloadPackageService extends BaseService<Integer, ProductDownloadPackage> {

  @Autowired private ProductDownloadPackageDao productDownloadPackageDao;

  public ProductDownloadPackage findByHashCode(String hash) {

    return productDownloadPackageDao.findByHashcode(hash);
  }

  public ProductDownloadPackage findByProductNameAndFromIdAndToId(
      String name, Integer fromId, Integer toId) {

    return productDownloadPackageDao.findByProductNameAndFromIdAndToId(name, fromId, toId);
  }
}
