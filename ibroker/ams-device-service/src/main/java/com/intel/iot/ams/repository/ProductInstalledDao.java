/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ProductInstalled;
import java.util.List;

public interface ProductInstalledDao extends Dao<Integer, ProductInstalled> {

  public List<ProductInstalled> findByClientUUID(String uuid);

  public ProductInstalled findByClientUuidAndProductName(String clientUuid, String productName);

  public void removeByClientUuid(String clientUuid);
}
