/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.service;

import com.intel.iot.ams.entity.ProductDownloadPackage;
import com.intel.iot.ams.repository.ProductDownloadPackageDao;
import com.intel.iot.ams.repository.hibernate.ProductDownloadPackageDaoImpl;


public class ProductDownloadPackageService extends BaseService<Integer, ProductDownloadPackage> {

  private ProductDownloadPackageDao productDownloadPackageDao;

  public ProductDownloadPackageService() {
    productDownloadPackageDao = new ProductDownloadPackageDaoImpl();
    super.setDao(productDownloadPackageDao);
  }

  public ProductDownloadPackage findByHashCode(String hash) {

    return productDownloadPackageDao.findByHashCode(hash);
  }

  public ProductDownloadPackage findByProductNameAndFromIdAndToId(String name, Integer fromId,
                                                                  Integer toId) {

    return productDownloadPackageDao.findByProductNameAndFromIdAndToId(name, fromId, toId);
  }

  public ProductDownloadPackage
      findByProductNameAndFromIdAndToIdAndIsAot(String name, Integer fromId, Integer toId,
                                                Boolean isAot) {

    return productDownloadPackageDao.findByProductNameAndFromIdAndToIdAndIsAot(name,
                                                                               fromId,
                                                                               toId,
                                                                               isAot);
  }

}
