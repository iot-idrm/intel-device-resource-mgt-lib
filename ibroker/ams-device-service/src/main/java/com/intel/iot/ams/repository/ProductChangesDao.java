/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.repository;

import com.intel.iot.ams.entity.ProductChanges;
import java.util.Date;
import java.util.List;

public interface ProductChangesDao extends Dao<Integer, ProductChanges> {

  public List<ProductChanges> findByClientUUID(String uuid);

  public void removeByClientUuid(String clientUuid);

  public void removeByClientUuidAndProductName(String clientUuid, String productName);

  public Date getMaxEnableTime();

  public int getEnableTimeCount(Date time);
}
