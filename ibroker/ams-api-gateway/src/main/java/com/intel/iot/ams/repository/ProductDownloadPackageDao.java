/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ProductDownloadPackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDownloadPackageDao extends JpaRepository<ProductDownloadPackage, Integer> {

  public ProductDownloadPackage findByHashcode(String hash);

  public ProductDownloadPackage findByProductNameAndFromIdAndToId(
      String name, Integer fromId, Integer toId);
}
