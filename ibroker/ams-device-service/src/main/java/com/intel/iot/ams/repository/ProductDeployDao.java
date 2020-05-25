/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ProductDeploy;
import java.util.List;

public interface ProductDeployDao extends Dao<Integer, ProductDeploy> {

  public List<ProductDeploy> findByClientUUID(String uuid);

  public List<ProductDeploy> findByDeviceId(String deviceId);

  public ProductDeploy findByClientUuidAndProductName(String clientUuid, String productName);

}
