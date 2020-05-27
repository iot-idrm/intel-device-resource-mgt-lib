/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ProductDownloadPackage;

public interface ProductDownloadPackageDao extends Dao<Integer, ProductDownloadPackage> {

  public ProductDownloadPackage findByHashCode(String hash);

  public ProductDownloadPackage findByProductNameAndFromIdAndToId(String name, Integer fromId,
                                                                  Integer toId);

  public ProductDownloadPackage
      findByProductNameAndFromIdAndToIdAndIsAot(String name, Integer fromId, Integer toId,
                                                Boolean isAot);
}
